package no.uio.ifi.in2000.prosjekt51.ui.result.scripts

import no.uio.ifi.in2000.prosjekt51.INVALID_GRIB
import no.uio.ifi.in2000.prosjekt51.model.isobaricGrib.GribJson
import no.uio.ifi.in2000.prosjekt51.model.isobaricGrib.GribPoint




fun findCoordinateCell(lat: Double, lon: Double, gribJson: GribJson): Pair<Int, Int> {
    /*Determines the indices (`n`, `m`) of the closest data cell in the GribJson data grid for a given latitude and longitude.
     If the given coordinates are out of bounds based on the GribJson header information, logs an error and returns (0, 0).

     Arguments:
        lat (Double): The latitude coordinate to find the data cell for.
        lon (Double): The longitude coordinate to find the data cell for.
        gribJson (Double): The GribJson object containing header and data which defines the grid spacing and bounds.
     Returns:
        A Pair of Integers indicating the indices (`n`, `m`) in the data grid corresponding to the coordinates.
     */

    // If latitude or longitude invalid, return pair of predetermined invalid values
    if (lat < gribJson.header.la1 || lat > gribJson.header.la2) {
        return Pair(INVALID_GRIB, INVALID_GRIB)
    }

    val fittedlon = if (gribJson.header.lo1 > 180) gribJson.header.lo1 - 360 else gribJson.header.lo1

    if (lon < fittedlon || lon > gribJson.header.lo2) {
        return Pair(INVALID_GRIB, INVALID_GRIB)
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

fun getGribDataFromCoordinates(
    lat: Double,
    lon: Double,
    grib: List<GribJson>?
): MutableList<GribPoint> {
    if (grib == null) {
        return mutableListOf()
    }

    val (n, m) = findCoordinateCell(lat, lon, grib.first())

    if (n == INVALID_GRIB && m == INVALID_GRIB) {
        return mutableListOf()
    }


    // Use a map to collect data by height
    val dataByHeight = mutableMapOf<Double, GribPoint>()

    for (g in grib) {
        val height = g.header.surface1Value
        val value = getValueFromGribjson(n, m, g)

        val gribData =
            dataByHeight.getOrPut(height) { GribPoint(height, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0) }

        when (g.header.parameterNumberName) {
            "U-component_of_wind" -> gribData.uComponent = String.format("%.1f", value).toDouble()
            "V-component_of_wind" -> gribData.vComponent = String.format("%.1f", value).toDouble()
            "Temperature" -> gribData.temperature =
                String.format("%.1f", kelvinToCelsius(value)).toDouble()
        }

        gribData.wind =
            String.format("%.1f", windStrength(gribData.uComponent, gribData.vComponent)).toDouble()
        gribData.winddir =
            String.format("%.1f", windDirection(gribData.uComponent, gribData.vComponent))
                .toDouble()

        val result = mutableListOf<GribPoint>()
        var previousGribPoint: GribPoint? = null

        for (gribPoint in dataByHeight.values) {
            if (previousGribPoint != null) {
                // Calculate the wind shear between the current point and the point just below it
                gribPoint.windshear = String.format(
                    "%.1f", windShear(
                        previousGribPoint.uComponent, previousGribPoint.vComponent,
                        gribPoint.uComponent, gribPoint.vComponent
                    )
                ).toDouble()
            } else {
                gribPoint.windshear = 0.0
            }
            result.add(gribPoint)
            previousGribPoint = gribPoint
        }


    }

    // Convert the map values to a list of GribPoint
    return dataByHeight.values.map {
        GribPoint(
            it.height,
            it.vComponent,
            it.uComponent,
            it.temperature,
            it.wind,
            it.winddir,
            it.windshear
        )
    }.toMutableList()
}