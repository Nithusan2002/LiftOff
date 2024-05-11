package no.uio.ifi.in2000.prosjekt51.ui.result

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
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
import no.uio.ifi.in2000.prosjekt51.LAT_NORTH_LIMIT
import no.uio.ifi.in2000.prosjekt51.LAT_SOUTH_LIMIT
import no.uio.ifi.in2000.prosjekt51.LON_EAST_LIMIT
import no.uio.ifi.in2000.prosjekt51.LON_WEST_LIMIT
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


// =========== DISPLAYS ==============
// Displays all data for a given data type, when clicking the relevant section


@Composable
fun WindDisplay(
    exitFunc: () -> Unit,
    data: TimeAndData?,
    gribPoints: List<GribPoint>?,
    groundPressure: Double?,
    groundTemp: Double?
) {
    val safeGribPoints = gribPoints ?: emptyList()
    var expanded by rememberSaveable { mutableStateOf(false) }

    val dynamicHeight = if (expanded) (safeGribPoints.size * 160 + 20).dp else 420.dp

    Box(modifier = Modifier.fillMaxSize()) {
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
            .padding(horizontal = 4.dp)
            .height(dynamicHeight)) {

            NullableText(value = data?.data?.wind_speed_of_gust, text = "Wind speed of gust:", unit = "m/s")

            if (safeGribPoints.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .clickable { expanded = !expanded }
                        .border(2.dp, color = MaterialTheme.colorScheme.secondary)
                        .padding(8.dp)
                ) {
                    Column {
                        safeGribPoints.take(if (expanded) safeGribPoints.size else 2).forEach {
                            GribPointItems(it, groundPressure, groundTemp)
                        }
                        Text(
                            text = if (expanded) "Show less..." else "Show more...",
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .padding(top = 4.dp),
                        )
                    }
                }
            }
        }
    }
}





@Composable
fun SightDisplay(exitFunc: () -> Unit, data: TimeAndData?){
    Box(modifier = Modifier.fillMaxSize()) {
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
            NullableText(value = data?.data?.cloud_area_fraction_high, text = "Cloud area fraction (high):", unit = "%")
            NullableText(value = data?.data?.cloud_area_fraction_medium, text = "Cloud area fraction (medium):", unit = "%")
            NullableText(value = data?.data?.cloud_area_fraction_low, text = "Cloud area fraction (low):", unit = "%")
            NullableText(value = data?.data?.cloud_area_fraction, text = "Cloud area fraction:", unit = "%")
            NullableText(value = data?.data?.fog_area_fraction, text = "Fog area fraction:", unit="%")
            NullableText(value = data?.data?.ultraviolet_index_clear_sky, text = "UV index (Clear sky):", testTag = "UV")
        }
    }
}

@Composable
fun PrecipitationDisplay(exitFunc: () -> Unit, data: TimeAndData?){
    Box(modifier = Modifier.fillMaxSize()) {
        IconButton(
            onClick = { exitFunc() },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
        ) {
            Icon(Icons.Filled.Close, contentDescription = "Close")
        }
        NullableText(value = data?.nexthourdata?.precipitation_amount, text = "Precipitation amount:", unit = "mm")
    }
}

@Composable
fun AirDisplay(exitFunc: () -> Unit, data: TimeAndData?){
    Box(modifier = Modifier.fillMaxSize()) {
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
            NullableText(value = data?.data?.dew_point_temperature, text = "Dew point temperature:", unit = "°C")
            NullableText(value = data?.data?.relative_humidity, text = "Relative humidity:", unit = "%")
            NullableText(value = data?.data?.air_temperature, text = "Air temperature:", unit = "°C")
            NullableText(value = data?.data?.air_temperature_percentile_10, text = "Air temperature (10th percentile):", unit = "°C")
            NullableText(value = data?.data?.air_temperature_percentile_90, text = "Air temperature (90th percentile):", unit = "°C")
            NullableText(value = data?.data?.air_pressure_at_sea_level, text = "Air pressure:", unit = "hPa")
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
                }
            )
        }
    }
}




// =========== SECTIONS ==============
// Small summary of data for each data type



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
            if (lat > LAT_NORTH_LIMIT || lat < LAT_SOUTH_LIMIT || lon > LON_EAST_LIMIT || lon < LON_WEST_LIMIT) {
                Text(text = "Note: Wind data may not be available for coordinates outside southern Norway", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.tertiary)
            }
            NullableText(value = data?.data?.wind_speed_of_gust, text = "Wind speed of gust:", unit = "m/s", small = true)
            NullableText(value = visualResultScreenUiState.maxWindSpeed, text = "Maximum wind-speed:", unit = "m/s", small = true)
            NullableText(value = visualResultScreenUiState.maxWindShear, text = "Maximum wind-shear:", unit = "m/s", small = true)

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
            NullableText(value = data?.data?.cloud_area_fraction_high, text = "Cloud area fraction (high):", unit = "%", small = true)
            NullableText(value = data?.data?.cloud_area_fraction_medium, text = "Cloud area fraction (medium):", unit = "%", small = true)
            NullableText(value = data?.data?.cloud_area_fraction_low, text = "Cloud area fraction (low):", unit = "%", small = true)
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
        Column(modifier = Modifier.padding(16.dp)) {
            NullableText(value = data?.nexthourdata?.precipitation_amount, text = "Precipitation amount:", unit = "mm", small = true)
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
        Column(modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()) {
            NullableText(value = data?.data?.dew_point_temperature, text = "Dew point temperature:", unit = "°C", small = true)
            NullableText(value = data?.data?.relative_humidity, text = "Relative humidity:", unit = "%", small = true)
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
            Text(text = "Please ensure to coordinate with the local municipality, " +
                    "landowner, Civil Aviation Authority, Avinor, fire department, " +
                    "and police. Personal arrangements must be made with each of " +
                    "these entities. Prior to this, please verify that you are not " +
                    "within restricted airspace: ...",
                color = MaterialTheme.colorScheme.tertiary
                )

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
            if (cells[0].size != 0) { // Added to prevent error when first value is 00, so that new day won't be added if previous day is empty
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


@Composable
fun NullableText(value: Double?, text: String, unit: String? = null, small: Boolean = false, testTag: String = "") {
    val fontSize = if (!small) 20.sp else 16.sp
    val modifier = if (!small) Modifier.padding(16.dp).testTag(testTag) else Modifier.padding(0.dp).testTag(testTag)

    Text(
        buildAnnotatedString {
            if (value != null) {
                append("$text $value ${unit ?: ""}")
            } else {
                append("$text ")
                withStyle(style = SpanStyle(color = Color(0xFFFFA500))) {
                    append("N/A")
                }
                append(" ${unit ?: ""}")
            }
        },
        fontSize = fontSize,
        modifier = modifier
    )
}