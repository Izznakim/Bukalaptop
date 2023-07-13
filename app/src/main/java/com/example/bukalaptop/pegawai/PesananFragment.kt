package com.example.bukalaptop.pegawai

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bukalaptop.R
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class PesananFragment : Fragment() {

    private lateinit var rvPesanan: RecyclerView
    private lateinit var listPesananAdapter: ListPesananAdapter
    private lateinit var listPesanan:ArrayList<Pesanan>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_pesanan, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rvPesanan = view.findViewById(R.id.rv_barang)
        rvPesanan.setHasFixedSize(true)

        initAdapter()

        val db = Firebase.firestore
        listPesanan = arrayListOf()
        db.collection("pesanan").addSnapshotListener { value, error ->
            listPesanan.clear()
            if (value != null) {
                for (document in value){
                    val pesanan=document.toObject(Pesanan::class.java)
                    listPesanan.add(pesanan)
                }
            }else if (error!=null){
                Log.d("List Pesanan", error.toString())
            }
            listPesananAdapter.setData(listPesanan)
        }
    }

    private fun initAdapter() {
        rvPesanan.layoutManager = LinearLayoutManager(activity)
        listPesananAdapter = ListPesananAdapter(arrayListOf())
        rvPesanan.adapter = listPesananAdapter
    }
}