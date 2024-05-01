package no.uio.ifi.in2000.prosjekt51.ui.result

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Divider
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import no.uio.ifi.in2000.prosjekt51.R
import androidx.compose.material3.*
import androidx.compose.runtime.getValue
import androidx.navigation.NavController
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.prosjekt51.ui.BottomNavigation
import no.uio.ifi.in2000.prosjekt51.ui.LaunchWindow
import no.uio.ifi.in2000.prosjekt51.ui.LaunchWindows
import no.uio.ifi.in2000.prosjekt51.ui.theme.badConditionsContainerLight
import no.uio.ifi.in2000.prosjekt51.ui.theme.edgeConditionsContainerLight
import no.uio.ifi.in2000.prosjekt51.ui.theme.goodConditionsContainerLight
import no.uio.ifi.in2000.prosjekt51.ui.theme.onBadConditionsContainerLight
import no.uio.ifi.in2000.prosjekt51.ui.theme.onEdgeConditionsContainerLight
import no.uio.ifi.in2000.prosjekt51.ui.theme.onGoodConditionsContainerLight
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
    errorMessage: String?,
    onNavigateToResultScreen: (String, String, Long, Int) -> Unit
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
                BottomNavigation(navController = navController)
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

            val launchWindows = listOf(
                LaunchWindow(hour = 10, color = goodConditionsContainerLight, textColor = onGoodConditionsContainerLight),
                LaunchWindow(hour = 11, color = goodConditionsContainerLight, textColor = onGoodConditionsContainerLight),
                LaunchWindow(hour = 12, color = edgeConditionsContainerLight, textColor = onEdgeConditionsContainerLight),
                LaunchWindow(hour = 13, color = edgeConditionsContainerLight, textColor = onEdgeConditionsContainerLight),
                LaunchWindow(hour = 14, color = badConditionsContainerLight, textColor = onBadConditionsContainerLight),
                LaunchWindow(hour = 22, color = badConditionsContainerLight, textColor = onBadConditionsContainerLight),
                LaunchWindow(hour = 25, color = goodConditionsContainerLight, textColor = onGoodConditionsContainerLight),  // Next day data
                LaunchWindow(hour = 26, color = badConditionsContainerLight, textColor = onBadConditionsContainerLight),
                LaunchWindow(hour = 27, color = goodConditionsContainerLight, textColor = onGoodConditionsContainerLight),
            )

            
            LaunchWindows(data = launchWindows, lon = 48.3, lat = 39.5, onWindowClick = {onNavigateToResultScreen(latitude,longitude,date,it)})
        }
    }
}



enum class DisplayStates {
    TOTAL,
    WIND,
    SIGHT,
    PRECIPITATION,
    AIR
}

