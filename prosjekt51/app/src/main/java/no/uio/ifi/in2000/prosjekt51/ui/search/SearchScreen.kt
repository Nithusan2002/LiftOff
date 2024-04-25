package no.uio.ifi.in2000.prosjekt51.ui.search

import android.os.Build
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.text.isDigitsOnly
import androidx.navigation.NavController
import no.uio.ifi.in2000.prosjekt51.ui.LabeledDivider
import java.time.LocalDateTime
import java.time.ZoneId

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
    onNavigateToResultScreen: (String, String, Long, Int) -> Unit,
    navController: NavController
    ) {

    var latitude by remember { mutableStateOf("") }
    var longitude by remember { mutableStateOf("") }
    var time by remember { mutableStateOf("${(LocalDateTime.now().hour + 1).mod(24)}:00") }
    var date by remember { mutableStateOf("${LocalDateTime.now().dayOfMonth}.${"${LocalDateTime.now().monthValue}".padStart(3, 0.toChar())}.${LocalDateTime.now().year}")}
    var dateint by remember { mutableIntStateOf(0)}
    var height by remember { mutableIntStateOf(0) }

    var timeexpanded by remember { mutableStateOf(false) }
    var dateexpanded by remember { mutableStateOf(false) }




    Scaffold(
        topBar = { CenterAlignedTopAppBar(title = { Text(text = "Search") }) },
        bottomBar = {
            BottomAppBar {
                Row(
                    modifier = Modifier.fillMaxWidth().height(56.dp),  // TODO: Same height on all screens
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    IconButton(onClick = { navController.navigate("searchScreen") }) {
                        Icon(Icons.Filled.Search, contentDescription = "Search")
                    }
                    IconButton(onClick = { navController.navigate("mapScreen") }) {
                        Icon(Icons.Filled.Place, contentDescription = "Map")
                    }
                    IconButton(onClick = { /* Placeholder action */ }) {
                        Icon(Icons.Filled.Star, contentDescription = "Favourites")
                    }
                    IconButton(onClick = { /* Placeholder action */ }) {
                        Icon(Icons.Filled.Settings, contentDescription = "Settings")
                    }
                }
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
                        onClick = { /*TODO*/ },
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
                        onClick = { /*TODO*/ },
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

                LabeledDivider(label = "Time")

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
                        onValueChange = { time = it },
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
                                    timeexpanded = false
                                },
                                contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                            )
                        }
                    }
                }


                LabeledDivider(label = "Date")

                ExposedDropdownMenuBox(expanded = dateexpanded, onExpandedChange = { dateexpanded = !dateexpanded }, modifier = Modifier.padding(start = 8.dp, end = 8.dp, bottom = 2.dp)) {
                    TextField(
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth(),
                        readOnly = true,
                        value = date,
                        onValueChange = { date = it },
                        label = { Text(text = "Launch time") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = dateexpanded) },
                        colors = ExposedDropdownMenuDefaults.textFieldColors(),
                    )

                    ExposedDropdownMenu(
                        modifier = Modifier.fillMaxWidth(),
                        expanded = dateexpanded,
                        onDismissRequest = { dateexpanded = false },
                    ) {
                        (0..7).forEach { day ->
                            val nextday = LocalDateTime.now().plusDays(day.toLong())
                            val dateText = "${nextday.dayOfMonth}.${"${nextday.monthValue}".padStart(3, 0.toChar())}.${nextday.year}" // TODO: PadStart not working?
                            DropdownMenuItem(
                                text = { Text(dateText) },
                                onClick = {
                                    date = dateText
                                    dateint = day
                                    dateexpanded = false
                                          },
                                contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                            )
                        }
                    }
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
                    modifier = Modifier.fillMaxWidth().padding(start = 8.dp, end = 8.dp, bottom = 2.dp),
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
                Button(onClick = {
                    // Get the current date and add days
                    val currentdate = LocalDateTime.now().plusDays(dateint.toLong())

                    // Strip the time to get midnight
                    val dateAtMidnight = currentdate.toLocalDate().atStartOfDay()

                    // Convert to milliseconds since January 1, 1970
                    val searchdate = dateAtMidnight.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()


                    if (isLatitudeValid(latitude) && isLongitudeValid(longitude)) {
                        onNavigateToResultScreen(latitude, longitude, searchdate, time.take(2).toInt())
                    } },
                    shape = RoundedCornerShape(5.dp),
                    modifier = Modifier
                        .height(52.dp)
                        .fillMaxWidth(),
                    contentPadding = PaddingValues(0.dp),) {
                    Text(text = "Search")
                }

            }

        }
    }


}


