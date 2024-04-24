package no.uio.ifi.in2000.prosjekt51.ui.map

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import no.uio.ifi.in2000.prosjekt51.R

class MapViewScreen : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var mMap: GoogleMap
    private lateinit var confirmButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        confirmButton = findViewById(R.id.confirmButton)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.setOnMapClickListener { latLng ->
            mMap.clear()
            mMap.addMarker(MarkerOptions().position(latLng).title("Selected Location"))
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10f))

            confirmButton.visibility = View.VISIBLE
            confirmButton.setOnClickListener {
                saveToFavorites(latLng)
                confirmButton.visibility = View.GONE  // Optionally hide the button after saving
            }
        }
    }
}

private fun saveToFavorites(latLng: LatLng) {
    // Implement saving to favorites
    // For example, save to a SharedPreferences or a database
    Log.d("MapTesting", latLng.toString())
}