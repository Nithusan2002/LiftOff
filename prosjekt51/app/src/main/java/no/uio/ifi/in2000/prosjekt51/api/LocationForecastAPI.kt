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


    // TODO: Fiks navn, fiks feilmelding, beatuify, kommentarer, docstrings
    // TODO: Utvide funksjonalitet i repository
        // TODO: Lagring av data
        // TODO: Funksjoner for å hente data fra spesifikt tidspunkt
    // TODO: GRIB-API implementasjon på samme vis (forhåpentligvis)
    // TODO: Utforsk andre API-er



    // Check api-level
    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun fetchTemperatureFromLocAndAlt(lat: Double, lon:Double, alt:Int): JsonArray? = coroutineScope {
        if (abs(lat) > 90) {
            Log.d("Invalid latitude", "Got latitude $lat")
        }

        if (abs(lon) > 180) {
            Log.d("Invalid longitude", "Got longitude $lon")
        }


        val url =
            "https://gw-uio.intark.uh-it.no/in2000/weatherapi/locationforecast/2.0/complete?lat=$lat&lon=$lon&altitude=$alt"
        val response: HttpResponse = client.get(url)
        val jsonString = response.bodyAsText()
        val jsonElement = Json.parseToJsonElement(jsonString)

        return@coroutineScope jsonElement.jsonObject["properties"]?.jsonObject?.get("timeseries")?.jsonArray
    }
}


@RequiresApi(Build.VERSION_CODES.O)
suspend fun main() {
    val lfa = LocationForecastAPI().fetchTemperatureFromLocAndAlt(59.6,10.4,300)
    println(lfa!!.size)
}
