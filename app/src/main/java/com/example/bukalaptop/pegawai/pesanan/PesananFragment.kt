package com.example.bukalaptop.pegawai.pesanan

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bukalaptop.R
import com.example.bukalaptop.model.Pesanan
import com.example.bukalaptop.pegawai.pesanan.adapter.ListPesananAdapter
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class PesananFragment : Fragment() {

    private lateinit var rvPesanan: RecyclerView
    private lateinit var listPesananAdapter: ListPesananAdapter
    private lateinit var listPesanan: ArrayList<Pesanan>
    private lateinit var pesanan: Pesanan
    private lateinit var tvProgress: TextView
    private lateinit var builder: AlertDialog.Builder
    private lateinit var progressDialog: AlertDialog

    private var pesananListener: ListenerRegistration? = null

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

        builder = AlertDialog.Builder(requireContext())
        val inflater = layoutInflater
        val dialogView = inflater.inflate(R.layout.progress_layout, null)
        builder.setView(dialogView)
        builder.setCancelable(false)
        progressDialog = builder.create()

        tvProgress = dialogView.findViewById(R.id.tv_progress)

        initAdapter()

        val db = Firebase.firestore
        listPesanan = arrayListOf()
        tvProgress.text = "Memuat barang..."
        progressDialog.show()
        pesananListener = db.collection("pesanan").orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener { value, error ->
                listPesanan.clear()
                if (error != null) {
                    Toast.makeText(requireContext(), "$error", Toast.LENGTH_SHORT).show()
                    progressDialog.dismiss()
                    return@addSnapshotListener
                }
                if (value != null) {
                    for (document in value) {
                        pesanan = Pesanan()
                        pesanan.id = document.id
                        pesanan.idPelanggan = document.getString("idPelanggan").toString()
                        pesanan.status = document.getString("status").toString()
                        if (pesanan.status == "netral" || pesanan.status == "diterima" || pesanan.status == "dikembalikan") {
                            listPesanan.add(pesanan)
                        }
                    }
                } else {
                    Toast.makeText(requireContext(), "Masih belum ada pesanan.", Toast.LENGTH_SHORT)
                        .show()
                }
                listPesananAdapter.setData(listPesanan)
                progressDialog.dismiss()
            }
    }

    private fun initAdapter() {
        rvPesanan.layoutManager = LinearLayoutManager(activity)
        listPesananAdapter = ListPesananAdapter(arrayListOf())
        rvPesanan.adapter = listPesananAdapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        pesananListener?.remove()
        progressDialog.dismiss()
        listPesananAdapter.stopListening()
    }
}