package com.example.bukalaptop.pegawai.barang

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bukalaptop.R
import com.example.bukalaptop.pegawai.barang.adapter.ListBarangAdapter
import com.example.bukalaptop.pegawai.barang.model.Barang
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class BarangFragment : Fragment() {

    private lateinit var rvBarang: RecyclerView
    private lateinit var listBarangAdapter: ListBarangAdapter
    private lateinit var listBarang: ArrayList<Barang>
    private lateinit var fabDetail: FloatingActionButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_barang, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rvBarang = view.findViewById(R.id.rv_barang)
        rvBarang.setHasFixedSize(true)

        fabDetail = view.findViewById(R.id.fab_detail)

        initAdapter()

        val db = Firebase.firestore
        listBarang = arrayListOf()
        db.collection("barang").addSnapshotListener { value, error ->
            listBarang.clear()
            if (value != null) {
                for (document in value) {
                    val barang = Barang()
                    barang.id = document.id
                    barang.fotoBarang = document.getString("fotoBarang").toString()
                    barang.merek = document.getString("merek").toString()
                    barang.model = document.getString("model").toString()
                    barang.prosesor = document.getString("prosesor").toString()
                    barang.kartuGrafis = document.getString("kartuGrafis").toString()
                    barang.ram = document.getString("ram").toString()
                    barang.penyimpanan = document.getString("penyimpanan").toString()
                    barang.sistemOperasi = document.getString("sistemOperasi").toString()
                    barang.perangkatLunak = document.get("perangkatLunak") as ArrayList<String>
                    barang.ukuranLayar = document.getString("ukuranLayar").toString()
                    barang.aksesoris = document.get("aksesoris") as ArrayList<String>
                    barang.kondisi = document.getString("kondisi").toString()
                    barang.biayaSewa = document.getLong("biayaSewa")?.toInt() ?: 0
                    barang.stok = document.getLong("stok")?.toInt() ?: 0
                    listBarang.add(barang)
                }
            } else if (error != null) {
                Log.d("List Barang", error.toString())
            }
            listBarangAdapter.setData(listBarang)
        }

        fabDetail.setOnClickListener {
            Toast.makeText(requireContext(), "Coming soon", Toast.LENGTH_SHORT).show()
        }
    }

    private fun initAdapter() {
        rvBarang.layoutManager = LinearLayoutManager(activity)
        listBarangAdapter = ListBarangAdapter(arrayListOf())
        rvBarang.adapter = listBarangAdapter
    }
}