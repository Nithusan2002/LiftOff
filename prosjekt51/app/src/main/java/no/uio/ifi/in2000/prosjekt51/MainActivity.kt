package no.uio.ifi.in2000.prosjekt51

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.prosjekt51.model.isobaricGrib.GribDataCache
import no.uio.ifi.in2000.prosjekt51.data.WeatherDataRepository
import no.uio.ifi.in2000.prosjekt51.ui.home.HomeScreen
import no.uio.ifi.in2000.prosjekt51.ui.result.ResultScreen
import no.uio.ifi.in2000.prosjekt51.ui.result.ResultScreenViewModel
import no.uio.ifi.in2000.prosjekt51.ui.theme.Prosjekt51Theme
import java.time.LocalDate
import java.time.LocalDateTime

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        preloadGribData()
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


@RequiresApi(Build.VERSION_CODES.O)
private fun preloadGribData() {
    val timesToFetch = calculateTimesToFetch()

    CoroutineScope(Dispatchers.IO).launch {
        timesToFetch.forEach { time ->
            // Check if data for this time is already fetched and stored
            if (!GribDataCache.isDataStoredForTime(time)) {
                var success = false
                var attempts = 0
                while (!success && attempts < 3) {
                    Log.d("GribTesting", "Attempting to fetch grib data for time: $time, attempt: ${attempts + 1}")
                    val result = WeatherDataRepository().fetchDataFromIsobaricGribAPI(time)
                    if (result.successfulConnection) {
                        GribDataCache.storeData(time, result.parsedGribData)
                        success = true
                    } else {
                        attempts++
                    }
                }
                if (!success) {
                    Log.d("GribTesting", "Failed to fetch grib data for time: $time after 3 attempts")
                }
            } else {
                Log.d("GribTesting", "Data for time: $time is already stored. Skipping fetch.")
            }
        }
    }
}


@RequiresApi(Build.VERSION_CODES.O)
fun calculateTimesToFetch(): List<String> {
    val possibleTimes = listOf("00", "03", "06", "09", "12", "15", "18", "21")
    val currentHour = LocalDateTime.now().hour
    val closestTimes = possibleTimes.map { it.toInt() }.filter { it >= currentHour }.take(5)

    // If we have less than 5 times, it means we need to take some from the next day
    val timesNeededFromNextDay = 5 - closestTimes.size
    val nextDayTimes = if (timesNeededFromNextDay > 0) possibleTimes.take(timesNeededFromNextDay).map { it.toInt() } else listOf()

    val today = LocalDate.now()
    val tomorrow = today.plusDays(1)

    val todayTimes = closestTimes.map { "${today}T${it.toString().padStart(2, '0')}:00:00Z" }
    val tomorrowTimes = nextDayTimes.map { "${tomorrow}T${it.toString().padStart(2, '0')}:00:00Z" }
    return todayTimes + tomorrowTimes
}
