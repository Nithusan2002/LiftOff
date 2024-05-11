package no.uio.ifi.in2000.prosjekt51

import no.uio.ifi.in2000.prosjekt51.ui.map.MapScreen
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.room.Room
import no.uio.ifi.in2000.prosjekt51.ui.theme.AppTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.prosjekt51.model.isobaricGrib.GribDataCache
import no.uio.ifi.in2000.prosjekt51.data.WeatherDataRepository
import no.uio.ifi.in2000.prosjekt51.ui.BottomNavigation
import no.uio.ifi.in2000.prosjekt51.ui.favorites.AppDatabase
import no.uio.ifi.in2000.prosjekt51.ui.favorites.FavoriteRepository
import no.uio.ifi.in2000.prosjekt51.ui.favorites.FavoriteViewModel
import no.uio.ifi.in2000.prosjekt51.ui.favorites.FavoritesListScreen
import no.uio.ifi.in2000.prosjekt51.ui.search.SearchScreen
import no.uio.ifi.in2000.prosjekt51.ui.result.VisualResultScreen
import no.uio.ifi.in2000.prosjekt51.ui.result.ResultScreenViewModel
import no.uio.ifi.in2000.prosjekt51.ui.settings.SettingsScreen
import java.time.LocalDate
import java.time.LocalDateTime

class MainActivity : ComponentActivity() {
    private lateinit var favoriteViewModel: FavoriteViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        preloadGribData()
        val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "favoritesDatabase.db"
        ).fallbackToDestructiveMigration().build()

        val favoriteDao = db.favoriteDao()
        val favoriteRepository = FavoriteRepository(favoriteDao)
        favoriteViewModel = ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(FavoriteViewModel::class.java)) {
                    @Suppress("UNCHECKED_CAST")
                    return FavoriteViewModel(favoriteRepository) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        })[FavoriteViewModel::class.java]

        setContent {
            AppTheme {
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App(
    navController: NavHostController = rememberNavController(),
    resultScreenViewModel: ResultScreenViewModel = remember { ResultScreenViewModel() }
) {
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold (
        bottomBar = {
            BottomAppBar {
                BottomNavigation(navController = navController)
            }
        }
    ) {
        innerPadding ->
            Column(
                modifier = Modifier.padding(innerPadding)
            ) {
                NavHost(navController = navController, startDestination = "searchScreen/0/0") {
                    composable(
                        route = "searchScreen/{latitudeInit}/{longitudeInit}",
                        arguments = listOf(
                            navArgument("latitudeInit") { type = NavType.StringType },
                            navArgument("longitudeInit") { type = NavType.StringType }
                        )
                    ) { backStackEntry ->
                        val latitudeInit = if (backStackEntry.arguments?.getString("latitudeInit") == DEFAULT_COORDS) "" else backStackEntry.arguments?.getString("latitudeInit") ?: ""
                        val longitudeInit = if (backStackEntry.arguments?.getString("longitudeInit") == DEFAULT_COORDS) "" else backStackEntry.arguments?.getString("longitudeInit") ?: ""

                        SearchScreen(
                            latitudeInit = latitudeInit,
                            longitudeInit = longitudeInit,
                            onNavigateToResultScreen = { latitude: String, longitude: String, date: Long, hour: String, height: Double ->
                                navController.navigate("resultScreen/$latitude/$longitude/$date/$hour/$height")
                            }
                        )
                    }


                    composable(
                        "resultScreen/{latitude}/{longitude}/{date}/{hour}/{height}",
                        arguments = listOf(
                            navArgument("latitude") {type = NavType.StringType},
                            navArgument("longitude") {type = NavType.StringType},
                            navArgument("date") {type = NavType.LongType},
                            navArgument("hour") {type = NavType.StringType},
                            navArgument("height") {type = NavType.FloatType}
                        )
                    ) { backStackEntry ->
                        val latitude = backStackEntry.arguments?.getString("latitude")
                        val longitude = backStackEntry.arguments?.getString("longitude")
                        val date = backStackEntry.arguments?.getLong("date")
                        val hour = backStackEntry.arguments?.getString("hour")
                        val height = backStackEntry.arguments?.getFloat("height")
                        if (latitude != null && longitude != null && date != null && hour != null) {
                            Log.d("height", "In MainActivity got height $height")
                            VisualResultScreen(
                                latitude = latitude,
                                longitude = longitude,
                                date = date,
                                hour = hour,
                                height = height?.toDouble(),
                                onNavigateToHomeScreen = {
                                    navController.navigate("searchScreen/$DEFAULT_COORDS/$DEFAULT_COORDS")
                                },
                                onNavigateToResultScreen = { funcLatitude: String, funcLongitude: String, funcDate: Long, funcHour: String ->
                                    navController.navigate("resultScreen/$funcLatitude/$funcLongitude/$funcDate/$funcHour/$height")
                                },
                                resultScreenViewModel = resultScreenViewModel,
                                navController = navController,
                                snackbarHostState = snackbarHostState,
                                onRetryClicked = {
                                    navController.popBackStack()
                                }
                            )
                        }
                    }

                    composable("mapScreen") {
                        MapScreen(navController)
                    }

                    composable("favoritesScreen") {
                        FavoritesListScreen(navController)
                    }

                    composable("settingsScreen") {
                        SettingsScreen()
                    }
                }
            }
    }
}


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


fun calculateTimesToFetch(): List<String> {
    val possibleTimes = listOf("00", "03", "06", "09", "12", "15", "18", "21")
    val currentHour = LocalDateTime.now().hour - 3
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
