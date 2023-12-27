package com.example.bukalaptop.pelanggan.checkout

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.bukalaptop.R
import com.example.bukalaptop.model.Alamat
import com.example.bukalaptop.model.Pelanggan
import com.example.bukalaptop.pelanggan.barang.adapter.ListAlamatAdapter
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class ListAlamatFragment : Fragment() {

    private lateinit var rvAlamat: RecyclerView
    private lateinit var listAlamatAdapter: ListAlamatAdapter
    private lateinit var listAlamat: ArrayList<Alamat>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_list_alamat, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rvAlamat = view.findViewById(R.id.rv_alamat)
        rvAlamat.setHasFixedSize(true)

        initAdapter()

        val db = Firebase.firestore

        listAlamat = arrayListOf()
        db.collection("pelanggan").document("ug58i2Mfv60PPjuzhjKr").collection("alamat")
            .addSnapshotListener { value, error ->
                listAlamat.clear()
                if (error != null) {
                    Log.d("List Alamat Error", error.toString())
                    return@addSnapshotListener
                }
                if (value != null) {
                    for (document in value) {
                        val alamat=document.toObject(Alamat::class.java)
                        listAlamat.add(alamat)
                    }
                }
                listAlamatAdapter.setData(listAlamat)
            }
    }

    private fun initAdapter() {
        rvAlamat.layoutManager = LinearLayoutManager(activity)
        listAlamatAdapter = ListAlamatAdapter(arrayListOf())
        rvAlamat.adapter = listAlamatAdapter
    }

    override fun onResume() {
        super.onResume()

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                parentFragmentManager.popBackStack()
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }
}