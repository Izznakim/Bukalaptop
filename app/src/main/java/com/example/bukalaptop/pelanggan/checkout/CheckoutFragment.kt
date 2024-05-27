package com.example.bukalaptop.pelanggan.checkout

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bukalaptop.R
import com.example.bukalaptop.databinding.FragmentCheckoutBinding
import com.example.bukalaptop.model.Keranjang
import com.example.bukalaptop.pegawai.barang.DetailBarangFragment
import com.example.bukalaptop.pegawai.barang.model.Barang
import com.example.bukalaptop.pelanggan.barang.DetailBarangPelangganFragment
import com.example.bukalaptop.pelanggan.checkout.adapter.ListBarangCheckoutAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.NumberFormat
import java.util.Currency

class CheckoutFragment : Fragment(), ListBarangCheckoutAdapter.OnItemClickListener {

    private var _binding: FragmentCheckoutBinding? = null

    private lateinit var adapter: ListBarangCheckoutAdapter
    private lateinit var listKeranjang: ArrayList<Keranjang>
    private lateinit var tvProgress: TextView
    private lateinit var builder: AlertDialog.Builder
    private lateinit var progressDialog: AlertDialog
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    private val binding get() = _binding!!
    private var pelangganId: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        builder = AlertDialog.Builder(requireContext())
        val inflater = layoutInflater
        val dialogView = inflater.inflate(R.layout.progress_layout, null)
        builder.setView(dialogView)
        builder.setCancelable(false)
        progressDialog = builder.create()

        tvProgress = dialogView.findViewById(R.id.tv_progress)

        auth = Firebase.auth
        db = Firebase.firestore

        listKeranjang = arrayListOf()

        _binding = FragmentCheckoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvKeranjang.setHasFixedSize(true)

        pelangganId = auth.currentUser?.uid ?: ""
        var total = 0
        initAdapter()

        tvProgress.text = "Memuat keranjang..."
        progressDialog.show()
        db.collection("pengguna").document(pelangganId).collection("keranjang")
            .addSnapshotListener { keranjang, error ->
                listKeranjang.clear()
                total = 0
                if (keranjang != null) {
                    val currencyFormat = NumberFormat.getCurrencyInstance()
                    currencyFormat.maximumFractionDigits = 2
                    currencyFormat.currency = Currency.getInstance("IDR")

                    for (krnjng in keranjang) {
                        db.collection("barang")
                            .addSnapshotListener { barang, error1 ->
                                if (barang != null) {
                                    for (brng in barang) {
                                        if (brng.id == krnjng.id) {
                                            val mBarang = brng.toObject(Barang::class.java)
                                            val jumlah = krnjng.get("jumlah").toString().toInt()
                                            total += (mBarang.biayaSewa * jumlah)

                                            val mKeranjang = Keranjang(mBarang, jumlah)
                                            mKeranjang.barang = brng.toObject(Barang::class.java)
                                            mKeranjang.jumlah = jumlah

                                            listKeranjang.add(mKeranjang)
                                        }
                                    }
                                } else if (error1 != null) {
                                    Log.d("List Keranjang", error.toString())
                                }
                                adapter.setData(listKeranjang)
                                binding.tvTotal.text =
                                    currencyFormat.format(total)
                            }
                    }

                    binding.tvTotal.text =
                        currencyFormat.format(total)
                } else if (error != null) {
                    Log.d("List Keranjang", error.toString())
                }
                progressDialog.dismiss()
            }

        binding.btnCheckout.setOnClickListener {
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

    private fun initAdapter() {
        binding.rvKeranjang.layoutManager = LinearLayoutManager(activity)
        adapter = ListBarangCheckoutAdapter(arrayListOf(), this)
        binding.rvKeranjang.adapter = adapter
    }

    override fun onItemClick(barang: Barang) {
        val detailBarangPelangganFragment = DetailBarangPelangganFragment()
        val mFragmentManager = requireActivity().supportFragmentManager
        val bundle = Bundle()
        bundle.putString(DetailBarangFragment.EXTRA_IDBARANG, barang.barangId)
        detailBarangPelangganFragment.arguments = bundle

        mFragmentManager.beginTransaction().apply {
            replace(
                R.id.fragment_pelanggan_container,
                detailBarangPelangganFragment,
                DetailBarangPelangganFragment::class.java.simpleName
            )
            addToBackStack(null)
            commit()
        }
    }

    override fun onDeleteClick(position: Int, barang: Barang) {
        val builder = AlertDialog.Builder(requireContext())

        builder.setMessage(
            HtmlCompat.fromHtml(
                "Anda yakin ingin menghapus <b>${barang.merek}</b> <b>${barang.model}</b> dari Keranjang Anda?",
                HtmlCompat.FROM_HTML_MODE_LEGACY
            )
        )
            .setTitle("Konfirmasi")

        builder.setPositiveButton("Ya") { dialog, which ->
            if (adapter.itemCount <= 1) {
                adapter.setData(emptyList())
            } else {
                adapter.listBarangKeranjang.removeAt(position)
                adapter.notifyItemRemoved(position)
            }
            barang.let { mBarang ->
                db.collection("pengguna").document(pelangganId).collection("keranjang")
                    .document(mBarang.barangId)
                    .delete()
                    .addOnSuccessListener {
                        Toast.makeText(
                            requireContext(),
                            "${mBarang.merek} ${mBarang.model} berhasil dihapus",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    .addOnFailureListener { e ->
                        Log.w(
                            "Error",
                            "Error deleting document",
                            e
                        )
                    }
            }
        }

        builder.setNegativeButton("Tidak") { dialog, which ->
            dialog.cancel()
        }

        val dialog = builder.create()
        dialog.show()
    }
}