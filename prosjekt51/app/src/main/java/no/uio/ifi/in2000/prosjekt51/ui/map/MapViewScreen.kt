import android.os.Bundle
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

@Composable
fun MapScreen(navController: NavController) {
    Scaffold(
        bottomBar = {
            BottomAppBar {
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
                        Icon(Icons.Filled.Star, contentDescription = "Favourites")
                    }
                    IconButton(onClick = { /* Placeholder action */ }) {
                        Icon(Icons.Filled.Settings, contentDescription = "Settings")
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
            val mapView = rememberMapViewWithLifecycle()
            AndroidView({ mapView }) { mapView ->
                mapView.getMapAsync { googleMap ->
                    googleMap.setOnMapClickListener { latLng ->
                        googleMap.clear()
                        googleMap.addMarker(
                            MarkerOptions().position(latLng).title("Selected Location")
                        )
                        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10f))
                        // You can implement additional functionality like showing a button or saving the location here.
                    }
                }
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
