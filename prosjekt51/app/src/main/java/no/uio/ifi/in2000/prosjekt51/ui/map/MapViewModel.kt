package no.uio.ifi.in2000.prosjekt51.ui.map

import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class MapViewModel : ViewModel() {
    /*
    View model for MapScreen, managing selected latitude, longitude, as well
    as display state of buttons and pop up dialog.
    */
    private val _selectedLatLng = MutableStateFlow<LatLng?>(null)
    val selectedLatLng = _selectedLatLng.asStateFlow()

    private val _showSaveButton = MutableStateFlow(false)
    val showSaveButton = _showSaveButton.asStateFlow()

    private val _showSaveDialog = MutableStateFlow(false)
    val showSaveDialog = _showSaveDialog.asStateFlow()

    fun selectLocation(latLng: LatLng) {
        _selectedLatLng.value = latLng
        _showSaveButton.value = true
    }

    fun toggleSaveDialog(show: Boolean) {
        _showSaveDialog.value = show
    }
}
