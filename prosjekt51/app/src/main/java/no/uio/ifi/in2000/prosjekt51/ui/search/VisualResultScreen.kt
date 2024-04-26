package no.uio.ifi.in2000.prosjekt51.ui.search

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import no.uio.ifi.in2000.prosjekt51.R
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.getValue
import androidx.navigation.NavController
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import no.uio.ifi.in2000.prosjekt51.model.isobaricGrib.GribJson
import no.uio.ifi.in2000.prosjekt51.model.isobaricGrib.GribPoint
import no.uio.ifi.in2000.prosjekt51.model.locationForecast.TimeAndData
import no.uio.ifi.in2000.prosjekt51.ui.result.ResultScreenUiState
import java.time.LocalDateTime


@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VisualResultScreen(
    latitude: String,
    longitude: String,
    date: Long,
    hour: Int,
    visualResultScreenViewModel: VisualResultScreenViewModel = viewModel(),
    onNavigateToHomeScreen: () -> Unit,
    navController: NavController
) {
    val visualResultScreenUiState: VisualResultScreenUiState by visualResultScreenViewModel.visualResultScreenUiState.collectAsState()

    visualResultScreenViewModel.fetchData(
        lat = latitude.toDouble(),
        lon = longitude.toDouble(),
        date = date,
        hour = hour
    )


    val scope = rememberCoroutineScope()

    var time by remember { mutableStateOf("${(LocalDateTime.now().hour + 1).mod(24)}:00") }
    var timeexpanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    var expanded by remember { mutableStateOf(false) }
                    IconButton(onClick = { expanded = true }) {
                        Icon(Icons.Filled.ArrowDropDown, contentDescription = null)
                        ExposedDropdownMenuBox(
                            expanded = timeexpanded,
                            onExpandedChange = { timeexpanded = !timeexpanded },
                            modifier = Modifier.padding(start = 8.dp, end = 8.dp, bottom = 2.dp)
                        ) {
                            TextField(
                                modifier = Modifier
                                    .menuAnchor()
                                    .fillMaxWidth(),
                                readOnly = true,
                                value = time,
                                onValueChange = { time = it },
                                label = { Text(text = "Launch time") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = timeexpanded) },
                                colors = ExposedDropdownMenuDefaults.textFieldColors(),
                            )

                            ExposedDropdownMenu(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(130.dp),  // Set a fixed height to ensure only part of the list is visible
                                expanded = timeexpanded,
                                onDismissRequest = { timeexpanded = false }
                            ) {
                                (0..23).forEach { hour ->
                                    val hourText = "%02d:00".format(hour)
                                    DropdownMenuItem(
                                        text = { Text(hourText) },
                                        onClick = {
                                            time = hourText
                                            timeexpanded = false
                                        },
                                        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                                    )
                                }
                            }
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { onNavigateToHomeScreen() }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        },
        bottomBar = {
            BottomAppBar {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    IconButton(onClick = { navController.navigate("searchScreen/-1/-1") }) {
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
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxHeight()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (
                    visualResultScreenUiState.windCondition
                    && visualResultScreenUiState.sightCondition
                    && visualResultScreenUiState.precipitationCondition
                    && visualResultScreenUiState.airCondition
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.checkmark),
                        contentDescription = "Checkmark",
                        modifier = Modifier.size(100.dp)
                    )
                } else {
                    Image(
                        painter = painterResource(id = R.drawable.weirdx),
                        contentDescription = "X",
                        modifier = Modifier.size(100.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text("Wind", color = if (visualResultScreenUiState.windCondition) Color.Green else Color.Red)
                    Text("Sight", color = if (visualResultScreenUiState.sightCondition) Color.Green else Color.Red)
                    Text("Precipitation", color = if (visualResultScreenUiState.precipitationCondition) Color.Green else Color.Red)
                    Text("Air", color = if (visualResultScreenUiState.airCondition) Color.Green else Color.Red)
                }
            }
            Divider()
            Column(modifier = Modifier.padding(8.dp).fillMaxWidth()) {
                WindSection(data = visualResultScreenUiState.currentLocationForecastData, visualResultScreenViewModel = visualResultScreenViewModel)
                SightSection(data = visualResultScreenUiState.currentLocationForecastData)
                PrecipitationSection(data = visualResultScreenUiState.currentLocationForecastData)
                AirSection(data = visualResultScreenUiState.currentLocationForecastData)

            }
        }
    }
}

@Composable
fun WindSection(data: TimeAndData?, visualResultScreenViewModel: VisualResultScreenViewModel) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(4.dp),
        onClick = { }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Wind", style = MaterialTheme.typography.titleMedium)
            Text("Wind speed of gust: ${data?.data?.wind_speed_of_gust} m/s")
            Text("Maximum wind-speed in athmosphere: ${visualResultScreenViewModel.findMaximumAirWindSpeed()} %")
            Text("Maximum wind-shear in athmosphere: ${visualResultScreenViewModel.findMaximumWindShear()} %")

        }
    }
}

@Composable
fun SightSection(data: TimeAndData?) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(4.dp),
        onClick = { }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Sight", style = MaterialTheme.typography.titleMedium)
            Text("Cloud area fraction (high): ${data?.data?.cloud_area_fraction_high} %")
            Text("Cloud area fraction (medium): ${data?.data?.cloud_area_fraction_medium} %")
            Text("Cloud area fraction (low): ${data?.data?.cloud_area_fraction_low} %")
        }
    }
}

@Composable
fun PrecipitationSection(data: TimeAndData?) {
    Card (
        modifier = Modifier.fillMaxWidth().padding(4.dp),
        onClick = { }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Precipitation", style = MaterialTheme.typography.titleMedium)
            Text("Precipitation amount: ${data?.data?.precipitation_amount} mm") // TODO: Doesn't work?
        }
    }
}

@Composable
fun AirSection(data: TimeAndData?) {
    Card (
        modifier = Modifier.fillMaxWidth().padding(4.dp),
        onClick = { }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Air", style = MaterialTheme.typography.titleMedium)
            Text("Dew point temperature: ${data?.data?.dew_point_temperature} °C")
            Text("Relative humidity: ${data?.data?.relative_humidity} %")
        }
    }
}

