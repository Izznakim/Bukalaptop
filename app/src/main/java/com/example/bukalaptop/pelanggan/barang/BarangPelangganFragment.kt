package com.example.bukalaptop.pelanggan.barang

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bukalaptop.R
import com.example.bukalaptop.pegawai.barang.adapter.ListBarangAdapter
import com.example.bukalaptop.pegawai.barang.model.Barang
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class BarangPelangganFragment : Fragment() {

    private lateinit var rvBarang: RecyclerView
    private lateinit var listBarangAdapter: ListBarangAdapter
    private lateinit var listBarang: ArrayList<Barang>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_barang_pelanggan, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rvBarang = view.findViewById(R.id.rv_barang)
        rvBarang.setHasFixedSize(true)

        initAdapter()

        val db = Firebase.firestore
        listBarang = arrayListOf()
        db.collection("barang").addSnapshotListener { value, error ->
            listBarang.clear()
            if (value != null) {
                for (document in value) {
                    val barang = document.toObject(Barang::class.java)
                    listBarang.add(barang)
                }
            } else if (error != null) {
                Log.d("List Barang", error.toString())
            }
            listBarangAdapter.setData(listBarang)
        }
    }

    private fun initAdapter() {
        rvBarang.layoutManager = LinearLayoutManager(activity)
        listBarangAdapter = ListBarangAdapter(arrayListOf())
        rvBarang.adapter = listBarangAdapter
    }
}