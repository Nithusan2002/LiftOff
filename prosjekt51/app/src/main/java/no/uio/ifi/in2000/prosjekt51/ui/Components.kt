package no.uio.ifi.in2000.prosjekt51.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import no.uio.ifi.in2000.prosjekt51.DEFAULT_COORDS

// Structural

@Composable
fun LabeledDivider(
    label: String,
    modifier: Modifier = Modifier,
    /* You can add additional parameters for text style, padding, etc, as needed */
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        HorizontalDivider(modifier = Modifier.width(10.dp))
        Text(
            text = label,
            modifier = Modifier.padding(start = 8.dp,end = 8.dp)
        )
        HorizontalDivider(
            modifier = Modifier.weight(1f)
        )
    }
}


@Composable
fun BottomNavigation(navController: NavController){
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),  // TODO: Same height on all screens
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        IconButton(onClick = { navController.navigate("searchScreen/$DEFAULT_COORDS/$DEFAULT_COORDS") }) {
            Icon(Icons.Filled.Search, contentDescription = "Search")
        }
        IconButton(onClick = { navController.navigate("mapScreen") }) {
            Icon(Icons.Filled.Place, contentDescription = "Map")
        }
        IconButton(onClick = { navController.navigate("favoritesScreen") }) {
            Icon(Icons.Filled.Star, contentDescription = "Favourites")
        }
        IconButton(onClick = { /* Placeholder action */ }) {
            Icon(Icons.Filled.Settings, contentDescription = "Settings")
        }
    }
}


// Data visualisation

data class LaunchWindow(val time: String, val color: Color, val textColor: Color)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun LaunchWindows(
    data: List<LaunchWindow>,
    onWindowClick: (hour: String, date: String) -> Unit // Callback for navigation
) {
    val cells = mutableListOf<MutableList<LaunchWindow>>(ArrayList())
    var currentDay = 0
    data.forEach { launchWindow ->
        val hour = launchWindow.time.substring(11,13)
        if (hour == "00") {
            currentDay++
            cells.add(ArrayList())
        }
        cells[currentDay].add(launchWindow)
    }

    Column {
        cells.forEach { day ->
            Text(text = day.first().time.substring(0,10))
            FlowRow {
                day.forEach { window ->
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .background(color = window.color)
                            .clickable {
                            onWindowClick(window.time.substring(11,13), window.time.substring(0,10))
                        }
                    ) {
                        Text(text = window.time.substring(11,13), modifier = Modifier.align(Alignment.Center), color = window.textColor)
                    }
                }
            }
            Spacer(modifier = Modifier.size(16.dp)) // Padding between days
        }
    }
}
