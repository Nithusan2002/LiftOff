package no.uio.ifi.in2000.prosjekt51.ui.information

import no.uio.ifi.in2000.prosjekt51.ui.information.data.GribJson
import no.uio.ifi.in2000.prosjekt51.ui.information.data.GribPoint
import no.uio.ifi.in2000.prosjekt51.ui.information.data.timeAndData

data class ResultScreenUiState(
    val locationForecastData: List<timeAndData>? = null, // TODO: Er det beste praksis å initialisere til null?
    val currentLocationForecastData: timeAndData? = null,
    val isobaricGribData: List<GribJson>? = null,
    val currentGribData: List<GribPoint>? = null
)
