package no.uio.ifi.in2000.prosjekt51.ui.result

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.prosjekt51.calculateTimesToFetch
import no.uio.ifi.in2000.prosjekt51.data.locationForecast.LocationForecastAPI
import no.uio.ifi.in2000.prosjekt51.model.isobaricGrib.GribDataCache
import no.uio.ifi.in2000.prosjekt51.model.isobaricGrib.GribJson
import no.uio.ifi.in2000.prosjekt51.model.isobaricGrib.GribPoint
import no.uio.ifi.in2000.prosjekt51.model.locationForecast.TimeAndData
import no.uio.ifi.in2000.prosjekt51.data.WeatherDataRepository
import no.uio.ifi.in2000.prosjekt51.ui.result.scripts.getGribDataFromCoordinates
import java.time.Instant
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.math.abs

data class ResultScreenUiState(
    val locationForecastData: List<TimeAndData>? = null, // TODO: Er det beste praksis å initialisere til null?
    val currentLocationForecastData: TimeAndData? = null,
    val isobaricGribData: List<GribJson>? = null,
    val currentGribData: List<GribPoint>? = null
)

class ResultScreenViewModel: ViewModel() {
    private val weatherDataRepository = WeatherDataRepository(LocationForecastAPI())

    private val _uiState = MutableStateFlow(ResultScreenUiState())
    val uiState: StateFlow<ResultScreenUiState> = _uiState.asStateFlow()

    @RequiresApi(Build.VERSION_CODES.O)  // TODO: Er dette requiresapi-greiene uunngåelig?
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
            _uiState.update {currentUiState ->
                val result = weatherDataRepository.fetchDataFromLocationForecastAPI(lat, lon, alt)

                if (result.successfulConnection) {
                    var first = result.parsedLocationForecastData.first()
                    for (element in result.parsedLocationForecastData) {
                        if (element.time == time) {
                            first = element
                        }
                    }

                    currentUiState.copy(currentLocationForecastData = first)
                } else {
                    Log.d("MVPTesting", "Failed to fetch location forecast data")
                    return@launch // TODO: Update with snackbar or similar
                }
            }
        }
    }

    fun fetchIsobaricGribFromCache(time: String) {
        /*
        Fetch IsobaricGrib-data through repository, and update uistate accordingly.
        arguments:
            lat: latitude between -90 and 90 as a double
            lon: longitude between -180 and 180 as a double
            alt: altitude as an integer
        */

        viewModelScope.launch {
            _uiState.update { currentUiState ->
                currentUiState.copy(isobaricGribData = getGribData(time))
            }
        }
    }

    fun getGribData(time: String): List<GribJson>? {
        return GribDataCache.getData(time)
    }

    fun getCurrentGribData(lat: Double, lon: Double, time: String){
        _uiState.update { currentUiState ->
            val gribPoints = getGribDataFromCoordinates(lat, lon, getGribData(time))
            currentUiState.copy(isobaricGribData = getGribData(time), currentGribData = gribPoints)
        }
    }

    fun fetchIsobaricGrib(time: String) {
        /*
        Fetch IsobaricGrib-data through repository, and update uistate accordingly.
        arguments:
            lat: latitude between -90 and 90 as a double
            lon: longitude between -180 and 180 as a double
            alt: altitude as an integer
        */

        viewModelScope.launch {
            _uiState.update { currentUiState ->
                val result = weatherDataRepository.fetchDataFromIsobaricGribAPI(time)

                if (result.successfulConnection) {
                    currentUiState.copy(isobaricGribData = result.parsedGribData)
                } else {
                    Log.d("GribTesting", "Failed to fetch gribdata in viewmodel")
                    return@launch // TODO: Update with snackbar or similar
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun checkLaunchConditions(lat: Double, lon: Double, alt: Int = 0, date: Long, hour: Int): String {
        /*
        Checks launch conditions against limit values

        arguments:
            lat: latitude between -90 and 90 as a double
            lon: longitude between -180 and 180 as a double
            alt: altitude as an integer
            date: milliseconds since epoch of 1970-01-01T00:00:00Z as a Long
            hour: hour of the day as an integer

        return:
            String to be displayed on resultScreen
         */

        //Convert hour of the day to milliseconds and add to milliseconds of day, before converting to ISO_8601 date format as a String
        val time: String = Instant.ofEpochMilli(date + hour*60*60*1000).toString()
        val correctedTime = findClosestGribData(time)
        Log.d("GribTesting", "Date looks like: $correctedTime")
        fetchLocationForecast(lat, lon, alt, time)
        getCurrentGribData(lat, lon, correctedTime)
        val launchCheckResult: Boolean
        //TODO Håndtere at tiden ikke finnes.
        val data = uiState.value.locationForecastData?.get(uiState.value.locationForecastData!!.indexOfFirst { it.time == time })?.data

        //If a value is above the limit value or null the result is false
        launchCheckResult = when {
            (data?.wind_speed_of_gust?.compareTo(8.6) ?: 1) > 0 -> false
            (data?.cloud_area_fraction_high?.compareTo(15.0) ?: 1) > 0 -> false
            (data?.cloud_area_fraction_medium?.compareTo(15.0) ?: 1) > 0 -> false
            (data?.cloud_area_fraction_low?.compareTo(5.0) ?: 1) > 0 -> false
            (data?.fog_area_fraction?.compareTo(0.0) ?: 1) > 0 -> false
            (data?.relative_humidity?.compareTo(75.0) ?: 1) > 0 -> false
            (data?.dew_point_temperature?.compareTo(15.0) ?: 1) > 0 -> false
            (data?.precipitation_amount?.compareTo(0) ?: 1) > 0 -> false
            //TODO Legg inn vind i lufta og Shear vind fra isobaric grib
            else -> true
        }

        logvalue()

        return when (launchCheckResult) {
            true -> "Forholdene er innenfor grenseverdiene for oppskytning."
            false -> "Forholdene er ikke innenfor grenseverdiene for oppskytning"
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

    fun logvalue(){
        /* Brukes bare til testing. Bør fjernes etter hvert, og erstattes av unit tests. */
        val gj = fetchIsobaricGrib("2024-03-19T15:00:00Z")
        Log.d("GribTesting", _uiState.value.isobaricGribData?.first().toString())
        //Log.d("GribTesting", "GribObject: ${GribDataCache.gribDataCache}")
    }
}


