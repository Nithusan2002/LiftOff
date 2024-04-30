package no.uio.ifi.in2000.prosjekt51.ui.map

import android.os.Bundle
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.foundation.layout.*
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
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import no.uio.ifi.in2000.prosjekt51.ui.favorites.DatabaseManager
import no.uio.ifi.in2000.prosjekt51.ui.favorites.Favorite
import no.uio.ifi.in2000.prosjekt51.ui.favorites.FavoriteViewModel
import no.uio.ifi.in2000.prosjekt51.ui.BottomNavigation

@Composable
fun MapScreen(navController: NavController) {
    var selectedLatLng by remember { mutableStateOf<LatLng?>(null) } // State to hold selected coordinates
    var showSaveButton by remember { mutableStateOf(false) }  // State to control button visibility
    var showSaveDialog by remember { mutableStateOf(false) }

    if (showSaveDialog) {
        SaveToFavoritesDialog(selectedLatLng!!)
    }


    Scaffold(
        topBar = {
            Column(
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (showSaveButton && selectedLatLng != null) {
                    Button(
                        onClick = { showSaveDialog = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    ) {
                        Text("Save position to favourites")
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
            MapViewWithState { latLng ->
                selectedLatLng = latLng
                showSaveButton = true

            }
        }
    }
}

@Composable
fun MapViewWithState(onLocationSelected: (LatLng) -> Unit) {
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
fun SaveToFavoritesDialog(latlon: LatLng) {
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

    // Collect the list of favorites from the ViewModel.
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
                        showDialog = false
                    }
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        showDialog = false
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}