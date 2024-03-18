package no.uio.ifi.in2000.prosjekt51.ui.information

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.prosjekt51.api.ConnectionResult
import no.uio.ifi.in2000.prosjekt51.api.LocationForecastAPI
import no.uio.ifi.in2000.prosjekt51.repository.WeatherDataRepository

class InformationScreenViewModel: ViewModel() {
    private val locationForecastRepository = WeatherDataRepository(LocationForecastAPI())

    private val _uiState = MutableStateFlow(InformationScreenUiState())
    val uiState: StateFlow<InformationScreenUiState> = _uiState.asStateFlow()

    @RequiresApi(Build.VERSION_CODES.O)  // TODO: Er dette requiresapi-greiene uunngåelig?
    fun fetchLocationForecast(lat: Double, lon: Double, alt: Int) {
        /*
        Fetch LocationForecast-data through repository, and update uistate accordingly.

        arguments:
            lat: latitude between -90 and 90 as a double
            lon: longitude between -180 and 180 as a double
            alt: altitude as an integer
        */

        viewModelScope.launch {
            val (result, repositoryData) = locationForecastRepository.fetchDataFromLocationForecastAPI(lat, lon, alt)
            if (result == true) {
                _uiState.value = InformationScreenUiState(repositoryData)
            } else {
                return@launch // TODO: Update with snackbar or similar
            }
        }
    }

    fun logvalue(){
        /* Brukes bare til testing. Bør fjernes etter hvert, og erstattes av unit tests. */
        Log.d("APITESTING", uiState.value.toString())
    }
}

