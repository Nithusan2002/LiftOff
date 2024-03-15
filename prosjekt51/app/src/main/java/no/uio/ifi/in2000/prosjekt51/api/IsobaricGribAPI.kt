package no.uio.ifi.in2000.prosjekt51.api


import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("/convert")
    suspend fun convertGribFile(@Query("time") time: String): String
}

class IsobaricGribAPI {

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://vast-mountain-52640-528dd291a956.herokuapp.com/")
        .addConverterFactory(ScalarsConverterFactory.create())
        .build()

    private val apiService = retrofit.create(ApiService::class.java)

    suspend fun getJsonDataForTime(time: String): String {
        // Network call on IO Dispatcher
        return withContext(Dispatchers.IO) {
            apiService.convertGribFile(time)
        }
    }
}

