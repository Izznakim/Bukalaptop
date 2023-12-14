package com.example.bukalaptop.pelanggan.checkout

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bukalaptop.R
import com.example.bukalaptop.model.Keranjang
import com.example.bukalaptop.pegawai.barang.model.Barang
import com.example.bukalaptop.pegawai.pesanan.adapter.ListKeranjangAdapter
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

        initAdapter()

        val pelangganId = "ug58i2Mfv60PPjuzhjKr"
        var total = 0

        val db = Firebase.firestore
        listKeranjang = arrayListOf()
        db.collection("pelanggan").document(pelangganId).collection("keranjang")
            .addSnapshotListener { keranjang, error ->
                listKeranjang.clear()
                if (keranjang != null) {
                    for (krnjng in keranjang) {
                        db.collection("barang")
                            .addSnapshotListener { barang, error1 ->
                                val currencyFormat = NumberFormat.getCurrencyInstance()
                                currencyFormat.maximumFractionDigits = 2
                                currencyFormat.currency = Currency.getInstance("IDR")

                                if (barang != null) {
                                    for (brng in barang) {
                                        if (brng.id == krnjng.id) {
                                            val mBarang = brng.toObject(Barang::class.java)
                                            val jumlah = krnjng.get("jumlah").toString().toInt()
                                            total += (mBarang.biayaSewa * jumlah)

                                            val keranjang = Keranjang(mBarang, jumlah)
                                            keranjang.barang = brng.toObject(Barang::class.java)
                                            keranjang.jumlah = jumlah

                                            listKeranjang.add(keranjang)
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
                } else if (error != null) {
                    Log.d("List Keranjang", error.toString())
                }
            }

        btnCheckout.setOnClickListener {
            val paymentFragment = PaymentFragment()
            val mFragmentManager = activity?.supportFragmentManager
            val bundle = Bundle()

            bundle.putString(PaymentFragment.EXTRA_PELANGGANID, pelangganId)
            bundle.putInt(PaymentFragment.EXTRA_TOTAL, total)
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

    private fun initAdapter() {
        rvKeranjang.layoutManager = LinearLayoutManager(activity)
        listKeranjangAdapter = ListKeranjangAdapter(arrayListOf(), false)
        rvKeranjang.adapter = listKeranjangAdapter
    }
}