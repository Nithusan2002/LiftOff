package no.uio.ifi.in2000.prosjekt51.ui.result

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.prosjekt51.CRITICAL_AIR_HUMIDITY
import no.uio.ifi.in2000.prosjekt51.CRITICAL_AIR_HUMIDITY_80
import no.uio.ifi.in2000.prosjekt51.CRITICAL_CLOUDCOVER_HIGHALT
import no.uio.ifi.in2000.prosjekt51.CRITICAL_CLOUDCOVER_HIGHALT_80
import no.uio.ifi.in2000.prosjekt51.CRITICAL_CLOUDCOVER_LOWALT
import no.uio.ifi.in2000.prosjekt51.CRITICAL_CLOUDCOVER_LOWALT_80
import no.uio.ifi.in2000.prosjekt51.CRITICAL_DEW_POINT_TEMP
import no.uio.ifi.in2000.prosjekt51.CRITICAL_DEW_POINT_TEMP_80
import no.uio.ifi.in2000.prosjekt51.CRITICAL_PRECIPITATION
import no.uio.ifi.in2000.prosjekt51.CRITICAL_PRECIPITATION_80
import no.uio.ifi.in2000.prosjekt51.CRITICAL_WIND_ALTITUDE
import no.uio.ifi.in2000.prosjekt51.CRITICAL_WIND_ALTITUDE_80
import no.uio.ifi.in2000.prosjekt51.CRITICAL_WIND_GUST
import no.uio.ifi.in2000.prosjekt51.CRITICAL_WIND_GUST_80
import no.uio.ifi.in2000.prosjekt51.CRITICAL_WIND_SHEAR
import no.uio.ifi.in2000.prosjekt51.CRITICAL_WIND_SHEAR_80
import no.uio.ifi.in2000.prosjekt51.MAX_HEIGHT
import no.uio.ifi.in2000.prosjekt51.calculateTimesToFetch
import no.uio.ifi.in2000.prosjekt51.data.WeatherDataRepository
import no.uio.ifi.in2000.prosjekt51.data.locationForecast.LocationForecastAPI
import no.uio.ifi.in2000.prosjekt51.model.isobaricGrib.GribDataCache
import no.uio.ifi.in2000.prosjekt51.model.isobaricGrib.GribJson
import no.uio.ifi.in2000.prosjekt51.model.isobaricGrib.GribPoint
import no.uio.ifi.in2000.prosjekt51.model.locationForecast.LocationForecastWeatherData
import no.uio.ifi.in2000.prosjekt51.model.locationForecast.LocationForecastWeatherNextHourData
import no.uio.ifi.in2000.prosjekt51.model.locationForecast.TimeAndData
import no.uio.ifi.in2000.prosjekt51.ui.result.scripts.getGribDataFromCoordinates
import no.uio.ifi.in2000.prosjekt51.ui.result.scripts.pressureToHeight
import no.uio.ifi.in2000.prosjekt51.ui.theme.badConditionsContainerLight
import no.uio.ifi.in2000.prosjekt51.ui.theme.edgeConditionsContainerLight
import no.uio.ifi.in2000.prosjekt51.ui.theme.goodConditionsContainerLight
import no.uio.ifi.in2000.prosjekt51.ui.theme.onBadConditionsContainerLight
import no.uio.ifi.in2000.prosjekt51.ui.theme.onEdgeConditionsContainerLight
import no.uio.ifi.in2000.prosjekt51.ui.theme.onGoodConditionsContainerLight
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import kotlin.math.abs

data class VisualResultScreenUiState(
    val locationForecastData: List<TimeAndData>? = null,
    val currentLocationForecastData: TimeAndData? = null,
    val isobaricGribData: List<GribJson>? = null,
    val currentGribData: List<GribPoint>? = null,
    val launchWindowsData: List<LaunchWindow>? = null,
    val error: String? = null,
    val isLoading: Boolean = true,
    val windCondition: Int = 2,
    val sightCondition: Int = 2,
    val precipitationCondition: Int = 0,
    val airCondition: Int = 0,
    val height: Double = MAX_HEIGHT.toDouble(),
    val maxWindSpeed: Double? = null,
    val maxWindShear: Double? = null
) {
    val hasError: Boolean
        get() = error != null
}

class ResultScreenViewModel: ViewModel() {
    private val weatherDataRepository = WeatherDataRepository(LocationForecastAPI())


    private val _visualResultScreenUiState = MutableStateFlow(VisualResultScreenUiState())
    val visualResultScreenUiState: StateFlow<VisualResultScreenUiState> = _visualResultScreenUiState.asStateFlow()

    fun fetchData(
        lat: Double,
        lon:Double,
        alt: Int = 0,
        date: Long,
        hour: String,
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

        viewModelScope.launch {
            try {
                updateLoading(true)
                val time: String = Instant.ofEpochMilli(date + hour.toInt() * 60 * 60 * 1000).toString()
                // Correct time by forcing time to closest 3-hour-interval value
                val correctedTime = findClosestGribData(time)
                fetchLocationForecast(lat, lon, alt, time)
                getCurrentGribData(lat, lon, correctedTime)
                if (height != null) {
                    updateHeight(height)
                }

                updateLoading(false)
            } catch (e: Exception) {
                _visualResultScreenUiState.update { currentUiState ->
                    currentUiState.copy(
                        error = e.message,
                        isLoading = false)
                }
            }
        }
    }

    private fun updateLoading(value: Boolean) {
        _visualResultScreenUiState.update { currentUiState ->
            currentUiState.copy(
                isLoading = value)
        }
    }


    private fun checkLaunchConditions() {
        /*
        Checks launch conditions against limit values

        return:
            String to be displayed on resultScreen
         */

        val launchCheckResult: List<Int>

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
                airCondition = launchCheckResult[3],
                error = null)
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

    private fun checkWindCondition(lfwData: LocationForecastWeatherData?, launch: Boolean = false): Int {
        /*
          Calculates maximum wind speed and maximum wind shear for height,
          and checks whether wind values are within limits

          arguments:
              lfwData: LocationForecastWeatherData-instance of the given time and coordinates

          returns:
              Int - 0 means condition is fulfilled; 2 means condition is not fulfilled; 1 means
              condition is not fulfilled, but within 20% of limit value
       */
        val maxWindSpeed = if (launch) null else findMaximumAirWindSpeed(visualResultScreenUiState.value.height,
            visualResultScreenUiState.value.currentLocationForecastData?.data?.air_pressure_at_sea_level ?: 0.0,
            visualResultScreenUiState.value.currentLocationForecastData?.data?.air_temperature ?: 0.0
        )

        val maxWindShear = if (launch) null else findMaximumWindShear(visualResultScreenUiState.value.height,
            visualResultScreenUiState.value.currentLocationForecastData?.data?.air_pressure_at_sea_level ?: 0.0,
            visualResultScreenUiState.value.currentLocationForecastData?.data?.air_temperature ?: 0.0
        )

        if (!launch) {
            _visualResultScreenUiState.update { currentUiState ->
                currentUiState.copy(maxWindSpeed = maxWindSpeed, maxWindShear = maxWindShear)
            }
        }

        val result = when {
            (lfwData?.wind_speed_of_gust?.compareTo(CRITICAL_WIND_GUST) ?: -1) > 0 -> 2
            (maxWindSpeed?.compareTo(CRITICAL_WIND_ALTITUDE) ?: -1) > 0 -> 2
            (maxWindShear?.compareTo(CRITICAL_WIND_SHEAR) ?: -1) > 0 -> 2
            (lfwData?.wind_speed_of_gust?.compareTo(CRITICAL_WIND_GUST_80) ?: -1) > 0 -> 1
            (maxWindSpeed?.compareTo(CRITICAL_WIND_ALTITUDE_80) ?: -1) > 0 -> 1
            (maxWindShear?.compareTo(CRITICAL_WIND_SHEAR_80) ?: -1) > 0 -> 1
            else -> 0
        }
        return result
    }

    private fun checkSightCondition(lfwData: LocationForecastWeatherData?): Int {
        /*
          Checks whether sight values are within limits

          arguments:
              lfwData: LocationForecastWeatherData-instance of the given time and coordinates

          returns:
              Int - 0 means condition is fulfilled; 2 means condition is not fulfilled; 1 means
              condition is not fulfilled, but within 20% of limit value
        */
        return when {
            (lfwData?.cloud_area_fraction_high?.compareTo(CRITICAL_CLOUDCOVER_HIGHALT) ?: 1) > 0 -> 2
            (lfwData?.cloud_area_fraction_medium?.compareTo(CRITICAL_CLOUDCOVER_HIGHALT) ?: 1) > 0 -> 2
            (lfwData?.cloud_area_fraction_low?.compareTo(CRITICAL_CLOUDCOVER_LOWALT) ?: 1) > 0 -> 2
            (lfwData?.cloud_area_fraction_high?.compareTo(CRITICAL_CLOUDCOVER_HIGHALT_80) ?: 1) > 0 -> 1
            (lfwData?.cloud_area_fraction_medium?.compareTo(CRITICAL_CLOUDCOVER_HIGHALT_80) ?: 1) > 0 -> 1
            (lfwData?.cloud_area_fraction_low?.compareTo(CRITICAL_CLOUDCOVER_LOWALT_80) ?: 1) > 0 -> 1
            else -> 0
        }
    }

    private fun checkPrecipitationCondition(lfwData: LocationForecastWeatherNextHourData?): Int {
        /*
          Checks whether precipitation values are within limits

          arguments:
              lfwData: LocationForecastWeatherData-instance of the given time and coordinates

          returns:
              Int - 0 means condition is fulfilled; 2 means condition is not fulfilled; 1 means
              condition is not fulfilled, but within 20% of limit value
        */
        return when {
            (lfwData?.precipitation_amount?.compareTo(CRITICAL_PRECIPITATION) ?: -1) > 0 -> 2
            (lfwData?.precipitation_amount?.compareTo(CRITICAL_PRECIPITATION_80) ?: -1) > 0 -> 1
            else -> 0
        }
    }

    private fun checkAirCondition(lfwData: LocationForecastWeatherData?): Int {
        /*
          Checks whether humidity- and temperature values are within limits

          arguments:
              lfwData: LocationForecastWeatherData-instance of the given time and coordinates

          returns:
              Int - 0 means condition is fulfilled; 2 means condition is not fulfilled; 1 means
              condition is not fulfilled, but within 20% of limit value
        */
        return when {
            (lfwData?.relative_humidity?.compareTo(CRITICAL_AIR_HUMIDITY) ?: 1) > 0 -> 2
            (lfwData?.dew_point_temperature?.compareTo(CRITICAL_DEW_POINT_TEMP) ?: 1) > 0 -> 2
            (lfwData?.relative_humidity?.compareTo(CRITICAL_AIR_HUMIDITY_80) ?: 1) > 0 -> 1
            (lfwData?.dew_point_temperature?.compareTo(CRITICAL_DEW_POINT_TEMP_80) ?: 1) > 0 -> 1
            else -> 0
        }
    }

    private fun findMaximumAirWindSpeed(height: Double, P_b: Double, t_b: Double): Double? {
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

    private fun findMaximumWindShear(height: Double, P_b: Double, t_b: Double): Double? {/*
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

    fun fetchLaunchWindows() {
        updateLoading(true)
        val updatedLaunchWindows: MutableList<LaunchWindow> = mutableListOf()
        visualResultScreenUiState.value.locationForecastData?.forEach {
            if (
                (checkWindCondition(it.data, // NOTE: Currently does not check windspeed and -shear
                                             // values
                    launch = true
                    ) == 0)&&
                (checkPrecipitationCondition(it.nexthourdata) == 0)&&
                (checkAirCondition(it.data) == 0)
            &&(checkSightCondition(it.data) == 0))
            {
                updatedLaunchWindows.add(
                    LaunchWindow(
                        it.time,
                        goodConditionsContainerLight,
                        onGoodConditionsContainerLight)
                )
            } else if ((checkWindCondition(it.data,
                launch = true) == 2)||
                (checkPrecipitationCondition(it.nexthourdata) == 2)||
                (checkAirCondition(it.data) == 2)||
                (checkSightCondition(it.data) == 2))
            {
                updatedLaunchWindows.add(
                    LaunchWindow(
                        it.time,
                        badConditionsContainerLight,
                        onBadConditionsContainerLight
                    )
                )
            } else {
                updatedLaunchWindows.add(
                    LaunchWindow(
                        it.time,
                        edgeConditionsContainerLight,
                        onEdgeConditionsContainerLight
                    )
                )
            }
        }
        _visualResultScreenUiState.update {
            it.copy(launchWindowsData = updatedLaunchWindows)
        }
        updateLoading(false)
    }

    private fun getCurrentGribData(lat: Double, lon: Double, time: String){
        _visualResultScreenUiState.update { currentUiState ->
            val gribPoints = getGribDataFromCoordinates(lat, lon, getGribData(time))
            currentUiState.copy(isobaricGribData = getGribData(time), currentGribData = gribPoints)
        }
        checkLaunchConditions()
    }

    private fun fetchLocationForecast(lat: Double, lon: Double, alt: Int, time: String) {
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
                    currentUiState.copy(currentLocationForecastData = first, locationForecastData = result.parsedLocationForecastData, error = null)
                }
                checkLaunchConditions()
            } catch (e: Exception) {
                _visualResultScreenUiState.value = _visualResultScreenUiState.value.copy(error = "Kan ikke hente locationforecast data")
            }
        }

    }

    fun convertDateToEpochMilli(dateString: String): Long {
        /*
        Converts string of date to milliseconds passed between 1. Jan 1970 to that date
        */
        val newDateString = dateString + "T00:00:00"
        val correctDateString = LocalDateTime.parse(newDateString)
        Log.d("LaunchWindows", "Convert: $correctDateString")
        val dateAtMidnight = correctDateString.toLocalDate().atStartOfDay()
        return dateAtMidnight.toInstant(ZoneOffset.UTC).toEpochMilli()
    }


    private fun findClosestGribData(target: String): String {
        /*
        Finds the nearest time for the given target of the following: 00, 03, 06, 09, 12, 15, 18, 21
        */
        val possTimes = calculateTimesToFetch()
        val formatter = DateTimeFormatter.ISO_DATE_TIME
        val targetDateTime = LocalDateTime.parse(target, formatter)

        var closestTime: LocalDateTime? = null
        var minDifference = Int.MAX_VALUE

        for (time in possTimes) {
            val possibleTime = LocalDateTime.parse(time, formatter)
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


