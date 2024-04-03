package no.uio.ifi.in2000.prosjekt51.api


import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
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

    suspend fun getJsonDataForTime(time: String): ConnectionResult<String> {
        // Network call on IO Dispatcher
        return try {
            val data = withContext(Dispatchers.IO) {
                apiService.convertGribFile(time)
            }
            ConnectionResult.Success(data)
        } catch (e: Exception){
            Log.e("ConnectionTimeout", "Couldn't access backend server for grib parsing with time $time, exception $e")
            ConnectionResult.TimeoutError(e)
        }
    }
}

interface ApiService {
    @GET("/convert")
    suspend fun convertGribFile(@Query("time") time: String): String
}