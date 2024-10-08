package com.example.bukalaptop.pelanggan.checkout

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
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

    private var penggunaListener: ListenerRegistration? = null
    private var barangListener: ListenerRegistration? = null

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
        penggunaListener = db.collection("pengguna").addSnapshotListener { pengguna, _ ->
            if (pengguna != null) {
                for (peng in pengguna) {
                    if (peng.getString("id") == pelangganId) {
                        peng.reference.collection("keranjang")
                            .addSnapshotListener { keranjang, error ->
                                if (keranjang != null) {
                                    if (keranjang.documents.isEmpty()) {
                                        listKeranjang.clear()
                                        listKeranjangAdapter.setData(listKeranjang)
                                    }
                                }
                                listKeranjang.clear()
                                total = 0
                                val existingItemIds = HashSet<String>()

                                if (keranjang != null) {
                                    val currencyFormat = NumberFormat.getCurrencyInstance()
                                    currencyFormat.maximumFractionDigits = 2
                                    currencyFormat.currency = Currency.getInstance("IDR")

                                    for (krnjng in keranjang) {
                                        barangListener = db.collection("barang")
                                            .addSnapshotListener { barang, error1 ->
                                                if (barang != null) {
                                                    for (brng in barang) {
                                                        if (brng.id == krnjng.id) {
                                                            val mBarang =
                                                                brng.toObject(Barang::class.java)
                                                            val jumlah =
                                                                krnjng.get("jumlah").toString()
                                                                    .toInt()
                                                            if (!existingItemIds.contains(brng.id)) {
                                                                total += (mBarang.biayaSewa * jumlah)

                                                                val mKeranjang =
                                                                    Keranjang(mBarang, jumlah)
                                                                mKeranjang.barang =
                                                                    brng.toObject(Barang::class.java)
                                                                mKeranjang.jumlah = jumlah

                                                                listKeranjang.add(mKeranjang)

                                                                existingItemIds.add(brng.id)
                                                            }
                                                        }
                                                    }
                                                } else if (error1 != null) {
                                                    Toast.makeText(
                                                        requireContext(),
                                                        "$error1",
                                                        Toast.LENGTH_SHORT
                                                    )
                                                        .show()
                                                }
                                                listKeranjangAdapter.setData(listKeranjang)
                                                tvTotal.text =
                                                    currencyFormat.format(total)
                                            }
                                    }

                                    tvTotal.text =
                                        currencyFormat.format(total)
                                } else if (error != null) {
                                    Toast.makeText(requireContext(), "$error", Toast.LENGTH_SHORT)
                                        .show()
                                }
                                progressDialog.dismiss()
                            }
                    }
                }
            }
        }

        btnCheckout.setOnClickListener {
            if (listKeranjangAdapter.itemCount <= 0) {
                Toast.makeText(activity, "Keranjang masih kosong", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
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

    override fun onDestroyView() {
        super.onDestroyView()
        penggunaListener?.remove()
        barangListener?.remove()
        progressDialog.dismiss()
        listKeranjangAdapter.stopListening()
    }
}