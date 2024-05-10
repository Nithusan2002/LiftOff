package no.uio.ifi.in2000.prosjekt51.ui.result

import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.compose.ThemeManager
import no.uio.ifi.in2000.prosjekt51.model.isobaricGrib.GribPoint
import no.uio.ifi.in2000.prosjekt51.model.locationForecast.TimeAndData
import no.uio.ifi.in2000.prosjekt51.ui.LabeledDivider
import no.uio.ifi.in2000.prosjekt51.ui.result.scripts.pressureToHeight


@Composable
fun SummaryDisplay(
    visualResultScreenUiState: VisualResultScreenUiState, enterFuncWind: () -> Unit,
    enterFuncSight: () -> Unit,
    enterFuncPrecipitation: () -> Unit,
    enterFuncAir: () -> Unit,
    enterFuncLegal: () -> Unit,
    lat: Double,
    lon: Double
) {
    Column(modifier = Modifier
        .padding(top = 8.dp, bottom = 8.dp)
        .fillMaxWidth()
        .fillMaxHeight()) {
        WindSection(enterFunc = enterFuncWind, data = visualResultScreenUiState.currentLocationForecastData, visualResultScreenUiState = visualResultScreenUiState, lat = lat, lon = lon)
        SightSection(enterFunc = enterFuncSight, data = visualResultScreenUiState.currentLocationForecastData)
        PrecipitationSection(enterFunc = enterFuncPrecipitation, data = visualResultScreenUiState.currentLocationForecastData)
        AirSection(enterFunc = enterFuncAir, data = visualResultScreenUiState.currentLocationForecastData)
        LegalSection(enterFunc=enterFuncLegal)
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
            .padding(top = 48.dp)
            .height(500.dp)) {
            val gusttext = data?.data?.wind_speed_of_gust
            Text(
                buildAnnotatedString {
                    if (gusttext != null) {
                        append("Wind speed of gust: $gusttext m/s")
                    } else {
                        append("Wind speed of gust: ")
                        withStyle(style = SpanStyle(color = Color(0xFFFFA500))) { // 0xFFFFA500 is the ARGB code for orange
                            append("N/A")
                        }
                        append(" m/s")
                    }
                },
                fontSize = 20.sp,
                modifier = Modifier.padding(16.dp)
            )
            /*
            LazyColumn(modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)) {
                items(safeGribPoints) { gribPoint ->
                    GribPointItems(gribPoint, groundPressure, groundTemp)
                }
            }

             */
            Log.d("Gribdata","Size ${safeGribPoints.size}")
            safeGribPoints.forEach {
                Log.d("Gribdata","Displayed")
                GribPointItems(it, groundPressure, groundTemp)
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
                modifier = Modifier
                    .padding(16.dp)
                    .testTag("UV")
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
        val precipitationtext = data?.nexthourdata?.precipitation_amount

        Text(
            buildAnnotatedString {
                if (precipitationtext != null) {
                    append("Precipitation amount: $precipitationtext mm")
                } else {
                    append("Precipitation amount: ")
                    withStyle(style = SpanStyle(color = Color(0xFFFFA500))) {
                        append("N/A")
                    }
                    append(" mm")
                }
            },
            fontSize = 20.sp,
            modifier = Modifier.padding(16.dp)
        )
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
fun LegalDisplay(exitFunc: () -> Unit) {
    val context = LocalContext.current

    Box(modifier = Modifier.fillMaxSize()) {
        IconButton(
            onClick = { exitFunc() },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
        ) {
            Icon(Icons.Filled.Close, contentDescription = "Close")
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 48.dp)
        ) {
            Text(
                text = "Legal restrictions",
                fontSize = 20.sp,
                modifier = Modifier.padding(16.dp),
                color = MaterialTheme.colorScheme.background
            )
            val annotatedString = buildAnnotatedString {
                append("Please ensure to coordinate with the local municipality, landowner, Civil Aviation Authority, Avinor, fire department, and police. ")
                append("Personal arrangements must be made with each of these entities. Prior to this, please verify that you are not within restricted airspace: ")

                pushStringAnnotation(
                    tag = "URL",
                    annotation = "https://luftrom.info/viewer.html#4/65.49/16.96/"
                )
                withStyle(style = SpanStyle(textDecoration = TextDecoration.Underline)) {
                    append("https://luftrom.info/viewer.html#4/65.49/16.96/")
                }
                pop()
            }

            ClickableText(
                text = annotatedString,
                style = TextStyle(color = MaterialTheme.colorScheme.tertiary),
                onClick = { offset ->
                    annotatedString.getStringAnnotations(tag = "URL", start = offset, end = offset)
                        .firstOrNull()?.let { annotation ->
                            // Create an intent with the URL to open it in a web browser
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(annotation.item))
                            context.startActivity(intent) // Start the intent to open the URL
                        }
                },
            )
        }
    }
}


@Composable
fun WindSection(enterFunc: () -> Unit, data: TimeAndData?, visualResultScreenUiState: VisualResultScreenUiState, lat: Double, lon: Double) {
    LabeledDivider(label = "Wind")
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
        modifier = Modifier
            .fillMaxWidth(),
        shape = RectangleShape,
        onClick = { enterFunc() }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            if (lat > 64.25 || lat < 55.35 || lon > 14.51 || lon < -1.45) {
                Text(text = "Note: Wind data may not be available for coordinates outside southern Norway", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.tertiary)
            }
            val gusttext = data?.data?.wind_speed_of_gust
            val speedtext = visualResultScreenUiState.maxWindSpeed
            val sheartext = visualResultScreenUiState.maxWindShear
            Text(
                buildAnnotatedString {
                    if (gusttext != null) {
                        append("Wind speed of gust: $gusttext m/s")
                    } else {
                        append("Wind speed of gust: ")
                        withStyle(style = SpanStyle(color = Color(0xFFFFA500))) { // 0xFFFFA500 is the ARGB code for orange
                            append("N/A")
                        }
                        append(" m/s")
                    }
                }
            )

            Text(
                buildAnnotatedString {
                    if (speedtext != null) {
                        append("Maximum wind-speed: $speedtext m/s")
                    } else {
                        append("Maximum wind-speed: ")
                        withStyle(style = SpanStyle(color = Color(0xFFFFA500))) {
                            append("N/A")
                        }
                        append(" m/s")
                    }
                },
            )

            Text(
                buildAnnotatedString {
                    if (sheartext != null) {
                        append("Maximum wind-shear: $sheartext m/s")
                    } else {
                        append("Maximum wind-shear: ")
                        withStyle(style = SpanStyle(color = Color(0xFFFFA500))) {
                            append("N/A")
                        }
                        append(" m/s")
                    }
                },
            )
        }
        Text(modifier = Modifier.align(Alignment.CenterHorizontally), text = "Show more...")
    }
}

@Composable
fun SightSection(enterFunc: () -> Unit, data: TimeAndData?) {
    LabeledDivider(label = "Sight")
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
        modifier = Modifier
            .fillMaxWidth(),
        shape = RectangleShape,
        onClick = { enterFunc() }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Cloud area fraction (high): ${data?.data?.cloud_area_fraction_high} %")
            Text("Cloud area fraction (medium): ${data?.data?.cloud_area_fraction_medium} %")
            Text("Cloud area fraction (low): ${data?.data?.cloud_area_fraction_low} %")
        }
        Text(modifier = Modifier.align(Alignment.CenterHorizontally), text = "Show more...")
    }
}

@Composable
fun PrecipitationSection(enterFunc: () -> Unit, data: TimeAndData?) {
    LabeledDivider(label = "Precipitation")
    Card (
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
        modifier = Modifier
            .fillMaxWidth(),
        shape = RectangleShape,
        onClick = { enterFunc() }
    ) {
        val precipitationtext = data?.nexthourdata?.precipitation_amount
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                buildAnnotatedString {
                    if (precipitationtext != null) {
                        append("Precipitation amount: $precipitationtext mm")
                    } else {
                        append("Precipitation amount: ")
                        withStyle(style = SpanStyle(color = Color(0xFFFFA500))) {
                            append("N/A")
                        }
                        append(" mm")
                    }
                },
            )
            Text(modifier = Modifier.align(Alignment.CenterHorizontally), text = "Show more...")
        }
    }
}

@Composable
fun AirSection(enterFunc: () -> Unit, data: TimeAndData?) {
    LabeledDivider(label = "Air")
    Card (
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
        modifier = Modifier
            .fillMaxWidth(),
        shape = RectangleShape,
        onClick = { enterFunc() }
    ) {
        Column(modifier = Modifier.padding(16.dp).fillMaxWidth()) {
            Text("Dew point temperature: ${data?.data?.dew_point_temperature} °C")
            Text("Relative humidity: ${data?.data?.relative_humidity} %")

        }
        Text(modifier = Modifier.align(Alignment.CenterHorizontally), text = "Show more...")
    }
}

@Composable
fun LegalSection(enterFunc: () -> Unit) {
    LabeledDivider(label = "Legal restrictions")
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
        modifier = Modifier
            .fillMaxWidth(),
        shape = RectangleShape,
        onClick = { enterFunc() }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Please ensure to coordinate with the local municipality, landowner, Civil Aviation Authority, Avinor, fire department, and police. Personal arrangements must be made with each of these entities. Prior to this, please verify that you are not within restricted airspace: [insert link here]...")
        }
        Text(modifier = Modifier.align(Alignment.CenterHorizontally), text = "Show more...")
    }
}


@Composable
fun GribPointItems(gribPoint: GribPoint, groundPressure: Double?, groundTemp: Double?) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = "Height: ${pressureToHeight(gribPoint.height, groundPressure, groundTemp)} meters")
        Text(text = "Wind: ${gribPoint.wind}")
        Text(text = "Temperature: ${gribPoint.temperature}")
        Text(text = "Wind-Shear: ${gribPoint.windshear}")
        Text(text = "Wind direction: ${gribPoint.winddir}")
    }
}

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
            if (cells.get(0).size != 0) { // Added to prevent error when first value is 00, so that new day won't be added if previous day is empty
                currentDay++
                cells.add(ArrayList())
            }
        }
        cells[currentDay].add(launchWindow)
    }

    Column {
        cells.forEach { day ->
            Text(text = day.first().time.substring(0, 10))
            FlowRow {
                day.forEach { window ->
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .background(color = window.color)
                            .clickable {
                                onWindowClick(
                                    window.time.substring(11, 13),
                                    window.time.substring(0, 10)
                                )
                            }
                    ) {
                        Text(
                            text = window.time.substring(11, 13),
                            modifier = Modifier.align(Alignment.Center),
                            color = window.textColor
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.size(16.dp)) // Padding between days
        }
    }
}
