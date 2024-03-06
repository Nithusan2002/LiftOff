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
import no.uio.ifi.in2000.prosjekt51.api.LocationForecastAPI
import no.uio.ifi.in2000.prosjekt51.repository.LocationForecastRepository

class InformationScreenViewModel: ViewModel() {
    private val locationForecastRepository = LocationForecastRepository(LocationForecastAPI())

    private val _uiState = MutableStateFlow(InformationScreenUiState())
    val uiState: StateFlow<InformationScreenUiState> = _uiState.asStateFlow()

    @RequiresApi(Build.VERSION_CODES.O)
    fun fetchLocationForecast(lat: Double, lon: Double, alt: Int) {
        viewModelScope.launch {
            _uiState.value = InformationScreenUiState(locationForecastRepository.fetchDataFromLocationForecastAPI(lat, lon, alt))
        }
    }

    fun logvalue(){
            Log.d("APITESTING", uiState.value.toString())
    }
}

