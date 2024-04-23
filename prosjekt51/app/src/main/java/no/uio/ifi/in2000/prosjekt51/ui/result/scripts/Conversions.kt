package no.uio.ifi.in2000.prosjekt51.ui.result.scripts

import android.util.Log
import kotlin.math.atan2
import kotlin.math.pow
import kotlin.math.round
import kotlin.math.sqrt

fun pressureToHeight(P: Double, t: Double, P_b: Double?, t_b: Double?): Double{
    /*
    Computes the altitude as a function of atmospheric pressure.
     */
    val P_b_pa = P_b?.times(100) ?: 0.0
    val t_kel = celsiusToKelvin(t)
    val t_b_kel = celsiusToKelvin(t_b)
    val l = -(6.5/1000)
    val M = 0.028964425278793993
    val g0 = 9.80665
    val R = 8.3144598
    val exponent = (l * -R)/(g0 * M)
    val middleFactor = (t_b_kel/l).times((P / P_b_pa).pow(exponent) - 1)
    Log.d("GribConversion", "Pressure $P converted to ${middleFactor}. Values: P: $P, t: $t, P_b: ${P_b_pa}, t_b: ${t_b}. Lapse rate is $exponent")
    return round(middleFactor)
}


fun kelvinToCelsius(K: Double): Double{
    return K - 273.15
}

fun celsiusToKelvin(C: Double?): Double{
    return if (C != null) {
        C + 273.15
    } else {
        0.0
    }
}



fun windStrength(ucomp: Double, vcomp: Double): Double{
    return sqrt(ucomp.pow(2) + vcomp.pow(2))
}

fun windDirection(ucomp: Double, vcomp: Double): Double{
    return atan2(ucomp, vcomp)
}

fun windShear(u1: Double, v1: Double, u2: Double, v2: Double): Double{
    val diff1 = u2 - u1
    val diff2 = v2 - v1
    return sqrt(diff1.pow(2) + diff2.pow(2))
}