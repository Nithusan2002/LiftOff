package no.uio.ifi.in2000.prosjekt51.ui.result

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.prosjekt51.model.isobaricGrib.GribPoint
import no.uio.ifi.in2000.prosjekt51.model.locationForecast.TimeAndData
import no.uio.ifi.in2000.prosjekt51.ui.result.scripts.pressureToHeight

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultScreen(
    latitude: String,
    longitude: String,
    date: Long,
    hour: Int,
    onNavigateToHomeScreen: () -> Unit,
    resultScreenViewModel: ResultScreenViewModel= viewModel(),
    snackbarHostState: SnackbarHostState,
    onRetryClicked: () -> Unit,
    errorMessage: String? // Parameter for feilmelding
) {
    val resultScreenUiState: ResultScreenUiState by resultScreenViewModel.resultScreenUiState.collectAsState()

    val launchCheckResultText: String = resultScreenViewModel.checkLaunchConditions(
        lat = latitude.toDouble(),
        lon = longitude.toDouble(),
        date = date,
        hour = hour
    )

    val scope = rememberCoroutineScope()

    if (resultScreenUiState.hasError) {
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
    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = { onNavigateToHomeScreen() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null
                        )
                    }
                }
            )
        }
    ) {innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            Column {
                Text(
                    "Resultat for ${latitude}° N ${longitude}° Ø",
                    fontSize = 20.sp
                )
                Text(
                    launchCheckResultText,
                    fontSize = 20.sp
                )
            }

            WeatherDataItem(data = resultScreenUiState.currentLocationForecastData)
            GribPointList(
                resultScreenUiState.currentGribData,
                resultScreenUiState.currentLocationForecastData?.data?.air_pressure_at_sea_level,
                resultScreenUiState.currentLocationForecastData?.data?.air_temperature
            )
        }
    }
}

@Composable
fun WeatherDataItem(data: TimeAndData?) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text("Time: ${data?.time}")
        Text("Wind speed of gust: ${data?.data?.wind_speed_of_gust} m/s")
        Text("Cloud area fraction (high): ${data?.data?.cloud_area_fraction_high} %")
        Text("Cloud area fraction (medium): ${data?.data?.cloud_area_fraction_medium} %")
        Text("Cloud area fraction (low): ${data?.data?.cloud_area_fraction_low} %")
        Text("Fog area fraction: ${data?.data?.fog_area_fraction} %")
        Text("Relative humidity: ${data?.data?.relative_humidity} %")
        Text("Dew point temperature: ${data?.data?.dew_point_temperature} °C")
        Text("Precipitation amount: ${data?.data?.precipitation_amount} mm")
    }
}

@Composable
fun GribPointList(gribPoints: List<GribPoint>?, ground_pressure: Double?, ground_temp: Double?) {
    Log.d("GribTesting", "INSIDE GRIBPOINTLIST: gribpoints: $gribPoints")
    // If gribPoints is null, use an empty list instead
    val safeGribPoints = gribPoints ?: emptyList()

    LazyColumn {
        items(safeGribPoints) { gribPoint ->
            GribPointItem(gribPoint, ground_pressure, ground_temp)
        }
    }
}

@Composable
fun GribPointItem(gribPoint: GribPoint, ground_pressure: Double?, ground_temp: Double?) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = "Height: ${pressureToHeight(gribPoint.height, gribPoint.temperature, ground_pressure, ground_temp)}")
        Text(text = "U-component of wind: ${gribPoint.uComponent}")
        Text(text = "V-component of wind: ${gribPoint.vComponent}")
        Text(text = "Temperature: ${gribPoint.temperature}")

    }
}