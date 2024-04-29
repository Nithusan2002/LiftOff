package no.uio.ifi.in2000.prosjekt51.ui.search

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.text.isDigitsOnly
import androidx.navigation.NavController
import no.uio.ifi.in2000.prosjekt51.ui.BottomNavigation
import no.uio.ifi.in2000.prosjekt51.ui.LabeledDivider
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset

// Function to validate latitude
private fun isLatitudeValid(lat: String): Boolean {
    return try {
        val value = lat.toDouble()
        value in -90.0..90.0
    } catch (e: NumberFormatException) {
        false
    }
}

// Function to validate longitude
private fun isLongitudeValid(lon: String): Boolean {
    return try {
        val value = lon.toDouble()
        value in -180.0..180.0
    } catch (e: NumberFormatException) {
        false
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@ExperimentalMaterial3Api
@Composable
fun SearchScreen(
    latitudeInit: String,
    longitudeInit: String,
    onNavigateToResultScreen: (String, String, Long, Int, Double) -> Unit,
    navController: NavController
    ) {

    var latitude by rememberSaveable { mutableStateOf(latitudeInit) }
    var longitude by rememberSaveable { mutableStateOf(longitudeInit) }
    var height by rememberSaveable { mutableIntStateOf(0) }





    Scaffold(
        topBar = { CenterAlignedTopAppBar(title = { Text(text = "Search") }) },
        bottomBar = {
            BottomAppBar {
                BottomNavigation(navController = navController)
            }
        }

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
                    OutlinedButton(
                        onClick = { },
                        shape = RoundedCornerShape(5.dp),
                        modifier = Modifier
                            .height(52.dp)
                            .aspectRatio(1f),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text(text = "N")
                    }
                    OutlinedTextField(
                        value = latitude,
                        onValueChange = { latitude = it },
                        label = { Text("Grader") },
                        singleLine = true,
                        modifier = Modifier,
                        isError = !isLatitudeValid(latitude) && latitude.isNotEmpty()

                    )
                }

                Row(
                    modifier = Modifier
                        .sizeIn(maxHeight = 60.dp)
                        .fillMaxWidth()
                        .padding(start = 8.dp, end = 8.dp, bottom = 2.dp),
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    OutlinedButton(
                        onClick = { },
                        shape = RoundedCornerShape(5.dp),
                        modifier = Modifier
                            .height(52.dp)
                            .aspectRatio(1f),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text(text = "E")
                    }
                    OutlinedTextField(
                        value = longitude,
                        onValueChange = { longitude = it },
                        label = { Text("Grader") },
                        singleLine = true,
                        isError = !isLongitudeValid(longitude) && longitude.isNotEmpty()
                    )
                }

                LabeledDivider(label = "Expected height")

                OutlinedTextField(
                    value = if (height == 0) "" else "$height",
                    onValueChange = { input ->
                        if (input.isEmpty() || input.isDigitsOnly()) {
                            height = if (input.isEmpty()) 0 else input.toInt()
                        }
                    },
                    label = { Text(text = "[meter]") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 8.dp, end = 8.dp, bottom = 2.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                )
            }
            Column(
                modifier = Modifier.padding(4.dp)
            ) {
                OutlinedButton(
                    onClick = { /*TODO*/ },
                    shape = RoundedCornerShape(5.dp),
                    modifier = Modifier
                        .height(52.dp)
                        .fillMaxWidth(),
                    contentPadding = PaddingValues(0.dp),
                ) {
                    Text(text = "More options")
                }
                Button(
                    onClick = {
                        // Get the current date
                        val currentdate = LocalDateTime.now()


                        // Strip the time to get midnight
                        val dateAtMidnight = currentdate.toLocalDate().atStartOfDay()



                        // Convert to milliseconds since January 1, 1970
                        val searchdate =
                            dateAtMidnight.toInstant(ZoneOffset.UTC).toEpochMilli()




                        if (isLatitudeValid(latitude) && isLongitudeValid(longitude)) {
                            Log.d("FinalTesting", "Height: ${if (height == 0) 80_000.0 else height.toDouble()}")
                            onNavigateToResultScreen(
                                latitude,
                                longitude,
                                searchdate,
                                "${LocalDateTime.now().hour}".take(2).toInt(),
                                if (height == 0) 80_000.0 else height.toDouble()
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


