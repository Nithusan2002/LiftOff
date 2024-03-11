package no.uio.ifi.in2000.prosjekt51

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import no.uio.ifi.in2000.prosjekt51.ui.information.InformationScreenViewModel
import no.uio.ifi.in2000.prosjekt51.ui.theme.Prosjekt51Theme

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Prosjekt51Theme {
                // A surface container using the 'background' color from the theme
                InformationScreen()
            }
        }
    }
}


@RequiresApi(Build.VERSION_CODES.O)  // TODO: Hele denne funksjonen skal vekk. Er her kun for testing per nå, kan erstattes med unittests senere.
@Composable
fun InformationScreen(
    informationScreenViewModel: InformationScreenViewModel = viewModel(),
){
    val lat = 54.4; val lon = 12.3; val alt = 10

    Column {
        Button(onClick = {informationScreenViewModel.fetchLocationForecast(lat, lon, alt)}) {
            Text(text = "Fetch")

        }
        Button(onClick = {informationScreenViewModel.logvalue()}) {
            Text(text = "Log")

        }
    }




}

