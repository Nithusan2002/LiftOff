package no.uio.ifi.in2000.prosjekt51.ui.home

import no.uio.ifi.in2000.prosjekt51.ui.information.timeAndData

data class HomeScreenUiState(
    val locationForecastData: List<timeAndData>? = null
)