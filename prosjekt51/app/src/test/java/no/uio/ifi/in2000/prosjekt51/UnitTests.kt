package no.uio.ifi.in2000.prosjekt51

import android.annotation.SuppressLint
import android.util.Log
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import io.ktor.client.HttpClient
import org.junit.Test
import org.junit.Assert.*
import no.uio.ifi.in2000.prosjekt51.ui.search.*
import io.ktor.client.engine.mock.*
import io.ktor.http.*
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import no.uio.ifi.in2000.prosjekt51.data.locationForecast.LocationForecastAPI
import no.uio.ifi.in2000.prosjekt51.ui.map.MapViewScreen
import no.uio.ifi.in2000.prosjekt51.ui.result.scripts.celsiusToKelvin
import no.uio.ifi.in2000.prosjekt51.ui.result.scripts.kelvinToCelsius
import no.uio.ifi.in2000.prosjekt51.ui.result.scripts.pressureToHeight
import org.junit.Before
import org.junit.runner.RunWith
import org.mockito.Mockito.mockStatic
import kotlin.math.abs
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config


class CoordinateValidationTests {
    @Test
    fun `latitude is valid within range`() {
        assertTrue(isLatitudeValid("45.0"))
        assertTrue(isLatitudeValid("-89.9999"))
        assertTrue(isLatitudeValid("90"))
    }

    @Test
    fun `latitude is invalid out of range`() {
        assertFalse(isLatitudeValid("90.1"))
        assertFalse(isLatitudeValid("-90.1"))
        assertFalse(isLatitudeValid("100"))
    }

    @Test
    fun `latitude is invalid non-numeric`() {
        assertFalse(isLatitudeValid("abc"))
        assertFalse(isLatitudeValid("45.0N"))
    }

    @Test
    fun `longitude is valid within range`() {
        assertTrue(isLongitudeValid("45.0"))
        assertTrue(isLongitudeValid("-179.9999"))
        assertTrue(isLongitudeValid("180"))
    }

    @Test
    fun `longitude is invalid out of range`() {
        assertFalse(isLongitudeValid("180.1"))
        assertFalse(isLongitudeValid("-180.1"))
        assertFalse(isLongitudeValid("500"))
    }

    @Test
    fun `longitude is invalid non-numeric`() {
        assertFalse(isLongitudeValid("abc"))
        assertFalse(isLongitudeValid("45.0N"))
    }
}


class ConversionTests {

    @Test
    fun `test pressure to height conversion`() {
        assertTrue(abs(pressureToHeight(85000.0, 1024.0, 15.0)  - 1500) < 100)
        assertTrue(abs(pressureToHeight(50000.0, 1024.0, 15.0)  - 5600) < 100)
    }

    @Test
    fun `test temperature conversion`() {
        val tol = 0.0001
        assertTrue(abs(celsiusToKelvin(15.0) - 288.15) < tol)
        assertTrue(abs(kelvinToCelsius(290.0) - 16.85) < tol)
    }

}



class ApiTests {
    @SuppressLint("CheckResult")
    @Before
    fun setup() {
        mockStatic(Log::class.java)
    }
    @Test
    fun `test non-200 response`() = runTest {
        val client = HttpClient(MockEngine) {
            engine {
                addHandler { request ->
                    if (request.url.encodedPath.endsWith("complete")) {
                        respond(
                            content = "Internal server error",
                            status = HttpStatusCode.InternalServerError,
                            headers = headersOf(HttpHeaders.ContentType, ContentType.Text.Plain.toString())
                        )
                    } else {
                        respondBadRequest()
                    }
                }
            }
        }

        val api = LocationForecastAPI(client)

        val result = api.fetchLocationForecast(59.91, 10.75, 0)

        assertFalse("Expected the connection to be unsuccessful due to 500 Internal Server Error", result.successfulConnection)
        assertNotNull("Expected an exception to be present indicating the nature of the error", result.exception)
    }
}
