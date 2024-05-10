package no.uio.ifi.in2000.prosjekt51.ui.result.scripts

import kotlin.math.atan2
import kotlin.math.pow
import kotlin.math.round
import kotlin.math.sqrt

fun pressureToHeight(P: Double, P_b: Double?, t_b: Double?): Double{
    /*
    Computes the altitude as a function of atmospheric pressure.

    arguments:
        P: Pressure at the height which is to be calculated
        P_b: Pressure at ground level
        t_b: Temperature at ground level

    returns: The calculated height as a Double
     */
    val P_b_pa = P_b?.times(100) ?: 0.0
    val t_b_kel = celsiusToKelvin(t_b)
    val l = -(6.5/1000)
    val M = 0.028964425278793993
    val g0 = 9.80665
    val R = 8.3144598
    val exponent = (l * -R)/(g0 * M)
    val middleFactor = (t_b_kel/l).times((P / P_b_pa).pow(exponent) - 1)
    return round(middleFactor)
}


fun kelvinToCelsius(K: Double): Double{
    /*
    Converts kelvin to celsius

    arguments:
        K: Temperature in kelvin

    returns: The temperature in celsius as a Double
     */
    return K - 273.15
}

fun celsiusToKelvin(C: Double?): Double{
    /*
    Converts celsius to kelvin

    arguments:
        C: Temperature in celsius

    returns: The temperature in kelvin as a Double
     */
    return if (C != null) {
        C + 273.15
    } else {
        0.0
    }
}



fun windStrength(ucomp: Double, vcomp: Double): Double{
    /*
    Finds wind strength from component vectors

    arguments:
        ucomp: Component of wind in u-direction
        vcomp: Component of wind in v-direction

    returns: The wind speed in m/s as a Double
     */
    return sqrt(ucomp.pow(2) + vcomp.pow(2))
}

fun windDirection(ucomp: Double, vcomp: Double): Double{
    /*
    Finds wind direction from component vectors

    arguments:
        ucomp: Component of wind in u-direction
        vcomp: Component of wind in v-direction

    returns: The wind direction as a Double
     */
    return atan2(ucomp, vcomp)
}

fun windShear(u1: Double, v1: Double, u2: Double, v2: Double): Double{
    /*
    Finds wind shear from component vectors of two different atmospheric layers

    arguments:
        u1: Component of wind in u-direction for layer 1
        v1: Component of wind in v-direction for layer 1
        u2: Component of wind in u-direction for layer 2
        v2: Component of wind in v-direction for layer 2

    returns: The wind shear in m/s as a Double
     */
    val diff1 = u2 - u1
    val diff2 = v2 - v1
    return sqrt(diff1.pow(2) + diff2.pow(2))
}