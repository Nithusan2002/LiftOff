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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.prosjekt51.ui.BottomNavigation
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset


@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VisualResultScreen(
    latitude: String,
    longitude: String,
    date: Long,
    hour: Int,
    height: Double?,
    visualResultScreenViewModel: VisualResultScreenViewModel = viewModel(),
    onNavigateToHomeScreen: () -> Unit,
    navController: NavController,
    snackbarHostState: SnackbarHostState,
    onRetryClicked: () -> Unit,
    errorMessage: String?,
) {
    val visualResultScreenUiState: VisualResultScreenUiState by visualResultScreenViewModel.visualResultScreenUiState.collectAsState()

    LaunchedEffect(latitude, longitude, date, hour, height) {
        visualResultScreenViewModel.fetchData(
            lat = latitude.toDouble(),
            lon = longitude.toDouble(),
            date = date,
            hour = hour,
            height = height
        )
    }

    val scope = rememberCoroutineScope()

    if (visualResultScreenUiState.hasError) {
        LaunchedEffect(snackbarHostState) {
            scope.launch {
                val result = snackbarHostState.showSnackbar(
                    message = "En feil oppstod",
                    actionLabel = "Prøv igjen",
                    duration = SnackbarDuration.Indefinite
                )
                when (result) {
                    SnackbarResult.ActionPerformed -> {
                        onRetryClicked()
                    }

                    else -> {}
                }
            }
        }
    }

    var time by rememberSaveable { mutableStateOf("$hour:00") }
    var dropdownDate by rememberSaveable {
        mutableStateOf(
            Instant.ofEpochMilli(date).toString().take(10)
        )
    }
    var timeexpanded by rememberSaveable { mutableStateOf(false) }
    var dateexpanded by rememberSaveable { mutableStateOf(false) }
    var displayState by rememberSaveable { mutableStateOf(DisplayStates.TOTAL) }


    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        topBar = {
            TopAppBar(
                title = {
                    var expanded by rememberSaveable { mutableStateOf(false) }
                    IconButton(onClick = { expanded = true }) {
                        Icon(Icons.Filled.ArrowDropDown, contentDescription = null)
                    }
                    Row() {
                        ExposedDropdownMenuBox(
                            expanded = timeexpanded,
                            onExpandedChange = { timeexpanded = !timeexpanded },
                            modifier = Modifier
                                .padding(start = 4.dp, end = 4.dp, bottom = 1.dp)
                                .width(160.dp)
                                .height(56.dp)
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
                                textStyle = TextStyle(fontSize = 22.sp)
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
                                            navController.navigate("resultScreen/$latitude/$longitude/$date/$hour/$height")
                                            timeexpanded = false
                                        },
                                        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                                    )
                                }
                            }
                        }
                        ExposedDropdownMenuBox(
                            expanded = dateexpanded,
                            onExpandedChange = { dateexpanded = !dateexpanded },
                            modifier = Modifier
                                .padding(start = 4.dp, end = 4.dp, bottom = 1.dp)
                                .width(160.dp)
                                .height(56.dp)
                        ) {
                            TextField(
                                modifier = Modifier
                                    .menuAnchor()
                                    .fillMaxWidth(),
                                readOnly = true,
                                value = dropdownDate,
                                onValueChange = {
                                    dropdownDate = it
                                },
                                label = { Text(text = "Launch date") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = dateexpanded) },
                                colors = ExposedDropdownMenuDefaults.textFieldColors(),
                                textStyle = TextStyle(fontSize = 16.sp)
                            )

                            ExposedDropdownMenu(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(130.dp),  // Set a fixed height to ensure only part of the list is visible
                                expanded = dateexpanded,
                                onDismissRequest = { dateexpanded = false }
                            ) {
                                (0..7).forEach { day ->
                                    val nextday = LocalDateTime.now().plusDays(day.toLong())
                                    val dateText = "${nextday.dayOfMonth}.${
                                        "${nextday.monthValue}".padStart(
                                            3,
                                            0.toChar()
                                        )
                                    }.${nextday.year}"
                                    DropdownMenuItem(
                                        text = { Text(dateText) },
                                        onClick = {
                                            dropdownDate = dateText
                                            val dateAtMidnight =
                                                nextday.toLocalDate().atStartOfDay()
                                            val newdate =
                                                dateAtMidnight.toInstant(ZoneOffset.UTC)
                                                    .toEpochMilli()
                                            navController.navigate("resultScreen/$latitude/$longitude/$newdate/$hour/$height")
                                            dateexpanded = false
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
                },
            )
        }
    ) { innerPadding ->
        if (visualResultScreenUiState.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize().padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
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
                        Text(
                            "Wind",
                            color = if (visualResultScreenUiState.windCondition) Color.Green else Color.Red
                        )
                        Text(
                            "Sight",
                            color = if (visualResultScreenUiState.sightCondition) Color.Green else Color.Red
                        )
                        Text(
                            "Precipitation",
                            color = if (visualResultScreenUiState.precipitationCondition) Color.Green else Color.Red
                        )
                        Text(
                            "Air",
                            color = if (visualResultScreenUiState.airCondition) Color.Green else Color.Red
                        )
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
}



enum class DisplayStates {
    TOTAL,
    WIND,
    SIGHT,
    PRECIPITATION,
    AIR
}

