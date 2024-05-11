package no.uio.ifi.in2000.prosjekt51.model.isobaricGrib

import kotlinx.serialization.Serializable

@Serializable
data class GribJson(
    /*A serializable data class representing the JSON response structure for Grib data.
     It contains header information and a list of data points.

     properties:
        header: The metadata header describing the specifics of the Grib data.
        data: A list of data points representing the actual Grib data values.
     */
    val header: GribHeader,
    val data: List<Double>
)

@Serializable
data class GribHeader(
    /*
     A serializable data class for the header section of the Grib API response.
     It contains reference time, parameters, geographic information, and grid spacing details.

     properties:
        refTime The reference time for the forecast.
        parameterNumberName The name of the parameter (e.g., temperature, wind).
        forecastTime The forecast time in hours since the reference time.
        surface1Value The value of the surface level for the data.
        lo1 The starting longitude value of the area.
        lo2 The ending longitude value of the area.
        la1 The starting latitude value of the area.
        la2 The ending latitude value of the area.
        dx The longitudinal grid spacing.
        dy The latitudinal grid spacing.
     */
    val refTime: String,
    val parameterNumberName: String,
    val forecastTime: Int,
    val surface1Value: Double,
    val lo1: Double,
    val lo2: Double,
    val la1: Double,
    val la2: Double,
    val dx: Double,
    val dy: Double
)


data class GribPoint(
    /*A data class representing a single point of Grib data.
    It holds the height and atmospheric variables at a specific point.

    properties:
        height (Double): The height at which the Grib data is measured.
        vComponent(Double): The vertical component of the wind at the point.
        uComponent(Double): The horizontal component of the wind at the point.
        temperature(Double): The temperature at the point.
        wind (Double): Wind speed
        winddir (Double): The direction of the wind
        windshear (Double): The shear of the wind compared to the athmospheric layer below
     */
    val height: Double,
    var vComponent: Double,
    var uComponent: Double,
    var temperature: Double,
    var wind: Double,
    var winddir: Double,
    var windshear: Double
)


object GribDataCache {
    /*A singleton object serving as a cache for storing and retrieving GribJson data.
    It provides methods to interact with the cache by time keys.

    Functions:
     * storeData - stores a list of GribJson associated with a specific time key.
     * getData - retrieves a list of GribJson associated with a specific time key.
     * isDataStoredForTime - checks if data is stored in the cache for a specific time key.

     properties:
        gribDataCache A map structure that holds time keys associated with lists of GribJson data.
     */
    private var gribDataCache: Map<String, List<GribJson>> = emptyMap()

    fun storeData(timeKey: String, data: List<GribJson>) {
        gribDataCache = gribDataCache.plus(timeKey to data)
    }

    fun getData(timeKey: String): List<GribJson>? = gribDataCache[timeKey]

    fun isDataStoredForTime(timeKey: String): Boolean {
        return gribDataCache.containsKey(timeKey)
    }
}


