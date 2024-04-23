package no.uio.ifi.in2000.prosjekt51.ui.result.scripts

import android.util.Log
import kotlin.math.pow
import kotlin.math.round

fun pressureToHeight(P: Double, t: Double, P_b: Double?, t_b: Double?): Double{
    /*
    Computes the atmospheric pressure as a function of altitude.
     */
    val l = (6.5/1000)
    val M = 0.028964425278793993
    val g0 = 9.80665
    val R = 8.3144598
    val exponent = (l * R)/(g0 * M)
    val middleFactor = t_b?.times((P / P_b!!).pow(exponent))
    if (middleFactor != null) {
        Log.d("GribConversion", "Pressure $P converted to ${(middleFactor + t)/l}")
    }
    return if (middleFactor != null) {
        round((middleFactor + t)/l)
    } else {
        0.0
    }
}


fun kelvinToCelsius(K: Double): Double{
    return K - 273.15
}