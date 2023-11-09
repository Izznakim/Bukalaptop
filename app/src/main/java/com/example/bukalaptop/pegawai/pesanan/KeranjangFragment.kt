package com.example.bukalaptop.pegawai.pesanan

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bukalaptop.R
import com.example.bukalaptop.pegawai.pesanan.DetailPesananFragment.Companion.EXTRA_IDPESANAN
import com.example.bukalaptop.pegawai.pesanan.adapter.ListKeranjangAdapter
import com.example.bukalaptop.pegawai.pesanan.model.Keranjang
import com.example.bukalaptop.pegawai.pesanan.model.Pesanan
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class KeranjangFragment : Fragment() {

    private lateinit var rvKeranjang: RecyclerView
    private lateinit var listKeranjangAdapter: ListKeranjangAdapter
    private lateinit var listKeranjang:ArrayList<Keranjang>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_keranjang, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rvKeranjang = view.findViewById(R.id.rv_keranjang)
        rvKeranjang.setHasFixedSize(true)

        initAdapter()

        val db = Firebase.firestore
        listKeranjang = arrayListOf()
        if (arguments != null) {
            val pesananId = arguments?.getString(EXTRA_IDPESANAN)
            if (pesananId!=null){
                db.collection("pesanan").document(pesananId).collection("keranjang").addSnapshotListener { value, error ->
                    listKeranjang.clear()
                    if (value != null) {
                        for (document in value){
                            Log.d("List Keranjang", document.data.toString())
                            val keranjang=document.toObject(Keranjang::class.java)
                            listKeranjang.add(keranjang)
                        }
                    }else if (error!=null){
                        Log.d("List Keranjang", error.toString())
                    }
                    listKeranjangAdapter.setData(listKeranjang)
                }
            }
        }
    }

    private fun initAdapter() {
        rvKeranjang.layoutManager = LinearLayoutManager(activity)
        listKeranjangAdapter = ListKeranjangAdapter(arrayListOf())
        rvKeranjang.adapter = listKeranjangAdapter
    }
}