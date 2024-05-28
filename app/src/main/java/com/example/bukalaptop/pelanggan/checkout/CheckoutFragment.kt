package com.example.bukalaptop.pelanggan.checkout

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bukalaptop.R
import com.example.bukalaptop.model.Keranjang
import com.example.bukalaptop.pegawai.barang.model.Barang
import com.example.bukalaptop.pegawai.pesanan.adapter.ListKeranjangAdapter
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.NumberFormat
import java.util.Currency

class CheckoutFragment : Fragment() {

    private lateinit var rvKeranjang: RecyclerView
    private lateinit var listKeranjangAdapter: ListKeranjangAdapter
    private lateinit var listKeranjang: ArrayList<Keranjang>
    private lateinit var tvTotal: TextView
    private lateinit var btnCheckout: Button
    private lateinit var tvProgress: TextView
    private lateinit var builder: AlertDialog.Builder
    private lateinit var progressDialog: AlertDialog

    private var listenerRegistration: ListenerRegistration? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_checkout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rvKeranjang = view.findViewById(R.id.rv_keranjang)
        tvTotal = view.findViewById(R.id.tv_total)
        btnCheckout = view.findViewById(R.id.btn_checkout)
        rvKeranjang.setHasFixedSize(true)

        builder = AlertDialog.Builder(requireContext())
        val inflater = layoutInflater
        val dialogView = inflater.inflate(R.layout.progress_layout, null)
        builder.setView(dialogView)
        builder.setCancelable(false)
        progressDialog = builder.create()

        tvProgress = dialogView.findViewById(R.id.tv_progress)

        val auth = Firebase.auth
        val db = Firebase.firestore

        val pelangganId = auth.currentUser?.uid ?: ""
        var total = 0
        initAdapter(pelangganId)

        listKeranjang = arrayListOf()
        tvProgress.text = "Memuat keranjang..."
        progressDialog.show()
        db.collection("pengguna").document(pelangganId).collection("keranjang")
            .addSnapshotListener { keranjang, error ->
                listKeranjang.clear()
                total = 0
                val existingItemIds = HashSet<String>()

                if (keranjang != null) {
                    val currencyFormat = NumberFormat.getCurrencyInstance()
                    currencyFormat.maximumFractionDigits = 2
                    currencyFormat.currency = Currency.getInstance("IDR")

                    for (krnjng in keranjang) {
                        listenerRegistration = db.collection("barang")
                            .addSnapshotListener { barang, error1 ->
                                if (barang != null) {
                                    for (brng in barang) {
                                        if (brng.id == krnjng.id) {
                                            val mBarang = brng.toObject(Barang::class.java)
                                            val jumlah = krnjng.get("jumlah").toString().toInt()
                                            if (!existingItemIds.contains(brng.id)) {
                                                total += (mBarang.biayaSewa * jumlah)

                                                val mKeranjang = Keranjang(mBarang, jumlah)
                                                mKeranjang.barang =
                                                    brng.toObject(Barang::class.java)
                                                mKeranjang.jumlah = jumlah

                                                listKeranjang.add(mKeranjang)

                                                existingItemIds.add(brng.id)
                                            }
//                                            total += (mBarang.biayaSewa * jumlah)
//
//                                            val keranjang = Keranjang(mBarang, jumlah)
//                                            keranjang.barang = brng.toObject(Barang::class.java)
//                                            keranjang.jumlah = jumlah
//
//                                            listKeranjang.add(keranjang)
                                        }
                                    }
                                } else if (error1 != null) {
                                    Log.d("List Keranjang", error.toString())
                                }
                                listKeranjangAdapter.setData(listKeranjang)
                                tvTotal.text =
                                    currencyFormat.format(total)
                            }
                    }

                    tvTotal.text =
                        currencyFormat.format(total)
                } else if (error != null) {
                    Log.d("List Keranjang", error.toString())
                }
                progressDialog.dismiss()
            }

        btnCheckout.setOnClickListener {
            val paymentFragment = PaymentFragment()
            val mFragmentManager = activity?.supportFragmentManager
            val bundle = Bundle()

            bundle.putString(PaymentFragment.EXTRA_PELANGGANID, pelangganId)
            bundle.putInt(PaymentFragment.EXTRA_TOTAL, total)
            bundle.putParcelableArrayList(PaymentFragment.EXTRA_KERANJANG, listKeranjang)
            paymentFragment.arguments = bundle
            mFragmentManager?.beginTransaction()?.apply {
                replace(
                    R.id.fragment_pelanggan_container,
                    paymentFragment,
                    PaymentFragment::class.java.simpleName
                )
                addToBackStack(null)
                commit()
            }
        }
    }

    private fun initAdapter(pelangganId: String) {
        rvKeranjang.layoutManager = LinearLayoutManager(activity)
        listKeranjangAdapter = ListKeranjangAdapter(arrayListOf(), false, pelangganId, true)
        rvKeranjang.adapter = listKeranjangAdapter
    }
}