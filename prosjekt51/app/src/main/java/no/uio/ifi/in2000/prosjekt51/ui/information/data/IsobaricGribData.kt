package no.uio.ifi.in2000.prosjekt51.ui.information.data

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import no.uio.ifi.in2000.prosjekt51.repository.WeatherDataRepository
import no.uio.ifi.in2000.prosjekt51.ui.information.ResultScreenViewModel
import java.time.Clock
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


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


