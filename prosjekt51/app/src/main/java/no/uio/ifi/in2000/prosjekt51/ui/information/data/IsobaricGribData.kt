package no.uio.ifi.in2000.prosjekt51.ui.information.data

import kotlinx.serialization.Serializable


@Serializable
data class GribJson(
    val header: GribHeader,
    val data: List<Double>
)

@Serializable
data class GribHeader(
    val refTime: String,
    val parameterNumberName: String,
    val forecastTime: Int,
    val surface1Value: Double,
    val lo1: Double,
    val lo2: Double,
    val la1: Double,
    val la2: Double,
    val dx: Double,
    val dy: Double
)

