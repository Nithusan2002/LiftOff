package no.uio.ifi.in2000.prosjekt51.ui.result

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.prosjekt51.ui.LabeledDivider
import no.uio.ifi.in2000.prosjekt51.ui.theme.badConditionsContainerLight
import no.uio.ifi.in2000.prosjekt51.ui.theme.edgeConditionsContainerLight
import no.uio.ifi.in2000.prosjekt51.ui.theme.goodConditionsContainerLight
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VisualResultScreen(
    latitude: String,
    longitude: String,
    date: Long,
    hour: String,
    height: Double?,
    resultScreenViewModel: ResultScreenViewModel = viewModel(),
    onNavigateToHomeScreen: () -> Unit,
    navController: NavController,
    snackbarHostState: SnackbarHostState,
    onRetryClicked: () -> Unit,
    onNavigateToResultScreen: (String, String, Long, String) -> Unit
) {
    val visualResultScreenUiState: VisualResultScreenUiState by resultScreenViewModel.visualResultScreenUiState.collectAsState()

    LaunchedEffect(latitude, longitude, date, hour, height) {
        resultScreenViewModel.fetchData(
            lat = latitude.toDouble(),
            lon = longitude.toDouble(),
            date = date,
            hour = hour,
            height = height
        )
    }

    resultScreenViewModel.fetchLaunchWindows()


    val scope = rememberCoroutineScope()

    if (visualResultScreenUiState.hasError) {
        LaunchedEffect(snackbarHostState) {
            scope.launch {
                val result = snackbarHostState.showSnackbar(
                    message = "An error occurred",
                    actionLabel = "Try again",
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
            CenterAlignedTopAppBar(
                title = { Text(text = "Result")
                },
                navigationIcon = {
                    IconButton(onClick = { onNavigateToHomeScreen() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
            )
        }
    ) { innerPadding ->
        // Display loading wheel if data isn't yet loaded
        if (visualResultScreenUiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxHeight()
                    .verticalScroll(rememberScrollState())
            ) {
                LabeledDivider(label = "Time and date")
                Row {
                    ExposedDropdownMenuBox(
                        expanded = timeexpanded,
                        onExpandedChange = { timeexpanded = !timeexpanded },
                        modifier = Modifier
                            .padding(start = 4.dp, end = 4.dp, bottom = 1.dp)
                            .weight(1f)
                            .fillMaxWidth()
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
                                .height(200.dp)
                                .fillMaxWidth(fraction = 0.65f),
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
                            .weight(1f)
                            .fillMaxWidth()
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
                                .fillMaxWidth(fraction = 0.65f)
                                .height(200.dp),
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
                LabeledDivider(label = "Recommendation")
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Wind",
                            color = if (visualResultScreenUiState.windCondition == 0) Color.Green else if (visualResultScreenUiState.windCondition == 1) Color.Yellow else Color.Red,
                            style = TextStyle(
                                shadow = Shadow(
                                    color = Color.Black,
                                    offset = Offset(2f, 2f),
                                    blurRadius = 2f
                                ),
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold

                            )
                        )
                        Text("Sight",
                            color = if (visualResultScreenUiState.sightCondition == 0) Color.Green else if (visualResultScreenUiState.sightCondition == 1) Color.Yellow else Color.Red,
                            style = TextStyle(
                                shadow = Shadow(
                                    color = Color.Black,
                                    offset = Offset(2f, 2f),
                                    blurRadius = 2f
                                ),
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Text("Precipitation",
                            color = if (visualResultScreenUiState.precipitationCondition == 0) Color.Green else if (visualResultScreenUiState.precipitationCondition == 1) Color.Yellow else Color.Red,
                            style = TextStyle(
                                shadow = Shadow(
                                    color = Color.Black,
                                    offset = Offset(2f, 2f),
                                    blurRadius = 2f
                                ),
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )

                        Text("Air",
                            color = if (visualResultScreenUiState.airCondition == 0) Color.Green else if (visualResultScreenUiState.airCondition == 1) Color.Yellow else Color.Red,
                            style = TextStyle(
                                shadow = Shadow(
                                    color = Color.Black,
                                    offset = Offset(2f, 2f),
                                    blurRadius = 2f
                                ),
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )

                    }
                    if (
                        (visualResultScreenUiState.windCondition == 2) ||
                        (visualResultScreenUiState.sightCondition == 2) ||
                        (visualResultScreenUiState.precipitationCondition == 2) ||
                        (visualResultScreenUiState.airCondition == 2)
                    ) {
                        Icon(
                            Icons.Filled.Close,
                            contentDescription = "X to denote bad conditions",
                            modifier = Modifier.size(100.dp),
                            tint = badConditionsContainerLight
                        )
                    } else if (
                        (visualResultScreenUiState.windCondition == 0) &&
                        (visualResultScreenUiState.sightCondition == 0) &&
                        (visualResultScreenUiState.precipitationCondition == 0) &&
                        (visualResultScreenUiState.airCondition == 0)
                    ) {
                        Icon(
                            Icons.Filled.Check,
                            contentDescription = "Checkmark",
                            modifier = Modifier.size(100.dp),
                            tint = goodConditionsContainerLight
                        )
                    } else {
                        Icon(
                            Icons.Filled.Warning,
                            contentDescription = "Warning to indicate conditions that may require closer inspection",
                            modifier = Modifier.size(100.dp),
                            tint = edgeConditionsContainerLight
                        )
                    }
                }

                when (displayState) {
                    DisplayStates.TOTAL -> {
                        SummaryDisplay(
                            visualResultScreenUiState = visualResultScreenUiState,
                            enterFuncWind = { displayState = DisplayStates.WIND },
                            enterFuncSight = { displayState = DisplayStates.SIGHT },
                            enterFuncPrecipitation = { displayState = DisplayStates.PRECIPITATION },
                            enterFuncAir = { displayState = DisplayStates.AIR },
                            enterFuncLegal = {displayState = DisplayStates.LEGAL},
                            lat = latitude.toDouble(),
                            lon = longitude.toDouble(),
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
                    DisplayStates.LEGAL -> {
                        LegalDisplay(
                            exitFunc = {
                                displayState = DisplayStates.TOTAL
                            },
                        )
                    }
                }


                if (visualResultScreenUiState.launchWindowsData?.size != 0 && visualResultScreenUiState.launchWindowsData != null) {
                    LaunchWindows(
                        data = visualResultScreenUiState.launchWindowsData!!,
                        onWindowClick = {
                                hour: String,
                                date: String ->
                            onNavigateToResultScreen(
                                latitude,
                                longitude,
                                resultScreenViewModel.convertDateToEpochMilli(date),
                                hour)
                        }
                    )
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
    AIR,
    LEGAL
}
