package no.uio.ifi.in2000.prosjekt51.ui.information

import kotlinx.serialization.Serializable

data class InformationScreenUiState(
    val locationForecastData: List<timeAndData>? = null // TODO: Er det beste praksis å initialisere til null?
)



// The following three data classes match the json-structure, and therefore are used in deserialization.
@Serializable
data class TimeseriesEntry(
    val time: String,
    val data: InstantDataContainer
)

@Serializable
data class InstantDataContainer(
    val instant: InstantDetails
)

@Serializable
data class InstantDetails(
    val details: locationForecastWeatherData
)


// The following two data classes are the classes we actually want to store in the uistate. // TODO: Sikkert mye bedre/mer effektive måter å gjøre dette på.
@Serializable
data class locationForecastWeatherData(
    val air_pressure_at_sea_level: Double,
    val air_temperature: Double,
    val air_temperature_percentile_10: Double? = null,
    val air_temperature_percentile_90: Double? = null,
    val cloud_area_fraction: Double,
    val cloud_area_fraction_high: Double? = null,
    val cloud_area_fraction_low: Double? = null,
    val cloud_area_fraction_medium: Double? = null,
    val dew_point_temperature: Double,
    val fog_area_fraction: Double? = null,
    val precipitation_amount: Double? = null,
    val relative_humidity: Double,
    val ultraviolet_index_clear_sky: Double? = null,
    val wind_from_direction: Double,
    val wind_speed: Double,
    val wind_speed_of_gust: Double? = null,
    val wind_speed_percentile_10: Double? = null,
    val wind_speed_percentile_90: Double? = null
)

data class timeAndData(
    val time: String,
    val data: locationForecastWeatherData
)