package no.uio.ifi.in2000.prosjekt51.ui.information.scripts

import android.util.Log
import no.uio.ifi.in2000.prosjekt51.ui.information.data.GribJson
import no.uio.ifi.in2000.prosjekt51.ui.information.data.GribPoint


fun findCoordinateCell(lat: Double, lon: Double, gribJson: GribJson): Pair<Int, Int> {
    if (lat > gribJson.header.la1 || lat < gribJson.header.la2) {
        Log.d("CoordinateError", "Latitude coordinate out of bounds")
        return Pair(0, 0)
    }

    if (lon < gribJson.header.lo1 || lon > gribJson.header.lo2) {
        Log.d("CoordinateError", "Longitude coordinate out of bounds")
        return Pair(0, 0)
    }

    val n = ((lon - gribJson.header.lo1 + gribJson.header.dx / 2) / gribJson.header.dx).toInt()
    val m = ((lat - gribJson.header.la1 + gribJson.header.dy / 2) / gribJson.header.dy).toInt()

    val gridWidth = 120; val gridHeight = 120

    val adjustedN = n.coerceIn(0, gridWidth - 1)
    val adjustedM = m.coerceIn(0, gridHeight - 1)

    return Pair(adjustedN, adjustedM)
}


fun getValueFromGribjson(n: Int, m: Int, gribJson: GribJson): Double{
    return gribJson.data[n*120 + m]
}

fun getGribDataFromCoordinates(lat: Double, lon: Double, grib: List<GribJson>?): MutableList<GribPoint> {
    if (grib == null) {
        return mutableListOf()
    }
    val (n, m) = findCoordinateCell(lat, lon, grib.first())

    // Use a map to collect data by height
    val dataByHeight = mutableMapOf<Double, GribPoint>()

    for (g in grib) {
        val height = g.header.surface1Value
        val value = getValueFromGribjson(n, m, g)

        val gribData = dataByHeight.getOrPut(height) { GribPoint(height, 0.0, 0.0, 0.0) }

        when (g.header.parameterNumberName) {
            "U-component_of_wind" -> gribData.uComponent = value
            "V-component_of_wind" -> gribData.vComponent = value
            "temperature" -> gribData.temperature = value
        }
    }

    // Convert the map values to a list of GribPoint
    return dataByHeight.values.map { GribPoint(it.height, it.vComponent, it.uComponent, it.temperature) }.toMutableList()
}