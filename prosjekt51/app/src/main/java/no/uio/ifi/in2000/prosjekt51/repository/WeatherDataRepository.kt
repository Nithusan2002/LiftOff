package no.uio.ifi.in2000.prosjekt51.repository

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.decodeFromJsonElement
import no.uio.ifi.in2000.prosjekt51.api.IsobaricGribAPI
import no.uio.ifi.in2000.prosjekt51.api.LocationForecastAPI
import no.uio.ifi.in2000.prosjekt51.ui.information.data.GribJson
import no.uio.ifi.in2000.prosjekt51.ui.information.data.timeAndData
import no.uio.ifi.in2000.prosjekt51.ui.information.data.TimeseriesEntry

class WeatherDataRepository(
    private val locationForecastAPI: LocationForecastAPI = LocationForecastAPI(),
    private val isobaricGribAPI: IsobaricGribAPI = IsobaricGribAPI()
) {

    // TODO: Unit tests. Også for andre filer.

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun parseTimeseriesJsonArray(jsonArray: JsonArray?): List<timeAndData> {
        /*
        Parses a jsonArray with Timeseries (from MET API) and serializes it
        to a list of timeAndData-instances from InformationScreenUiState.kt // TODO: timeAndData bør kanskje få sin egen fil?

        arguments:
            jsonArray (JsonArray?): A json-array consisting of data from LocationForecast MET-API;
                                    specifically the "timeseries"-part.

        returns:
            List<timeAndData>
         */

        // Check if jsonArray is not null
        if (jsonArray == null) {
            return emptyList()  // TODO: Er dette beste praksis?
        }

        val customJson = Json { ignoreUnknownKeys = true }

        // Parse the JsonArray into a list of TimeseriesEntry objects
        val timeseriesList = customJson.decodeFromJsonElement<List<TimeseriesEntry>>(jsonArray)

        // Map each TimeseriesEntry to a timeAndData object, extracting only the relevant data
        return timeseriesList.map { timeseriesEntry ->     // TODO: Per nå returneres kun data innenfor "instant".
            timeAndData(                                   // TODO: Etter hvert burde vi sjekke om den andre dataen er relevant også.
                time = timeseriesEntry.time,
                data = timeseriesEntry.data.instant.details
            )
        }
    }

    suspend fun parseGribJsonString(jsonString: String): List<GribJson> {
        val json = Json { ignoreUnknownKeys = true } // Configure as needed
        return json.decodeFromString(jsonString)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun fetchDataFromLocationForecastAPI(lat: Double, lon: Double, alt: Int): List<timeAndData> {
        /* Fetches and deserializes data. Returns List<timeAndData> */
        val jsonarr = locationForecastAPI.fetchLocationForecast(lat, lon, alt)
        return parseTimeseriesJsonArray(jsonarr)
    }


    suspend fun fetchDataFromIsobaricGribAPI(time: String): List<GribJson> {
        val jsonstring = isobaricGribAPI.getJsonDataForTime(time)
        return parseGribJsonString(jsonstring)
    }
}


suspend fun main(){
    val time = "2024-03-13T18:00:00Z"
    val gribJsonList = WeatherDataRepository().fetchDataFromIsobaricGribAPI(time)

    gribJsonList.forEach { gribJson ->
        println(gribJson.header)
    }

}
