package com.example.bukalaptop.pelanggan.riwayat

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bukalaptop.R
import com.example.bukalaptop.model.Pesanan
import com.example.bukalaptop.pegawai.pesanan.adapter.ListPesananAdapter
import com.example.bukalaptop.pelanggan.riwayat.adapter.ListRiwayatAdapter
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class RiwayatFragment : Fragment() {

    private lateinit var rvRiwayat: RecyclerView
    private lateinit var listRiwayatAdapter: ListRiwayatAdapter
    private lateinit var listRiwayat: ArrayList<Pesanan>
    private lateinit var riwayat: Pesanan

    var riwayatListener: ListenerRegistration? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_riwayat, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rvRiwayat = view.findViewById(R.id.rv_riwayat)
        rvRiwayat.setHasFixedSize(true)

        initAdapter()

        val db = Firebase.firestore
        val auth = Firebase.auth
        listRiwayat = arrayListOf()
        riwayatListener = db.collection("pesanan").addSnapshotListener { value, error ->
            listRiwayat.clear()
            if (error != null) {
                Toast.makeText(requireContext(), error.toString(), Toast.LENGTH_SHORT).show()
                return@addSnapshotListener
            }
            if (value != null) {
                for (document in value) {
                    riwayat = Pesanan()
                    riwayat.id = document.id
                    riwayat.idPelanggan = document.getString("idPelanggan").toString()
                    if (riwayat.idPelanggan == auth.currentUser?.uid) {
                        listRiwayat.add(riwayat)
                    }
                }
            } else {
                Toast.makeText(requireContext(), "Data Kosong", Toast.LENGTH_SHORT).show()
            }
            listRiwayatAdapter.setData(listRiwayat)
        }
    }

    private fun initAdapter() {
        rvRiwayat.layoutManager = LinearLayoutManager(activity)
        listRiwayatAdapter = ListRiwayatAdapter(arrayListOf())
        rvRiwayat.adapter = listRiwayatAdapter
    }
}