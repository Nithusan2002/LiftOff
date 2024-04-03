package no.uio.ifi.in2000.prosjekt51.ui.home
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TimeInput
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToResultScreen: (String, String, Long, Int) -> Unit,
) {
    var latitude by remember { mutableStateOf("") }
    var longitude by remember { mutableStateOf("") }
    val datePickerState = rememberDatePickerState(initialDisplayMode = DisplayMode.Input)
    val timePickerState = rememberTimePickerState()

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(16.dp)
    ) {
        TextField(
            value = latitude,
            onValueChange = { latitude = it },
            label = { Text("Breddegrader") },
            modifier = Modifier
                .padding(16.dp),
            singleLine = true
        )

        TextField(
            value = longitude,
            onValueChange = { longitude = it },
            label = { Text(text = "Lengdegrader") },
            modifier = Modifier
                .padding(16.dp),
            singleLine = true
        )

        DatePicker(
            state = datePickerState,
            modifier = Modifier
                .padding(16.dp)
        )

        TimeInput(
            state = timePickerState,
            modifier = Modifier
                .padding(16.dp)
        )

        //Navigates to result screen with parameters
        Button(onClick = { datePickerState.selectedDateMillis?.let {
            onNavigateToResultScreen(latitude.toString(), longitude.toString(), it, timePickerState.hour)
        } }) {
            Text("Start")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    //HomeScreen()
}