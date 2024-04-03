package no.uio.ifi.in2000.prosjekt51.model.isobaricGrib

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


data class GribPoint(
    val height: Double,
    var vComponent: Double,
    var uComponent: Double,
    var temperature: Double
)


object GribDataCache {
    var gribDataCache: Map<String, List<GribJson>> = emptyMap()

    fun storeData(timeKey: String, data: List<GribJson>) {
        gribDataCache = gribDataCache.plus(timeKey to data)
    }

    fun getData(timeKey: String): List<GribJson>? = gribDataCache[timeKey]

    fun isDataStoredForTime(timeKey: String): Boolean {
        return gribDataCache.containsKey(timeKey)
    }
}


