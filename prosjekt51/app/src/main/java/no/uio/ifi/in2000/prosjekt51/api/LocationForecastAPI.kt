package no.uio.ifi.in2000.prosjekt51.api

import android.content.res.Resources
import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpHeaders
import io.ktor.http.headers
import kotlinx.serialization.json.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.coroutineScope
import no.uio.ifi.in2000.blparton.oblig2.R


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


    // TODO: Gi tid og sted som argument
        // TODO: Bygg opp url fra dette
    // TODO: Returner all info
    // TODO: Data class for mulige variabler (air_temperature, wind_speed et.c.)


    suspend fun fetchTemperatureFromAPI(): Double = coroutineScope {

        val url =
            "https://gw-uio.intark.uh-it.no/in2000/weatherapi/locationforecast/2.0/complete?lat=59.93&lon=10.72&altitude=90"
        val response: HttpResponse = client.get(url)

        val jsonString = response.bodyAsText()
        val jsonElement = Json.parseToJsonElement(jsonString)

        val timeseries =
            jsonElement.jsonObject["properties"]?.jsonObject?.get("timeseries")?.jsonArray
        var temperature = 0.0
        timeseries?.forEach { element ->
            val time = element.jsonObject["time"]?.jsonPrimitive?.content
            if (time == "2024-03-05T12:00:00Z") {
                val details =
                    element.jsonObject["data"]?.jsonObject?.get("instant")?.jsonObject?.get("details")?.jsonObject
                temperature = details?.get("air_temperature")?.jsonPrimitive?.double ?: 0.0
                return@coroutineScope temperature
            }
        }
        return@coroutineScope 0.0
    }
}


suspend fun main() {
    val lfa = LocationForecastAPI()
    val temp = lfa.fetchTemperatureFromAPI()
    println("Temperature is $temp")
}
