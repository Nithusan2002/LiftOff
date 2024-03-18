package no.uio.ifi.in2000.prosjekt51.ui.information

import kotlinx.serialization.Serializable
import no.uio.ifi.in2000.prosjekt51.ui.information.data.timeAndData

data class InformationScreenUiState(
    val locationForecastData: List<timeAndData>? = null // TODO: Er det beste praksis å initialisere til null?
)
