package no.uio.ifi.in2000.prosjekt51.ui.information.scripts

import android.util.Log
import kotlin.math.pow

fun pressureToHeight(P: Double, t: Double, P_b: Double?, t_b: Double?): Double{
    val l = (6.5/1000)
    val M = 0.028964425278793993
    val g0 = 9.80665
    val R = 8.3144598
    val exponent = (l * R)/(g0 * M)
    val middleFactor = t_b?.times((P / P_b!!).pow(exponent))
    if (middleFactor != null) {
        Log.d("GribTesting", "Found value ${(middleFactor + t)/l}")
    }
    return if (middleFactor != null) {
        (middleFactor + t)/l
    } else {
        0.0
    }
}