package no.uio.ifi.in2000.prosjekt51.model.locationForecast

import kotlinx.serialization.Serializable


// The following three data classes match the json-structure, and therefore are used in deserialization.
@Serializable
data class TimeseriesEntry(
    /*
     A serializable data class which represents an entry of Timeseries data from LocationForecast API.
     It contains a time string and an instance of InstantDataContainer.

     properties:
        time: A String indicating the specific timestamp of the Timeseries data.
        data: An Instance of InstantDataContainer representing the weather conditions at the given time.
     */
    val time: String,
    val data: InstantDataContainer,
)

@Serializable
data class InstantDataContainer(
    /*A serializable data class which acts as a container for instant weather details fetched from LocationForecast API.

     properties:
        instant: An instance of InstantDetails representing the detailed weather conditions at a specific time.
        next_1_hours: An instance of Next_1_Hours, representing data from the next hour, beginning at the given time
     */
    val instant: InstantDetails,
    val next_1_hours: NextHourDetails? = null
)

@Serializable
data class InstantDetails(
    /*A serializable data class which holds the minutiae of weather conditions at a specific time.

     properties:
        details: An Instance of LocationForecastWeatherData representing the exact weather conditions/details.
     */
    val details: LocationForecastWeatherData
)

@Serializable
data class NextHourDetails(
    /*A serializable data class which holds the minutiae of weather conditions at a specific time, plus one hour.

     properties:
        details: An Instance of LocationForecastWeatherData representing the exact weather conditions/details during the next hour.
     */
    val details: LocationForecastWeatherNextHourData
)


// The following three data classes are the classes we actually want to store in the uistate.
@Serializable
data class LocationForecastWeatherData(
    /* A serializable data class representing the detailed weather condition data at a specific time.
     It contains information about air pressure, temperature, cloud area fraction, relative humidity,
     direction and speed of wind, dew point temperature, etc.
     */
    val air_pressure_at_sea_level: Double,
    val air_temperature: Double,
    val air_temperature_percentile_10: Double? = null,
    val air_temperature_percentile_90: Double? = null,
    val cloud_area_fraction: Double,
    val cloud_area_fraction_high: Double? = null,
    val cloud_area_fraction_low: Double? = null,
    val cloud_area_fraction_medium: Double? = null,
    val dew_point_temperature: Double,
    val fog_area_fraction: Double? = null,
    val relative_humidity: Double,
    val ultraviolet_index_clear_sky: Double? = null,
    val wind_from_direction: Double,
    val wind_speed: Double,
    val wind_speed_of_gust: Double? = null,
    val wind_speed_percentile_10: Double? = null,
    val wind_speed_percentile_90: Double? = null
)

@Serializable
data class LocationForecastWeatherNextHourData(
    /* A serializable data class representing the detailed weather condition data during the next hour
    from a specific time. It contains information about different precipitation parameters.
     */
    val precipitation_amount: Double? = null,
    val precipitation_amount_max: Double? = null,
    val precipitation_amount_min: Double? = null,
    val probability_of_precipitation: Double? = null,
    val probability_of_thunder: Double? = null
)



data class TimeAndData(
    /*
     Represents a timestamp and its corresponding weather data.

     properties:
     time: A String indicating the specific timestamp of the weather data.
     data: An Instance of LocationForecastWeatherData representing the detailed weather conditions at the specified time.
     nexthourdata: An instance of LocationForecastWeatherNextHourData, representing weather data for the coming hour
     */
    val time: String,
    val data: LocationForecastWeatherData,
    val nexthourdata: LocationForecastWeatherNextHourData?
)


object LocationForecastCache {
    /*A singleton object serving as a cache for storing and retrieving LocationForecast data.
    It provides methods to interact with the cache by time keys.

    Functions:
     * storeData - stores a list of TimeAndData associated with a specific time key.
     * getData - retrieves a list of TimeAndData associated with a specific time key.
     * isDataStoredForTime - checks if data is stored in the cache for a specific time key.

     properties:
        gribDataCache A map structure that holds time keys associated with lists of GribJson data.
     */
    private var locationForecastCache: Map<String, List<TimeAndData>> = emptyMap()

    fun storeData(coordKey: String, data: List<TimeAndData>) {
        locationForecastCache = locationForecastCache.plus(coordKey to data)
    }

    fun getData(coordKey: String): List<TimeAndData>? = locationForecastCache[coordKey]

    fun isDataStoredForCoords(coordKey: String): Boolean {
        /*Coord is lat;lon, like so: "14.3;67.928" */
        return locationForecastCache.containsKey(coordKey)
    }
}