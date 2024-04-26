package no.uio.ifi.in2000.prosjekt51.ui.result

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.prosjekt51.model.isobaricGrib.GribPoint
import no.uio.ifi.in2000.prosjekt51.model.locationForecast.TimeAndData
import no.uio.ifi.in2000.prosjekt51.ui.result.scripts.pressureToHeight
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
    navController: NavController,
    snackbarHostState: SnackbarHostState,
    onRetryClicked: () -> Unit,
    errorMessage: String?
) {
    val visualResultScreenUiState: VisualResultScreenUiState by visualResultScreenViewModel.visualResultScreenUiState.collectAsState()

    visualResultScreenViewModel.fetchData(
        lat = latitude.toDouble(),
        lon = longitude.toDouble(),
        date = date,
        hour = hour
    )


    val scope = rememberCoroutineScope()

    if (visualResultScreenUiState.hasError) {
        LaunchedEffect(snackbarHostState) {
            scope.launch {
                val result = snackbarHostState.showSnackbar(
                    message = "En feil oppstod",
                    actionLabel = "Prøv igjen",
                    duration = SnackbarDuration.Indefinite
                )
                when(result){
                    SnackbarResult.ActionPerformed -> {
                        onRetryClicked()
                    }
                    else -> {}
                }
            }
        }
    }

    var time by remember { mutableStateOf("${(LocalDateTime.now().hour + 1).mod(24)}:00") }
    var timeexpanded by remember { mutableStateOf(false) }
    var displayState by remember { mutableStateOf(DisplayStates.TOTAL)}

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
                                onValueChange = {
                                    time = it
                                                },
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
                                            navController.navigate("resultScreen/$latitude/$longitude/$date/$hour")
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
                    IconButton(onClick = { navController.navigate("searchScreen/-500/-500") }) {
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
            when (displayState) {
                DisplayStates.TOTAL -> {
                    SummaryDisplay(
                        visualResultScreenViewModel = visualResultScreenViewModel,
                        visualResultScreenUiState = visualResultScreenUiState,
                        enterFuncWind = { displayState = DisplayStates.WIND },
                        enterFuncSight = { displayState = DisplayStates.SIGHT },
                        enterFuncPrecipitation = { displayState = DisplayStates.PRECIPITATION },
                        enterFuncAir = { displayState = DisplayStates.AIR },
                        lat = latitude.toDouble(),
                        lon = longitude.toDouble()
                    )
                }
                DisplayStates.WIND -> {
                    WindDisplay(
                        exitFunc = { displayState = DisplayStates.TOTAL },
                        data = visualResultScreenUiState.currentLocationForecastData,
                        gribPoints = visualResultScreenUiState.currentGribData,
                        groundPressure = visualResultScreenUiState.currentLocationForecastData?.data?.air_pressure_at_sea_level,
                        groundTemp = visualResultScreenUiState.currentLocationForecastData?.data?.air_temperature
                    )
                }
                DisplayStates.SIGHT -> {
                    SightDisplay(
                        exitFunc = { displayState = DisplayStates.TOTAL },
                        data = visualResultScreenUiState.currentLocationForecastData
                    )

                }
                DisplayStates.PRECIPITATION -> {
                    PrecipitationDisplay(
                        exitFunc = { displayState = DisplayStates.TOTAL },
                        data = visualResultScreenUiState.currentLocationForecastData
                    )
                }
                DisplayStates.AIR -> {
                    AirDisplay(
                        exitFunc = { displayState = DisplayStates.TOTAL },
                        data = visualResultScreenUiState.currentLocationForecastData
                    )
                }
            }
        }
    }
}

@Composable
fun SummaryDisplay(visualResultScreenViewModel: VisualResultScreenViewModel, visualResultScreenUiState: VisualResultScreenUiState,
                   enterFuncWind: () -> Unit,
                   enterFuncSight: () -> Unit,
                   enterFuncPrecipitation: () -> Unit,
                   enterFuncAir: () -> Unit,
                   lat: Double,
                   lon: Double
                   ) {
    Column(modifier = Modifier
        .padding(8.dp)
        .fillMaxWidth()
        .fillMaxHeight()) {
        WindSection(enterFunc = enterFuncWind, data = visualResultScreenUiState.currentLocationForecastData, visualResultScreenViewModel = visualResultScreenViewModel, lat = lat, lon = lon)
        SightSection(enterFunc = enterFuncSight, data = visualResultScreenUiState.currentLocationForecastData)
        PrecipitationSection(enterFunc = enterFuncPrecipitation, data = visualResultScreenUiState.currentLocationForecastData)
        AirSection(enterFunc = enterFuncAir, data = visualResultScreenUiState.currentLocationForecastData)

    }
}

@Composable
fun WindDisplay(exitFunc: () -> Unit, data: TimeAndData?, gribPoints: List<GribPoint>?, groundPressure: Double?, groundTemp: Double?){
    val safeGribPoints = gribPoints ?: emptyList()

    Box(modifier = Modifier.fillMaxSize()) {
        // Positioning the close icon in the top right corner
        IconButton(
            onClick = { exitFunc() },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
        ) {
            Icon(Icons.Filled.Close, contentDescription = "Close")
        }

        Column(modifier = Modifier
            .fillMaxSize()
            .padding(top = 48.dp)) {
            Text(
                text = "Wind speed of gust: ${data?.data?.wind_speed_of_gust} m/s",
                fontSize = 20.sp,
                modifier = Modifier.padding(16.dp)
            )
            LazyColumn(modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)) {
                items(safeGribPoints) { gribPoint ->
                    GribPointItems(gribPoint, groundPressure, groundTemp)
                }
            }
        }
    }
}

@Composable
fun SightDisplay(exitFunc: () -> Unit, data: TimeAndData?){
    Box(modifier = Modifier.fillMaxSize()) {
        // Positioning the close icon in the top right corner
        IconButton(
            onClick = { exitFunc() },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
        ) {
            Icon(Icons.Filled.Close, contentDescription = "Close")
        }

        Column(modifier = Modifier
            .fillMaxSize()
            .padding(top = 48.dp)) {
            Text(
                text = "Cloud area fraction (high): ${data?.data?.cloud_area_fraction_high} %",
                fontSize = 20.sp,
                modifier = Modifier.padding(16.dp)
            )
            Text(
                text = "Cloud area fraction (medium): ${data?.data?.cloud_area_fraction_medium} %",
                fontSize = 20.sp,
                modifier = Modifier.padding(16.dp)
            )
            Text(
                text = "Cloud area fraction (low): ${data?.data?.cloud_area_fraction_low} %",
                fontSize = 20.sp,
                modifier = Modifier.padding(16.dp)
            )
            Text(
                text = "Fog area fraction: ${data?.data?.fog_area_fraction} %",
                fontSize = 20.sp,
                modifier = Modifier.padding(16.dp)
            )
            Text(
                text = "Cloud area fraction: ${data?.data?.cloud_area_fraction} %",
                fontSize = 20.sp,
                modifier = Modifier.padding(16.dp)
            )
            Text(
                text = "UV-index (clear sky): ${data?.data?.ultraviolet_index_clear_sky} ",
                fontSize = 20.sp,
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

@Composable
fun PrecipitationDisplay(exitFunc: () -> Unit, data: TimeAndData?){
    Box(modifier = Modifier.fillMaxSize()) {
        // Positioning the close icon in the top right corner
        IconButton(
            onClick = { exitFunc() },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
        ) {
            Icon(Icons.Filled.Close, contentDescription = "Close")
        }

        Column(modifier = Modifier
            .fillMaxSize()
            .padding(top = 48.dp)) {
            Text(
                text = "Precipitation: ${data?.data?.precipitation_amount} mm",
                fontSize = 20.sp,
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

@Composable
fun AirDisplay(exitFunc: () -> Unit, data: TimeAndData?){
    Box(modifier = Modifier.fillMaxSize()) {
        // Positioning the close icon in the top right corner
        IconButton(
            onClick = { exitFunc() },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
        ) {
            Icon(Icons.Filled.Close, contentDescription = "Close")
        }

        Column(modifier = Modifier
            .fillMaxSize()
            .padding(top = 48.dp)) {
            Text(
                text = "Dew point temperature: ${data?.data?.dew_point_temperature} °C",
                fontSize = 20.sp,
                modifier = Modifier.padding(16.dp)
            )
            Text(
                text = "Relative humidity: ${data?.data?.relative_humidity} %",
                fontSize = 20.sp,
                modifier = Modifier.padding(16.dp)
            )
            Text(
                text = "Air temperature: ${data?.data?.air_temperature} °C",
                fontSize = 20.sp,
                modifier = Modifier.padding(16.dp)
            )
            Text(
                text = "Air temperature (10th percentile): ${data?.data?.air_temperature_percentile_10} °C",
                fontSize = 20.sp,
                modifier = Modifier.padding(16.dp)
            )
            Text(
                text = "Air temperature (90th percentile): ${data?.data?.air_temperature_percentile_90} °C",
                fontSize = 20.sp,
                modifier = Modifier.padding(16.dp)
            )
            Text(
                text = "Air pressure: ${data?.data?.air_pressure_at_sea_level} mB", // TODO: Er det millibar?
                fontSize = 20.sp,
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}




@Composable
fun WindSection(enterFunc: () -> Unit, data: TimeAndData?, visualResultScreenViewModel: VisualResultScreenViewModel, lat: Double, lon: Double) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp),
        onClick = { enterFunc() }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Wind", style = MaterialTheme.typography.titleMedium)
            if (lat > 64.25 || lat < 55.35 || lon > 14.51 || lon < -1.45) {
                Text(text = "Note: Wind data may not be available for coordinates outside southern Norway", style = MaterialTheme.typography.titleSmall, color = Color.DarkGray)
            }
            Text("Wind speed of gust: ${data?.data?.wind_speed_of_gust} m/s")
            Text("Maximum wind-speed in athmosphere: ${visualResultScreenViewModel.findMaximumAirWindSpeed()} %")
            Text("Maximum wind-shear in athmosphere: ${visualResultScreenViewModel.findMaximumWindShear()} %")

        }
    }
}

@Composable
fun SightSection(enterFunc: () -> Unit, data: TimeAndData?) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp),
        onClick = { enterFunc() }
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
fun PrecipitationSection(enterFunc: () -> Unit, data: TimeAndData?) {
    Card (
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp),
        onClick = { enterFunc() }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Precipitation", style = MaterialTheme.typography.titleMedium)
            Text("Precipitation amount: ${data?.data?.precipitation_amount} mm") // TODO: Doesn't work?
        }
    }
}

@Composable
fun AirSection(enterFunc: () -> Unit, data: TimeAndData?) {
    Card (
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp),
        onClick = { enterFunc() }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Air", style = MaterialTheme.typography.titleMedium)
            Text("Dew point temperature: ${data?.data?.dew_point_temperature} °C")
            Text("Relative humidity: ${data?.data?.relative_humidity} %")
        }
    }
}

@Composable
fun GribPointItems(gribPoint: GribPoint, groundPressure: Double?, groundTemp: Double?) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = "Height: ${pressureToHeight(gribPoint.height, groundPressure, groundTemp)} meters")
        Text(text = "Wind: ${gribPoint.wind}")
        Text(text = "Temperature: ${gribPoint.temperature}")
        Text(text = "Wind-Shear: ${gribPoint.windshear}")

    }
}


enum class DisplayStates {
    TOTAL,
    WIND,
    SIGHT,
    PRECIPITATION,
    AIR
}

