package com.example.bukalaptop.pelanggan.checkout

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.bukalaptop.R
import com.example.bukalaptop.databinding.ActivityMapsPelangganBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.util.Locale

class MapsPelangganActivity : AppCompatActivity(), OnMapReadyCallback, LocationListener,
    GoogleMap.OnCameraIdleListener {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsPelangganBinding
    private lateinit var geocoder: Geocoder

    private var lat: Double = 0.0
    private var lng: Double = 0.0

    companion object {
        var EXTRA_LATITUDE = "extra_latitude"
        var EXTRA_LONGITUDE = "extra_longitude"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsPelangganBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val text: Spannable = SpannableString("Peta")
        text.setSpan(
            ForegroundColorSpan(ContextCompat.getColor(this, R.color.yelowrangeLight)),
            0,
            text.length,
            Spannable.SPAN_INCLUSIVE_INCLUSIVE
        )
        supportActionBar?.title = text
        supportActionBar?.elevation = 0f
        supportActionBar?.setBackgroundDrawable(ColorDrawable(Color.BLACK))
        geocoder = Geocoder(this, Locale.getDefault())

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_maps, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.act_submit -> {
                lat = mMap.cameraPosition.target.latitude
                lng = mMap.cameraPosition.target.longitude
                val resultIntent = Intent().apply {
                    putExtra(
                        PaymentFragment.EXTRA_ADDRESS,
                        binding.tvAddress.text.toString()
                    )
                    putExtra(EXTRA_LATITUDE, lat)
                    putExtra(EXTRA_LONGITUDE, lng)
                }
                setResult(Activity.RESULT_OK, resultIntent)
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        val latitude = intent.getDoubleExtra(EXTRA_LATITUDE, 0.0)
        val longitude = intent.getDoubleExtra(EXTRA_LONGITUDE, 0.0)
        val myLoc = LatLng(latitude, longitude)

        CoroutineScope(Dispatchers.Main).launch {
            val addressText = getAddressText(latitude, longitude)
            if (addressText != null) {
                mMap.addMarker(MarkerOptions().position(myLoc).title(addressText))
            } else {
                Toast.makeText(
                    this@MapsPelangganActivity,
                    "Alamat tidak ditemukan",
                    Toast.LENGTH_SHORT
                )
                    .show()
            }
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLoc, 20f))
        }

        mMap.setOnCameraIdleListener(this)
    }

    override fun onLocationChanged(location: Location) {
        CoroutineScope(Dispatchers.Main).launch {
            val addressText = getAddressText(location.latitude, location.longitude)
            if (addressText != null) {
                setAddress(addressText)
            } else {
                Toast.makeText(this@MapsPelangganActivity, "Alamat tidak ditemukan", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private suspend fun getAddressText(latitude: Double, longitude: Double): String? {
        return withContext(Dispatchers.IO) {
            try {
                val addresses = geocoder.getFromLocation(latitude, longitude, 1)
                if (!addresses.isNullOrEmpty()) {
                    addresses[0].getAddressLine(0)
                } else {
                    null
                }
            } catch (e: IOException) {
                null
            }
        }
    }

    private fun setAddress(address: String) {
        binding.tvAddress.text = address
    }

    override fun onCameraIdle() {
        CoroutineScope(Dispatchers.Main).launch {
            val addressText = getAddressText(mMap.cameraPosition.target.latitude, mMap.cameraPosition.target.longitude)
            if (addressText != null) {
                setAddress(addressText)
            } else {
                setAddress("Memuat alamat...")
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mMap.setOnCameraIdleListener(null)
    }
}