package no.uio.ifi.in2000.prosjekt51.data.locationForecast

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.coroutineScope
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlin.math.abs


class LocationForecastAPI {

    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
            })
        }
        defaultRequest {
            header("X-Gravitee-Api-Key", "50ef5733-05b0-47ed-8976-f90a15527894")
        }
    }




    // Check api-level
    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun fetchLocationForecast(lat: Double, lon:Double, alt:Int): ConnectionResult<JsonArray?> = coroutineScope {
        /*
        fetches data from locationforecast API

        arguments:
            lat: latitude between -90 and 90 as a double
            lon: longitude between -180 and 180 as a double
            alt: altitude as an integer

        returns:
            jsonArray of timeseries
         */

        //Check for invalid coordinates
        if (abs(lat) > 90 || abs(lon) > 180) {
            Log.e("Invalid coordinates", "Got latitude $lat, and longitude $lon")  // TODO: Burde kanskje bryte funksjonsflyten her, i tillegg til feilmelding?
            return@coroutineScope ConnectionResult.InputError(Exception())
        }


        //Build url
        val url =
            "https://gw-uio.intark.uh-it.no/in2000/weatherapi/locationforecast/2.0/complete?lat=$lat&lon=$lon&altitude=$alt"
        //Fetch data and parse to jsonElement
        try {
            val response: HttpResponse = client.get(url)
            val jsonString = response.bodyAsText()
            val jsonElement = Json.parseToJsonElement(jsonString)
            //Return jsonArray of timeseries
            val data = jsonElement.jsonObject["properties"]?.jsonObject?.get("timeseries")?.jsonArray
            return@coroutineScope ConnectionResult.Success(data)
        } catch (e: Exception) {
            Log.e("ConnectionTimeout", "Couldn't connect to $url; exception $e")
            return@coroutineScope ConnectionResult.TimeoutError(e)
        }
    }
}


sealed class ConnectionResult<out R> {
    data class Success<out T>(val data: T) : ConnectionResult<T>()
    data class InputError(val exception: Exception) : ConnectionResult<Nothing>()
    data class TimeoutError(val exception: Exception) : ConnectionResult<Nothing>()
}
