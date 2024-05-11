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
    /*
      Data source class for fetching Isobaric Grib data from the IsobaricGribAPI.
      The class uses retrofit for making network requests and converts the response
      to a string format using ScalarsConverterFactory.
     */

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://vast-mountain-52640-528dd291a956.herokuapp.com/")
        .addConverterFactory(ScalarsConverterFactory.create())
        .build()

    private val apiService = retrofit.create(ApiService::class.java)

    suspend fun getJsonDataForTime(time: String): ConnectionResult {
        /*
        fetches data from the IsobaricGribAPI
        arguments:
            time: a string with the time for which Grib data is requested.

        returns: ConnectionResult object, either success and the associated data is returned,
            or an exception, either inputError og TimeoutError is returned
         */

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
    /*
    Interface for requesting gribdata from backend server
     */
    @GET("/convert")
    suspend fun convertGribFile(@Query("time") time: String): String
}