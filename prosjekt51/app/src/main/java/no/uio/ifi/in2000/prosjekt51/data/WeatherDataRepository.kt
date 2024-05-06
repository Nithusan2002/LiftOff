package no.uio.ifi.in2000.prosjekt51.data

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.decodeFromJsonElement
import no.uio.ifi.in2000.prosjekt51.data.isobaricGrib.IsobaricGribAPI
import no.uio.ifi.in2000.prosjekt51.data.locationForecast.ConnectionResult
import no.uio.ifi.in2000.prosjekt51.data.locationForecast.LocationForecastAPI
import no.uio.ifi.in2000.prosjekt51.model.isobaricGrib.GribJson
import no.uio.ifi.in2000.prosjekt51.model.locationForecast.LocationForecastCache
import no.uio.ifi.in2000.prosjekt51.model.locationForecast.TimeAndData
import no.uio.ifi.in2000.prosjekt51.model.locationForecast.TimeseriesEntry

class WeatherDataRepository(
    private val locationForecastAPI: LocationForecastAPI = LocationForecastAPI(),
    private val isobaricGribAPI: IsobaricGribAPI = IsobaricGribAPI()
) {

    // TODO: Unit tests. Også for andre filer.

    @RequiresApi(Build.VERSION_CODES.O)
    fun parseTimeseriesJsonArray(jsonArray: JsonArray?): List<TimeAndData> {
        /*
        Parses a jsonArray with Timeseries (from MET API) and serializes it
        to a list of timeAndData-instances from InformationScreenUiState.kt //

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
        return timeseriesList.map { timeseriesEntry ->
            TimeAndData(
                time = timeseriesEntry.time,
                data = timeseriesEntry.data.instant.details,
                nexthourdata = timeseriesEntry.data.next_1_hours?.details
            )
        }
    }

    private fun parseGribJsonString(jsonString: String): List<GribJson> {
        /*
        deserialize a jsonstring of gribdata

        arguments:
            jsonString (String): jsonString containing gribdata

        returns:
            List<GribJson>
         */
        val json = Json { ignoreUnknownKeys = true } // Configure as needed
        return json.decodeFromString(jsonString)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun fetchDataFromLocationForecastAPI(lat: Double, lon: Double, alt: Int): ConnectionResult {
        /* Fetches and deserializes data.
         arguments:
             lat (Double): The latitude of the location.
             lon (Double): The longitude of the location.
             Alt (Int): The altitude of the location.

         Returns:
             ConnectionResult

             If the fetch operation encounters InputError or TimeoutError, it returns a Pair with
             the Boolean value false and an empty list of timeAndData instances.*/

        if (LocationForecastCache.isDataStoredForCoords("$lat;$lon")) {
            Log.d("LocCache", "Fetched from LocationCache")
            val data = LocationForecastCache.getData("$lat;$lon") ?: emptyList()
            return ConnectionResult(
                successfulConnection = true,
                parsedLocationForecastData = data
            )
        } else {
            Log.d("LocCache", "New coords, fetched from api")
            val jsonarr: ConnectionResult = locationForecastAPI.fetchLocationForecast(lat, lon, alt)

            return if (jsonarr.successfulConnection) {
                Log.d("LocCache", "Successful connection")
                jsonarr.parsedLocationForecastData =
                    parseTimeseriesJsonArray(jsonarr.locationForecastData)
                LocationForecastCache.storeData("$lat;$lon", jsonarr.parsedLocationForecastData)
                jsonarr
            } else {
                Log.d("LocCache", "Didn't work")
                jsonarr
            }
        }
    }



    suspend fun fetchDataFromIsobaricGribAPI(time: String): ConnectionResult {
        /*The method performs a network call to obtain JSON data related to isobaric conditions for the given time.
        It logs the result of the fetch operation and returns a Pair containing a Boolean to indicate success or failure,
        and a List of GribJson instances representing the parsed JSON data.

        Arguments:
        time (String): A time string specifying the point in time for which the isobaric conditions data is to be fetched.

        Returns:
        ConnectionResult

        If the fetch operation encounters InputError or TimeoutError, it returns a Pair with
        the Boolean value false and an empty list of GribJson objects.
         */
        val jsonstring = isobaricGribAPI.getJsonDataForTime(time)
        return if (jsonstring.successfulConnection) {
            Log.d("GribTesting", "Successfully fetched jsondata from api: ${parseGribJsonString(jsonstring.gribString)}")
            jsonstring.parsedGribData = parseGribJsonString(jsonstring.gribString)
            Log.d("GribJsonFull", jsonstring.parsedGribData.filter { it.header.parameterNumberName == "Temperature" }
                .map { "${it.header}: ${it.data}" }.toString())
            jsonstring
        } else {
            Log.d("GribTesting", "Fetching failed: $jsonstring")
            jsonstring
        }
    }
}


suspend fun main(){
    val wdr = WeatherDataRepository(LocationForecastAPI(), IsobaricGribAPI())
    val time = "2024-03-15T18:00:00Z"
    val result = wdr.fetchDataFromIsobaricGribAPI(time)

    result.parsedGribData.forEach { gribJson ->
        println(gribJson.header)
    }
}