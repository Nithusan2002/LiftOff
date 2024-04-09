package no.uio.ifi.in2000.prosjekt51.data.isobaricGrib


import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import no.uio.ifi.in2000.prosjekt51.data.locationForecast.ConnectionResult
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

class IsobaricGribAPI {

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://vast-mountain-52640-528dd291a956.herokuapp.com/")
        .addConverterFactory(ScalarsConverterFactory.create())
        .build()

    private val apiService = retrofit.create(ApiService::class.java)

    suspend fun getJsonDataForTime(time: String): ConnectionResult {
        // Network call on IO Dispatcher
        return try {
            val data = withContext(Dispatchers.IO) {
                apiService.convertGribFile(time)
            }
            ConnectionResult(successfulConnection = true, gribString = data)
        } catch (e: Exception){
            Log.e("ConnectionTimeout", "Couldn't access backend server for grib parsing with time $time, exception $e")
            ConnectionResult(successfulConnection = false)
        }
    }
}

interface ApiService {
    @GET("/convert")
    suspend fun convertGribFile(@Query("time") time: String): String
}