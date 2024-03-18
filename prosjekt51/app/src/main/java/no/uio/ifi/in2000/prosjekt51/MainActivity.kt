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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import no.uio.ifi.in2000.prosjekt51.ui.home.HomeScreen
import no.uio.ifi.in2000.prosjekt51.ui.information.ResultScreen
import no.uio.ifi.in2000.prosjekt51.ui.information.ResultScreenViewModel
import no.uio.ifi.in2000.prosjekt51.ui.theme.Prosjekt51Theme

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Prosjekt51Theme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    App()
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun App(
    navController: NavHostController = rememberNavController()
) {
    NavHost(navController = navController, startDestination = "homeScreen") {
        composable("homeScreen") {
            HomeScreen(
                onNavigateToResultScreen = { latitude: String, longitude: String, date: Long, hour: Int ->
                    navController.navigate("resultScreen/$latitude/$longitude/$date/$hour")
                }
            )
        }
        composable(
            "resultScreen/{latitude}/{longitude}/{date}/{hour}",
            arguments = listOf(
                navArgument("latitude") {type = NavType.StringType},
                navArgument("longitude") {type = NavType.StringType},
                navArgument("date") {type = NavType.LongType},
                navArgument("hour") {type = NavType.IntType}
                )
        ) {backStackEntry ->
            val latitude = backStackEntry.arguments?.getString("latitude")
            val longitude = backStackEntry.arguments?.getString("longitude")
            val date = backStackEntry.arguments?.getLong("date")
            val hour = backStackEntry.arguments?.getInt("hour")
            if (latitude != null && longitude != null && date != null && hour != null) {
                val resultScreenViewModel = ResultScreenViewModel()
                ResultScreen(
                    latitude = latitude,
                    longitude = longitude,
                    date = date,
                    hour = hour,
                    onNavigateToHomeScreen = {
                        navController.navigate("homeScreen")
                    },
                    resultScreenViewModel = resultScreenViewModel
                )
            }
        }
    }
}

/*
@RequiresApi(Build.VERSION_CODES.O)  // TODO: Hele denne funksjonen skal vekk. Er her kun for testing per nå, kan erstattes med unittests senere.
@Composable
fun InformationScreen(
    informationScreenViewModel: ResultScreenViewModel = viewModel(),
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

 */

