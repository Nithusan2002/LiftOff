package no.uio.ifi.in2000.prosjekt51.ui.information

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.prosjekt51.api.LocationForecastAPI
import no.uio.ifi.in2000.prosjekt51.repository.WeatherDataRepository
import no.uio.ifi.in2000.prosjekt51.ui.information.data.GribDataCache
import no.uio.ifi.in2000.prosjekt51.ui.information.data.GribJson
import no.uio.ifi.in2000.prosjekt51.ui.information.data.GribPoint
import no.uio.ifi.in2000.prosjekt51.ui.information.scripts.getGribDataFromCoordinates
import java.time.Instant

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
            val (result, lfData) = weatherDataRepository.fetchDataFromLocationForecastAPI(lat, lon, alt)
            if (result == true) {
                var first = lfData.first()
                for (element in lfData) {
                    if (element.time == time) {
                        first = element
                    }
                }
                Log.d("MVPTesting", "LFdata is now $lfData")
                val lfdata = _uiState.value.locationForecastData  // TODO: Fiks denne uhåndterbare måten å opprettholde uistate på
                val gribdata = _uiState.value.isobaricGribData
                val curgrib = _uiState.value.currentGribData
                _uiState.value = ResultScreenUiState(lfdata, first, gribdata, curgrib)
            } else {
                Log.d("MVPTesting", "Failed to fetch location forecast data")
                return@launch // TODO: Update with snackbar or similar
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
            val lfdata = _uiState.value.locationForecastData
            val curlfdata = _uiState.value.currentLocationForecastData
            val curgrib = _uiState.value.currentGribData
            _uiState.value = ResultScreenUiState(lfdata, curlfdata, getGribData(time), curgrib)
        }
    }

    fun getGribData(time: String): List<GribJson>? {
        return GribDataCache.getData(time)
    }

    fun getCurrentGribData(lat: Double, lon: Double, time: String){
        val gribPoints = getGribDataFromCoordinates(lat, lon, getGribData(time))
        val lfdata = _uiState.value.locationForecastData
        val curlfdata = _uiState.value.currentLocationForecastData
        Log.d("GribTesting", "gribPoints: $gribPoints")
        _uiState.value = ResultScreenUiState(lfdata, curlfdata, getGribData(time), gribPoints)
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
            val (result, gribdata) = weatherDataRepository.fetchDataFromIsobaricGribAPI(time)

            if (result == true) {
                val lfdata = _uiState.value.locationForecastData
                val curlfdata = _uiState.value.currentLocationForecastData
                val curgrib = _uiState.value.currentGribData
                _uiState.value = ResultScreenUiState(lfdata, curlfdata, gribdata, curgrib)
                Log.d("GribTesting", "gribdata is now $gribdata")
            } else {
                Log.d("GribTesting", "Failed to fetch gribdata in viewmodel")
                return@launch // TODO: Update with snackbar or similar
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
        fetchLocationForecast(lat, lon, alt, time)
        getCurrentGribData(lat, lon, time)
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

    fun logvalue(){
        /* Brukes bare til testing. Bør fjernes etter hvert, og erstattes av unit tests. */
        val gj = fetchIsobaricGrib("2024-03-19T15:00:00Z")
        Log.d("GribTesting", _uiState.value.isobaricGribData?.first().toString())
        //Log.d("GribTesting", "GribObject: ${GribDataCache.gribDataCache}")
    }
}


