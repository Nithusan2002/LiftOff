package no.uio.ifi.in2000.prosjekt51.ui.search

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.text.isDigitsOnly
import no.uio.ifi.in2000.prosjekt51.DEFAULT_COORDS
import no.uio.ifi.in2000.prosjekt51.MAX_HEIGHT
import no.uio.ifi.in2000.prosjekt51.ui.LabeledDivider
import java.time.LocalDateTime
import java.time.ZoneOffset

// Function to validate latitude
fun isLatitudeValid(lat: String): Boolean {
    /* Validates latitude
         arguments:
             lat (String): The latitude of the location.

        returns:
            Boolean
     */
    return try {
        val value = lat.toDouble()
        value in -90.0..90.0
    } catch (e: NumberFormatException) {
        false
    }
}

// Function to validate longitude
fun isLongitudeValid(lon: String): Boolean {
    /* Validates longitude
     arguments:
         lon (String): The longitude of the location.
     returns:
         Boolean
     */
    return try {
        val value = lon.toDouble()
        value in -180.0..180.0
    } catch (e: NumberFormatException) {
        false
    }
}

@ExperimentalMaterial3Api
@Composable
fun SearchScreen(
    latitudeInit: String,
    longitudeInit: String,
    onNavigateToResultScreen: (String, String, Long, String, Double) -> Unit,
    ) {

    var latitude by rememberSaveable { mutableStateOf(latitudeInit) }
    var longitude by rememberSaveable { mutableStateOf(longitudeInit) }
    var height by rememberSaveable { mutableIntStateOf(MAX_HEIGHT) }

    var north by rememberSaveable { mutableStateOf(true) }
    var east by rememberSaveable { mutableStateOf(true) }




    Scaffold(
        topBar = { CenterAlignedTopAppBar(title = { Text(text = "Search") }) }
    ) {innerPadding ->
        Column(
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxHeight()
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(horizontal = 4.dp)
            ) {

                LabeledDivider(label = "Coordinates")

                Row(
                    modifier = Modifier
                        .sizeIn(maxHeight = 60.dp)
                        .fillMaxWidth()
                        .padding(start = 8.dp, end = 8.dp, bottom = 2.dp),
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    CoordinateSymbol(
                        coordText = if (north) "N" else "S"
                    ) { north = !north }
                    CoordinateInput(latitude, { latitude = it}, { isLatitudeValid(latitude) })

                }

                Row(
                    modifier = Modifier
                        .sizeIn(maxHeight = 60.dp)
                        .fillMaxWidth()
                        .padding(start = 8.dp, end = 8.dp, bottom = 2.dp),
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    CoordinateSymbol(
                        coordText = if (east) "E" else "W"
                    ) { east = !east }
                    CoordinateInput(longitude, { longitude = it}, { isLongitudeValid(longitude) })

                }

                LabeledDivider(label = "Expected height")

                HeightInput(height = height, onValueChange = { input ->
                    if (input.isEmpty() || input.isDigitsOnly()) {
                        height = if (input.isEmpty()) MAX_HEIGHT else input.toInt()
                    }
                } )
            }


            Column(
                modifier = Modifier.padding(4.dp)
            ) {
                Button(
                    onClick = {
                        // Get the current date
                        val currentdate = LocalDateTime.now()
                        // Strip the time to get midnight
                        val dateAtMidnight = currentdate.toLocalDate().atStartOfDay()
                        // Convert to milliseconds since January 1, 1970
                        val searchdate =
                            dateAtMidnight.toInstant(ZoneOffset.UTC).toEpochMilli()



                        // Convert latitude/longitude based on north/south or east/west coordinate
                        val trueLatitude = try {
                            val lat = latitude.toDouble()
                            if (north) latitude else (-lat).toString()
                        } catch (e: NumberFormatException) {
                            DEFAULT_COORDS
                        }

                        val trueLongitude = try {
                            val lon = longitude.toDouble()
                            if (east) longitude else (-lon).toString()
                        } catch (e: NumberFormatException) {
                            DEFAULT_COORDS
                        }

                        if (isLatitudeValid(trueLatitude) && isLongitudeValid(trueLongitude)) {
                            onNavigateToResultScreen(
                                trueLatitude,
                                trueLongitude,
                                searchdate,
                                "${LocalDateTime.now().hour}".take(2),
                                height.toDouble()
                            )
                        }
                    },
                    shape = RoundedCornerShape(5.dp),
                    modifier = Modifier
                        .height(52.dp)
                        .fillMaxWidth()
                        .padding(5.dp),
                    contentPadding = PaddingValues(0.dp),
                ) {
                    Text(text = "Search")
                }

            }

        }
    }


}


