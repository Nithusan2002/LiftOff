package no.uio.ifi.in2000.prosjekt51.repository

import android.os.Build
import androidx.annotation.RequiresApi
import kotlinx.serialization.json.JsonArray
import no.uio.ifi.in2000.prosjekt51.api.LocationForecastAPI

class LocationForecastRepository(
    private val locationForecastAPI: LocationForecastAPI = LocationForecastAPI()
) {
    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun fetchDataFromLocationForecastAPI(lat: Double, lon: Double, alt: Int): JsonArray? {
        return locationForecastAPI.fetchTemperatureFromLocAndAlt(lat, lon, alt)
    }
}

@RequiresApi(Build.VERSION_CODES.O)
suspend fun main() {
    val lfr = LocationForecastRepository().fetchDataFromLocationForecastAPI(59.6,10.4,300)
    println(lfr!!.size)
}