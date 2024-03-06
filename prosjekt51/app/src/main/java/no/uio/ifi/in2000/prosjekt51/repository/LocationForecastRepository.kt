package no.uio.ifi.in2000.prosjekt51.repository

import android.os.Build
import androidx.annotation.RequiresApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.decodeFromJsonElement
import no.uio.ifi.in2000.prosjekt51.api.LocationForecastAPI
import no.uio.ifi.in2000.prosjekt51.ui.information.TimeseriesEntry
import no.uio.ifi.in2000.prosjekt51.ui.information.timeAndData

class LocationForecastRepository(
    private val locationForecastAPI: LocationForecastAPI = LocationForecastAPI()
) {

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun parseTimeseriesJsonArray(jsonArray: JsonArray?): List<timeAndData> {
        // Check if jsonArray is not null
        if (jsonArray == null) {
            return emptyList()
        }

        val customJson = Json { ignoreUnknownKeys = true }

        // Parse the JsonArray into a list of TimeseriesEntry objects
        val timeseriesList = customJson.decodeFromJsonElement<List<TimeseriesEntry>>(jsonArray)

        // Map each TimeseriesEntry to a timeAndData object, extracting only the relevant data
        return timeseriesList.map { timeseriesEntry ->
            timeAndData(
                time = timeseriesEntry.time,
                data = timeseriesEntry.data.instant.details
            )
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun fetchDataFromLocationForecastAPI(lat: Double, lon: Double, alt: Int): List<timeAndData> {
        val jsonarr = locationForecastAPI.fetchTemperatureFromLocAndAlt(lat, lon, alt)
        return parseTimeseriesJsonArray(jsonarr)
    }
}

@RequiresApi(Build.VERSION_CODES.O)
suspend fun main() {
    val lfr = LocationForecastRepository().fetchDataFromLocationForecastAPI(59.6,10.4,300)
    println(lfr.size)
}