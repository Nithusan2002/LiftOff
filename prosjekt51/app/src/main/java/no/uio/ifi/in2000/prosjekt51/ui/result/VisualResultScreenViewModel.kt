package no.uio.ifi.in2000.prosjekt51.ui.result

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.prosjekt51.MAX_HEIGHT
import no.uio.ifi.in2000.prosjekt51.calculateTimesToFetch
import no.uio.ifi.in2000.prosjekt51.data.WeatherDataRepository
import no.uio.ifi.in2000.prosjekt51.data.locationForecast.LocationForecastAPI
import no.uio.ifi.in2000.prosjekt51.model.isobaricGrib.GribJson
import no.uio.ifi.in2000.prosjekt51.model.isobaricGrib.GribPoint
import no.uio.ifi.in2000.prosjekt51.model.locationForecast.TimeAndData
import no.uio.ifi.in2000.prosjekt51.model.isobaricGrib.GribDataCache
import no.uio.ifi.in2000.prosjekt51.model.locationForecast.LocationForecastWeatherData
import no.uio.ifi.in2000.prosjekt51.model.locationForecast.LocationForecastWeatherNextHourData
import no.uio.ifi.in2000.prosjekt51.ui.result.scripts.getGribDataFromCoordinates
import no.uio.ifi.in2000.prosjekt51.ui.result.scripts.pressureToHeight
import java.time.Instant
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.math.abs

data class VisualResultScreenUiState(
    val locationForecastData: List<TimeAndData>? = null,
    val currentLocationForecastData: TimeAndData? = null,
    val isobaricGribData: List<GribJson>? = null,
    val currentGribData: List<GribPoint>? = null,
    val error: String? = null,
    val windCondition: Boolean = false,
    val sightCondition: Boolean = false,
    val precipitationCondition: Boolean = false,
    val airCondition: Boolean = false,
    val height: Double = MAX_HEIGHT.toDouble(),
    val maxWindSpeed: Double? = null,
    val maxWindShear: Double? = null
) {
    val hasError: Boolean
        get() = error != null
}

class VisualResultScreenViewModel: ViewModel() {
    private val weatherDataRepository = WeatherDataRepository(LocationForecastAPI())


    private val _visualResultScreenUiState = MutableStateFlow(VisualResultScreenUiState())
    val visualResultScreenUiState: StateFlow<VisualResultScreenUiState> = _visualResultScreenUiState.asStateFlow()

    @RequiresApi(Build.VERSION_CODES.O)
    fun fetchData(lat: Double,
                  lon:Double,
                  alt: Int = 0,
                  date: Long,
                  hour: Int,
                  height: Double? = null) {
        /*
        Fetches weather data into uiState

        arguments:
            lat: latitude between -90 and 90 as a double
            lon: longitude between -180 and 180 as a double
            alt: altitude as an integer
            date: milliseconds since epoch of 1970-01-01T00:00:00Z as a Long
            hour: hour of the day as an integer,
            height: maximum height of launch

         */
        val time: String = Instant.ofEpochMilli(date + hour*60*60*1000).toString()
        // Correct time by forcing time to closest 3-hour-interval value
        val correctedTime = findClosestGribData(time)
        fetchLocationForecast(lat, lon, alt, time)
        getCurrentGribData(lat, lon, correctedTime)
        if (height != null) { updateHeight(height)}
    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun checkLaunchConditions() {
        /*
        Checks launch conditions against limit values

        return:
            String to be displayed on resultScreen
         */


        val launchCheckResult: List<Boolean>

        val data = visualResultScreenUiState.value.currentLocationForecastData?.data
        val nexthourdata = visualResultScreenUiState.value.currentLocationForecastData?.nexthourdata
        //If a value is above the limit value or null the result is false
        launchCheckResult = listOf(
            checkWindCondition(data),
            checkSightCondition(data),
            checkPrecipitationCondition(nexthourdata),
            checkAirCondition(data)
        )

        _visualResultScreenUiState.update { currentUiState ->
            currentUiState.copy(
                windCondition = launchCheckResult[0],
                sightCondition = launchCheckResult[1],
                precipitationCondition = launchCheckResult[2],
                airCondition = launchCheckResult[3]
                , error = null)
        }
    }

    private fun updateHeight(height: Double){
        /*
           Updates height in uiState

           arguments:
               height: Double of the height
        */
        _visualResultScreenUiState.update { currentUiState ->
            currentUiState.copy(height = height, error = null)
        }
    }


    private fun checkWindCondition(lfwData: LocationForecastWeatherData?): Boolean{
        /*
          Calculates maximum wind speed and maximum wind shear for height,
          and checks whether wind values are within limits

          arguments:
              lfwData: LocationForecastWeatherData-instance of the given time and coordinates

          returns:
              Boolean
       */
        val maxWindSpeed = findMaximumAirWindSpeed(visualResultScreenUiState.value.height,
            visualResultScreenUiState.value.currentLocationForecastData?.data?.air_pressure_at_sea_level ?: 0.0,
            visualResultScreenUiState.value.currentLocationForecastData?.data?.air_temperature ?: 0.0
        ) ?: 0.0

        val maxWindShear = findMaximumWindShear(visualResultScreenUiState.value.height,
            visualResultScreenUiState.value.currentLocationForecastData?.data?.air_pressure_at_sea_level ?: 0.0,
            visualResultScreenUiState.value.currentLocationForecastData?.data?.air_temperature ?: 0.0
        ) ?: 0.0

        _visualResultScreenUiState.update { currentUiState ->
            currentUiState.copy(maxWindSpeed = maxWindSpeed, maxWindShear = maxWindShear)
        }
        val result = when {
            (lfwData?.wind_speed_of_gust?.compareTo(8.6) ?: 1) > 0 -> false
            (maxWindSpeed).compareTo(17.2) > 0 -> false
            (maxWindShear).compareTo(24.5) > 0 -> false
            else -> true
        }
        return result
    }

    private fun checkSightCondition(lfwData: LocationForecastWeatherData?): Boolean{
        /*
          Checks whether wind values are within limits

          arguments:
              lfwData: LocationForecastWeatherData-instance of the given time and coordinates

          returns:
              Boolean
        */
        return when {
            (lfwData?.cloud_area_fraction_high?.compareTo(15.0) ?: 1) > 0 -> false
            (lfwData?.cloud_area_fraction_medium?.compareTo(15.0) ?: 1) > 0 -> false
            (lfwData?.cloud_area_fraction_low?.compareTo(5.0) ?: 1) > 0 -> false
            else -> true
        }
    }

    private fun checkPrecipitationCondition(lfwData: LocationForecastWeatherNextHourData?): Boolean{
        /*
          Checks whether precipitation values are within limits

          arguments:
              lfwData: LocationForecastWeatherData-instance of the given time and coordinates

          returns:
              Boolean
        */
        return when {
            (lfwData?.precipitation_amount?.compareTo(0) ?: 1) > 0 -> false
            else -> true
        }
    }

    private fun checkAirCondition(lfwData: LocationForecastWeatherData?): Boolean{
        /*
          Checks whether humidity- and temperature values are within limits

          arguments:
              lfwData: LocationForecastWeatherData-instance of the given time and coordinates

          returns:
              Boolean
        */
        return when {
            (lfwData?.relative_humidity?.compareTo(75.0) ?: 1) > 0 -> false
            (lfwData?.dew_point_temperature?.compareTo(15.0) ?: 1) > 0 -> false
            else -> true
        }
    }

    fun findMaximumAirWindSpeed(height: Double, P_b: Double, t_b: Double): Double? {
        /*
         Finds the maximum wind speed value below a given height value.

         arguments:
             height: The height beneath which maximum wind speed is found
             P_b: Ground pressure, used to calculate height of grib data
             t_b: Ground temperature, used to calculate height of grib data

         returns:
             Double of maximum wind speed
       */
        return visualResultScreenUiState.value.currentGribData
            ?.filter { pressureToHeight( it.height, P_b, t_b)  <= height }
            ?.maxOfOrNull { it.wind }
    }

    fun findMaximumWindShear(height: Double, P_b: Double, t_b: Double): Double? {/*
         Finds the maximum wind shear value below a given height value.

         arguments:
             height: The height beneath which maximum wind shear is found
             P_b: Ground pressure, used to calculate height of grib data
             t_b: Ground temperature, used to calculate height of grib data

         returns:
             Double of maximum wind shear
       */
        return visualResultScreenUiState.value.currentGribData
            ?.filter {pressureToHeight( it.height, P_b, t_b)  <= height }
            ?.maxOfOrNull { it.windshear }
    }


    private fun getGribData(time: String): List<GribJson>? {
        return GribDataCache.getData(time)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getCurrentGribData(lat: Double, lon: Double, time: String){
        _visualResultScreenUiState.update { currentUiState ->
            val gribPoints = getGribDataFromCoordinates(lat, lon, getGribData(time))
            currentUiState.copy(isobaricGribData = getGribData(time), currentGribData = gribPoints)
        }
        checkLaunchConditions()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun fetchLocationForecast(lat: Double, lon: Double, alt: Int, time: String) {
        /*
        Fetch LocationForecast-data through repository, and update uistate accordingly.
        arguments:
            lat: latitude between -90 and 90 as a double
            lon: longitude between -180 and 180 as a double
            alt: altitude as an integer
            time: time as a string
        */

        viewModelScope.launch {
            try {
                val result = weatherDataRepository.fetchDataFromLocationForecastAPI(lat, lon, alt)
                var first = result.parsedLocationForecastData.first()
                for (element in result.parsedLocationForecastData) {
                    if (element.time == time) {
                        first = element
                    }
                }
                _visualResultScreenUiState.update { currentUiState ->
                    currentUiState.copy(currentLocationForecastData = first, error = null)
                }
                checkLaunchConditions()
            } catch (e: Exception) {
                _visualResultScreenUiState.value = _visualResultScreenUiState.value.copy(error = "Kan ikke hente locationforecast data")
            }
        }

    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun findClosestGribData(target: String): String {
        val possTimes = calculateTimesToFetch()
        val formatter = DateTimeFormatter.ISO_DATE_TIME
        val targetDateTime = LocalDateTime.parse(target, formatter)

        // Placeholder for the best match
        var closestTime: LocalDateTime? = null
        var minDifference = Int.MAX_VALUE

        for (time in possTimes) {
            val possibleTime = LocalDateTime.parse(time, formatter)
            // Ensure comparison happens on the same date
            if (possibleTime.toLocalDate() == targetDateTime.toLocalDate()) {
                val difference = abs(possibleTime.toLocalTime().toSecondOfDay() - targetDateTime.toLocalTime().toSecondOfDay())
                if (difference < minDifference) {
                    minDifference = difference
                    closestTime = possibleTime
                }
            }
        }

        // Check if the closest time is within 90 minutes (5400 seconds)
        if (closestTime != null && minDifference <= 5400) {
            return formatter.format(closestTime) + "Z"
        }

        return target
    }
}


