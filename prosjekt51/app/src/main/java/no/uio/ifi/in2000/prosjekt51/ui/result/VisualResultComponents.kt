package no.uio.ifi.in2000.prosjekt51.ui.result

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import no.uio.ifi.in2000.prosjekt51.model.isobaricGrib.GribPoint
import no.uio.ifi.in2000.prosjekt51.model.locationForecast.TimeAndData
import no.uio.ifi.in2000.prosjekt51.ui.result.scripts.pressureToHeight


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
        WindSection(enterFunc = enterFuncWind, data = visualResultScreenUiState.currentLocationForecastData, visualResultScreenViewModel = visualResultScreenViewModel, visualResultScreenUiState = visualResultScreenUiState, lat = lat, lon = lon)
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
                text = "Precipitation: ${data?.nexthourdata?.precipitation_amount} mm",
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
                text = "Air pressure: ${data?.data?.air_pressure_at_sea_level} hPa",
                fontSize = 20.sp,
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}




@Composable
fun WindSection(enterFunc: () -> Unit, data: TimeAndData?, visualResultScreenViewModel: VisualResultScreenViewModel, visualResultScreenUiState: VisualResultScreenUiState, lat: Double, lon: Double) {
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
            Text("Maximum wind-speed: ${visualResultScreenViewModel.findMaximumAirWindSpeed(visualResultScreenUiState.height, 
                visualResultScreenUiState.currentLocationForecastData?.data?.air_pressure_at_sea_level ?: 0.0, 
                visualResultScreenUiState.currentLocationForecastData?.data?.air_temperature ?: 0.0)} m/s")
            Text("Maximum wind-shear: ${visualResultScreenViewModel.findMaximumWindShear(visualResultScreenUiState.height,
                visualResultScreenUiState.currentLocationForecastData?.data?.air_pressure_at_sea_level ?: 0.0,
                visualResultScreenUiState.currentLocationForecastData?.data?.air_temperature ?: 0.0)} m/s")

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
            Text("Precipitation amount: ${data?.nexthourdata?.precipitation_amount} mm")
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