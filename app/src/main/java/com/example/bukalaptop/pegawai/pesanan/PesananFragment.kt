package com.example.bukalaptop.pegawai.pesanan

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bukalaptop.R
import com.example.bukalaptop.pegawai.pesanan.adapter.ListPesananAdapter
import com.example.bukalaptop.model.Pelanggan
import com.example.bukalaptop.model.Pesanan
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class PesananFragment : Fragment() {

    private lateinit var rvPesanan: RecyclerView
    private lateinit var listPesananAdapter: ListPesananAdapter
    private lateinit var listPesanan: ArrayList<Pesanan>
    private lateinit var pesanan: Pesanan

    private var pesananListenerReg: ListenerRegistration? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_pesanan, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rvPesanan = view.findViewById(R.id.rv_pesanan)
        rvPesanan.setHasFixedSize(true)

        initAdapter()

        val db = Firebase.firestore
        listPesanan = arrayListOf()
        pesananListenerReg = db.collection("pesanan").addSnapshotListener { value, error ->
            listPesanan.clear()
            if (error != null) {
                Log.d("List Pesanan Error", error.toString())
                return@addSnapshotListener
            }
            if (value != null) {
                for (document in value) {
                    pesanan = Pesanan()
                    pesanan.id = document.id
                    pesanan.idPelanggan = document.getString("idPelanggan").toString()
                    pesanan.status = document.getString("status").toString()
                    if (pesanan.status != "ditolak") {
                        listPesanan.add(pesanan)
                    }
                }
            } else {
                Log.d("List Pesanan", "Data Kosong")
            }
            listPesananAdapter.setData(listPesanan)
        }
    }

    private fun initAdapter() {
        rvPesanan.layoutManager = LinearLayoutManager(activity)
        listPesananAdapter = ListPesananAdapter(arrayListOf())
        rvPesanan.adapter = listPesananAdapter
    }

    override fun onStop() {
        super.onStop()

        pesananListenerReg?.remove()
    }
}