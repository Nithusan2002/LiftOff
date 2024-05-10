package no.uio.ifi.in2000.prosjekt51.data

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

    private fun parseTimeseriesJsonArray(jsonArray: JsonArray?): List<TimeAndData> {
        /*
        Parses a jsonArray with Timeseries (from MET API) and serializes it
        to a list of timeAndData-instances from InformationScreenUiState.kt

        arguments:
            jsonArray (JsonArray?): A json-array consisting of data from LocationForecast MET-API;
                                    specifically the "timeseries"-part.

        returns:
            List<timeAndData>
         */

        // Check if jsonArray is not null
        if (jsonArray == null) {
            return emptyList()
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
        Deserialize a jsonstring of gribdata

        arguments:
            jsonString (String): jsonString containing gribdata

        returns:
            List<GribJson>
         */
        val json = Json { ignoreUnknownKeys = true }
        return json.decodeFromString(jsonString)
    }

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
            val data = LocationForecastCache.getData("$lat;$lon") ?: emptyList()
            return ConnectionResult(
                successfulConnection = true,
                parsedLocationForecastData = data
            )
        } else {
            val jsonarr: ConnectionResult = locationForecastAPI.fetchLocationForecast(lat, lon, alt)
            return if (jsonarr.successfulConnection) {
                jsonarr.parsedLocationForecastData =
                    parseTimeseriesJsonArray(jsonarr.locationForecastData)
                LocationForecastCache.storeData("$lat;$lon", jsonarr.parsedLocationForecastData)
                jsonarr
            } else {
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
            jsonstring.parsedGribData = parseGribJsonString(jsonstring.gribString)
            jsonstring
        } else {
            jsonstring
        }
    }
}