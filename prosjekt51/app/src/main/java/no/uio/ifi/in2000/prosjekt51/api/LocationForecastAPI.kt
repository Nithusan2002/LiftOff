package no.uio.ifi.in2000.prosjekt51.api

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
import kotlinx.serialization.json.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.coroutineScope
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
            // Replace "Header-Name" with the actual name of the header and
            // "YourApiKey" with your actual API key.
            header("X-Gravitee-Api-Key", "50ef5733-05b0-47ed-8976-f90a15527894")
        }
    }


    // TODO: Gi tid og sted som argument VV
        // TODO: Bygg opp url fra dette VV
        // TODO: Litt feilmeldinger
    // TODO: Returner all info
    // TODO: Data class for mulige variabler (air_temperature, wind_speed et.c.)


    // Check api-level
    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun fetchLocationForecast(lat: Double, lon:Double, alt:Int): JsonArray? = coroutineScope {
        /*
        fetches data from locationforecast API

        arguments:
        lat: latitude between -90 and 90 as a double
        lon: longitude between -180 and 180 as a double
        alt: altidude as an integer

        return:
        jsonArray of timeseries
         */

        //Check for invalid coordinates
        if (abs(lat) > 90 || abs(lon > 180)) {
            Log.d("Invalid coordinates", "Got latitude $lat, and longitude $lon")
        }

        //Build url
        val url =
            "https://gw-uio.intark.uh-it.no/in2000/weatherapi/locationforecast/2.0/complete?lat=$lat&lon=$lon&altitude=$alt"
        //Fetch data and parse to jsonElement
        val response: HttpResponse = client.get(url)
        val jsonString = response.bodyAsText()
        val jsonElement = Json.parseToJsonElement(jsonString)

        //Return jsonArray of timeseries
        return@coroutineScope jsonElement.jsonObject["properties"]?.jsonObject?.get("timeseries")?.jsonArray
    }
}


@RequiresApi(Build.VERSION_CODES.O)
suspend fun main() {
    val lfa = LocationForecastAPI().fetchTemperatureFromLocAndAlt(59.6,10.4,300)
    println(lfa!!.size)
}
