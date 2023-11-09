package com.example.bukalaptop.pegawai.pesanan

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import com.example.bukalaptop.R
import com.example.bukalaptop.pegawai.pesanan.DetailPesananFragment.Companion.EXTRA_IDPESANAN
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MapsFragment : Fragment() {

    private val callback = OnMapReadyCallback { googleMap ->
        if (arguments != null) {
            val pesananId = arguments?.getString(EXTRA_IDPESANAN).toString()

            val db = Firebase.firestore
            db.collection("pesanan").addSnapshotListener { valuePesanan, errorPesanan ->
                if (errorPesanan != null) {
                    Log.d("List Pesanan Error", errorPesanan.toString())
                    return@addSnapshotListener
                }
                if (valuePesanan != null) {
                    for (document in valuePesanan) {
                        if (document.id == pesananId) {
                            val namaLengkap = document.getString("namaLengkap").toString()
                            val alamatLengkap = document.getString("alamatLengkap").toString()
                            val latitude = document.getGeoPoint("alamat")?.latitude ?: 0.0
                            val longitude = document.getGeoPoint("alamat")?.longitude ?: 0.0

                            val alamat = LatLng(latitude, longitude)
                            googleMap.addMarker(
                                MarkerOptions().position(alamat).title(namaLengkap)
                                    .snippet(alamatLengkap).infoWindowAnchor(0.5f, 0.0f)
                            )
                            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(alamat, 15f))
                        }
                    }
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_maps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                parentFragmentManager.popBackStack()
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }
}