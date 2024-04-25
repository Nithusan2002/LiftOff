import android.os.Bundle
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import no.uio.ifi.in2000.prosjekt51.ui.map.MapViewScreen
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp

@Composable
fun MapScreen(navController: NavController) {
    var selectedLatLng by remember { mutableStateOf<LatLng?>(null) } // State to hold selected coordinates
    var showSaveButton by remember { mutableStateOf(false) }  // State to control button visibility

    Scaffold(
        bottomBar = {
            BottomAppBar(
                modifier = Modifier.height(if (showSaveButton && selectedLatLng != null) 122.dp else 56.dp) // TODO: Add height so bottom bar looks unchanged
            ) {
                Column(
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (showSaveButton && selectedLatLng != null) {
                        Button(
                            onClick = { /* Currently does nothing, but could save to favorites */ },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                        ) {
                            Text("Save position to favourites")
                        }
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        IconButton(onClick = { navController.navigate("searchScreen") }) {
                            Icon(Icons.Filled.Search, contentDescription = "Search")
                        }
                        IconButton(onClick = { navController.navigate("mapScreen") }) {
                            Icon(Icons.Filled.Place, contentDescription = "Map")
                        }
                        IconButton(onClick = { /* Placeholder action */ }) {
                            Icon(Icons.Filled.Star, contentDescription = "Favourites")  // TODO: Favourites or Favorites or Favoritter?
                        }
                        IconButton(onClick = { /* Placeholder action */ }) {
                            Icon(Icons.Filled.Settings, contentDescription = "Settings")
                        }
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
            MapViewWithState(showSaveButton, selectedLatLng, { latLng ->
                selectedLatLng = latLng
                showSaveButton = true
                Log.d("MapScreen", "LatLng selected: $latLng")  // Debugging log
                Log.d("MapScreen", "showSaveButton = $showSaveButton")
            })
        }
    }
}

@Composable
fun MapViewWithState(showSaveButton: Boolean, selectedLatLng: LatLng?, onLocationSelected: (LatLng) -> Unit) {
    val mapView = rememberMapViewWithLifecycle()
    AndroidView({ mapView }) { mapView ->
        mapView.getMapAsync { googleMap ->
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
            getMapAsync(OnMapReadyCallback { googleMap ->
                MapViewScreen()
            })
        }
    }
    DisposableEffect(mapView) {
        onDispose {
            mapView.onDestroy()
        }
    }
    return mapView
}
