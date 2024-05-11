package no.uio.ifi.in2000.prosjekt51.ui.map

import android.os.Bundle
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.AlertDialog
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import no.uio.ifi.in2000.prosjekt51.ui.favorites.DatabaseManager
import no.uio.ifi.in2000.prosjekt51.ui.favorites.Favorite
import no.uio.ifi.in2000.prosjekt51.ui.favorites.FavoriteViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(navController: NavController, viewModel: MapViewModel = viewModel()) {

    val showSaveButton = viewModel.showSaveButton.collectAsState().value
    val selectedLatLng = viewModel.selectedLatLng.collectAsState().value
    val showSaveDialog = viewModel.showSaveDialog.collectAsState().value

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(title = { Text(text = "Choose location") })
        },
        bottomBar = {
            Row {
                if (showSaveButton && selectedLatLng != null) {
                    Button(
                        onClick = { viewModel.toggleSaveDialog(true) },
                        shape = RoundedCornerShape(5.dp),
                        modifier = Modifier
                            .height(52.dp)
                            .fillMaxWidth()
                            .weight(1f)
                            .padding(5.dp),
                        contentPadding = PaddingValues(0.dp),
                    ) {
                        Text("Save position to favorites")
                    }
                    Button(
                        onClick = {
                            navController.navigate("searchScreen/${selectedLatLng.latitude}/${selectedLatLng.longitude}")
                        },
                        shape = RoundedCornerShape(5.dp),
                        modifier = Modifier
                            .height(52.dp)
                            .weight(1f)
                            .fillMaxWidth()
                            .padding(5.dp),
                        contentPadding = PaddingValues(0.dp),
                    ) {
                        Text("Search position")
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxHeight()
        ) {
            MapViewWithState(viewModel::selectLocation)
        }
    }

    if (showSaveDialog && selectedLatLng != null) {
        SaveToFavoritesDialog(selectedLatLng, viewModel)
    }
}
@Composable
fun MapViewWithState(onLocationSelected: (LatLng) -> Unit) {
    /*
    Helper function to display map with added marker at selected location. Also facilitates
    centering and zooming on the selected location.
    */
    val mapView = rememberMapViewWithLifecycle()
    AndroidView({ mapView }) { mv ->
        mv.getMapAsync { googleMap ->
            googleMap.setOnMapClickListener { latLng ->
                googleMap.clear()
                googleMap.addMarker(
                    MarkerOptions().position(latLng).title("Selected Location")
                )
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10f))
                onLocationSelected(latLng)
            }
        }
    }
}

@Composable
fun rememberMapViewWithLifecycle(): MapView {
    /*Initialize map view within lifecycle*/
    val context = LocalContext.current
    val mapView = remember {
        MapView(context).apply {
            onCreate(Bundle())
            onResume()
            getMapAsync { _ ->
                MapViewScreen()
            }
        }
    }
    DisposableEffect(mapView) {
        onDispose {
            mapView.onDestroy()
        }
    }
    return mapView
}


@Composable
fun SaveToFavoritesDialog(latlon: LatLng, mapViewModel: MapViewModel) {
    /*Pop up dialog to allow user to save selected location to favorites*/
    val context = LocalContext.current
    val viewModel: FavoriteViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val repository = DatabaseManager.getFavoriteRepository(context)
                if (modelClass.isAssignableFrom(FavoriteViewModel::class.java)) {
                    @Suppress("UNCHECKED_CAST")
                    return FavoriteViewModel(repository) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
    )

    var showDialog by remember { mutableStateOf(true) }
    var text by remember { mutableStateOf("") }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = {
                showDialog = false
            },
            title = { Text("Save to Favorites") },
            text = {
                TextField(
                    value = text,
                    onValueChange = { text = it },
                    label = { Text("Enter a name for the location") }
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.addFavorite(Favorite(lat = latlon.latitude, lon = latlon.longitude, name = text))
                        mapViewModel.toggleSaveDialog(false)
                    }
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        mapViewModel.toggleSaveDialog(false)
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}
