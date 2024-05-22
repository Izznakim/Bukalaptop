package com.example.bukalaptop.pelanggan.checkout

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.bukalaptop.MainActivity
import com.example.bukalaptop.R

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.bukalaptop.databinding.ActivityMapsPelangganBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.io.IOException
import java.util.Locale

class MapsPelangganActivity : AppCompatActivity(), OnMapReadyCallback, LocationListener,
    GoogleMap.OnCameraIdleListener {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsPelangganBinding

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

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_maps,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.act_submit -> {
                lat = mMap.cameraPosition.target.latitude
                lng = mMap.cameraPosition.target.longitude
                val resultIntent = Intent()
                resultIntent.putExtra(PaymentFragment.EXTRA_ADDRESS, binding.tvAddress.text.toString())
                resultIntent.putExtra(EXTRA_LATITUDE, lat)
                resultIntent.putExtra(EXTRA_LONGITUDE, lng)
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

        // Add a marker in Sydney and move the camera
        val myLoc = LatLng(latitude, longitude)
        val geocoder = Geocoder(this, Locale.getDefault())
        val addresses: List<Address>? = geocoder.getFromLocation(latitude, longitude, 1)
        if (!addresses.isNullOrEmpty()) {
            val address: Address = addresses[0]
            val addressText = address.getAddressLine(0)
            mMap.addMarker(MarkerOptions().position(myLoc).title(addressText))
        } else {
            Toast.makeText(this, "Alamat tidak ditemukan", Toast.LENGTH_SHORT)
                .show()
        }
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLoc, 20f))

        mMap.setOnCameraIdleListener(this)
    }

    override fun onLocationChanged(location: Location) {
        val geocoder = Geocoder(this, Locale.getDefault())
        var addresses: List<Address>? = null
        try {
            addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        setAddress(addresses!![0])
    }

    private fun setAddress(addresses: Address) {
        if (addresses.getAddressLine(0) != null) {
            binding.tvAddress.text = addresses.getAddressLine(0)
        }
        if (addresses.getAddressLine(1) != null) {
            binding.tvAddress.text = "${binding.tvAddress.text} ${addresses.getAddressLine(0)}"
        }
    }

    override fun onCameraIdle() {
        val addresses: List<Address>?
        val geocoder = Geocoder(this, Locale.getDefault())
        try {
            addresses = geocoder.getFromLocation(
                mMap.cameraPosition.target.latitude,
                mMap.cameraPosition.target.longitude,
                1
            )
            setAddress(addresses!![0])
        } catch (e: IndexOutOfBoundsException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}