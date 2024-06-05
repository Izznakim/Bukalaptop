package com.example.bukalaptop.pegawai.pesanan

import android.content.Intent
import android.graphics.Paint
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.bukalaptop.R
import com.example.bukalaptop.ZoomImageActivity
import com.example.bukalaptop.pegawai.barang.model.Barang
import com.example.bukalaptop.pegawai.pesanan.ProfilPelangganFragment.Companion.EXTRA_IDPELANGGAN
import com.example.bukalaptop.pegawai.pesanan.adapter.ListKeranjangAdapter
import com.example.bukalaptop.model.Keranjang
import com.example.bukalaptop.model.Pelanggan
import com.example.bukalaptop.model.Pesanan
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Currency
import java.util.Date
import java.util.Locale

class DetailPesananFragment : Fragment() {

    private lateinit var cvPelangganProfil: CardView
    private lateinit var tvNama: TextView
    private lateinit var tvEmail: TextView
    private lateinit var tvTglPengiriman: TextView
    private lateinit var tvTglPengambilan: TextView
    private lateinit var tvHari: TextView
    private lateinit var tvTotal: TextView
    private lateinit var tvAlamat: TextView
    private lateinit var ivBukti: ImageView
    private lateinit var btnTerima: Button
    private lateinit var btnTolak: Button
    private lateinit var rvKeranjang: RecyclerView
    private lateinit var listKeranjangAdapter: ListKeranjangAdapter
    private lateinit var listKeranjang: ArrayList<Keranjang>
    private lateinit var pesanan: Pesanan
    private lateinit var pelanggan: Pelanggan
    private lateinit var tvProgress: TextView
    private lateinit var builder: AlertDialog.Builder
    private lateinit var progressDialog: AlertDialog

    companion object {
        var EXTRA_IDPESANAN = "extra_idpesanan"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_detail_pesanan, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        cvPelangganProfil = view.findViewById(R.id.cv_pelanggan_profil)
        tvNama = view.findViewById(R.id.tv_namaLengkap)
        tvEmail = view.findViewById(R.id.tv_email)
        tvTglPengiriman = view.findViewById(R.id.tv_tglpengiriman)
        tvTglPengambilan = view.findViewById(R.id.tv_tglpengambilan)
        tvHari = view.findViewById(R.id.tv_hari)
        tvTotal = view.findViewById(R.id.tv_total)
        btnTolak = view.findViewById(R.id.btn_tolak)
        btnTerima = view.findViewById(R.id.btn_terima)
        ivBukti = view.findViewById(R.id.iv_bukti)
        tvAlamat = view.findViewById(R.id.tv_alamat)

        cvPelangganProfil.setOnClickListener {
            val profilPelangganFragment = ProfilPelangganFragment()
            val mFragmentManager = activity?.supportFragmentManager
            val bundle = Bundle()

            bundle.putString(EXTRA_IDPELANGGAN, pesanan.idPelanggan)
            profilPelangganFragment.arguments = bundle
            mFragmentManager?.beginTransaction()?.apply {
                replace(
                    R.id.fragment_pegawai_container,
                    profilPelangganFragment,
                    ProfilPelangganFragment::class.java.simpleName
                )
                addToBackStack(null)
                commit()
            }
        }

        rvKeranjang = view.findViewById(R.id.rv_keranjang)
        rvKeranjang.setHasFixedSize(true)

        builder = AlertDialog.Builder(requireContext())
        val inflater = layoutInflater
        val dialogView = inflater.inflate(R.layout.progress_layout, null)
        builder.setView(dialogView)
        builder.setCancelable(false)
        progressDialog = builder.create()

        tvProgress = dialogView.findViewById(R.id.tv_progress)

        tvAlamat.paintFlags = tvAlamat.paintFlags or Paint.UNDERLINE_TEXT_FLAG

        initAdapter()

        val pesananId: String

        val db = Firebase.firestore
        listKeranjang = arrayListOf()

        if (arguments != null) {
            pesananId = arguments?.getString(EXTRA_IDPESANAN).toString()

            tvProgress.text = "Memuat informasi pesanan..."
            progressDialog.show()
            db.collection("pesanan").addSnapshotListener { valuePesanan, errorPesanan ->
                if (errorPesanan != null) {
                    Log.d("List Pesanan Error", errorPesanan.toString())
                    return@addSnapshotListener
                }
                if (valuePesanan != null) {
                    for (document in valuePesanan) {
                        if (document.id == pesananId) {
                            pesanan = Pesanan()
                            pesanan.id = document.id
                            pesanan.idPelanggan = document.getString("idPelanggan").toString()
                            pesanan.buktiBayar = document.getString("buktiBayar").toString()
                            pesanan.tglPengiriman =
                                document.getTimestamp("tglPengiriman")?.toDate()
                            pesanan.tglPengambilan =
                                document.getTimestamp("tglPengambilan")?.toDate()
                            pesanan.alamat = document.getString("alamat")
                            pesanan.latitute = document.getDouble("latitude")
                            pesanan.longitude = document.getDouble("longitude")
                        }
                    }

                    val currencyFormat = NumberFormat.getCurrencyInstance()
                    currencyFormat.maximumFractionDigits = 2
                    currencyFormat.currency = Currency.getInstance("IDR")

                    val sdf = SimpleDateFormat("dd/MM/yyy", Locale.getDefault())
                    val diff =
                        (pesanan.tglPengambilan?.time ?: 0) - (pesanan.tglPengiriman?.time ?: 0)
                    val masaSewa = (diff / 1000 / 60 / 60 / 24).toInt()

                    db.collection("pengguna")
                        .addSnapshotListener { valuePelanggan, errorPelanggan ->
                            if (errorPelanggan != null) {
                                Log.d("List Pesanan Error", errorPelanggan.toString())
                                return@addSnapshotListener
                            }
                            if (valuePelanggan != null) {
                                for (document in valuePelanggan) {
                                    if (document.getString("id") == pesanan.idPelanggan) {
                                        pelanggan = document.toObject(Pelanggan::class.java)
                                        tvNama.text = pelanggan.namaLengkap
                                        tvEmail.text = pelanggan.email
                                    }
                                }
                            }
                        }

                    tvTglPengiriman.text = sdf.format(pesanan.tglPengiriman ?: Date())
                    tvTglPengambilan.text = sdf.format(pesanan.tglPengambilan ?: Date())
                    tvHari.text = masaSewa.toString()
                    tvAlamat.text = pesanan.alamat

                    if (isAdded) {
                        Glide.with(requireContext())
                            .load(pesanan.buktiBayar)
                            .apply(RequestOptions())
                            .into(ivBukti)
                    }

                    db.collection("pesanan").document(pesananId).collection("keranjang")
                        .addSnapshotListener { valueKeranjang, errorKeranjang ->
                            var total = 0
                            listKeranjang.clear()
                            if (valueKeranjang != null) {
                                for (document in valueKeranjang) {
                                    val barang = document.toObject(Barang::class.java)
                                    val jumlah = document.get("jumlah").toString().toInt()
                                    total += (barang.biayaSewa * jumlah)

                                    val keranjang = Keranjang(barang, jumlah)
                                    keranjang.barang = document.toObject(Barang::class.java)
                                    keranjang.jumlah = jumlah

                                    listKeranjang.add(keranjang)
                                }
                            } else if (errorKeranjang != null) {
                                Log.d("List Keranjang", errorKeranjang.toString())
                            }
                            listKeranjangAdapter.setData(listKeranjang)
                            tvTotal.text =
                                currencyFormat.format(total * masaSewa)
                        }
                } else {
                    Log.d("List Pesanan", "Data Kosong")
                }
                progressDialog.dismiss()
            }

            tvAlamat.setOnClickListener {
                val uri =
                    Uri.parse("geo:${pesanan.latitute},${pesanan.longitude}?q=${pesanan.latitute},${pesanan.longitude}")
                val intent = Intent(Intent.ACTION_VIEW, uri)
                intent.setPackage("com.google.android.apps.maps")
                if (intent.resolveActivity(requireActivity().packageManager) != null) {
                    startActivity(intent)
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Google Maps belum diinstall",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

            ivBukti.setOnClickListener {
                Intent(activity, ZoomImageActivity::class.java).also {
                    it.putExtra(ZoomImageActivity.EXTRA_IMAGE, pesanan.buktiBayar)
                    startActivity(it)
                }
            }
            btnTerima.setOnClickListener {
                tvProgress.text = "Memuat pesanan..."
                progressDialog.show()
                db.collection("pesanan").document(pesananId).update("status", "diterima")
                    .addOnSuccessListener {
                        Toast.makeText(
                            requireContext(),
                            "Pesanan telah Anda setujui",
                            Toast.LENGTH_SHORT
                        ).show()
                        parentFragmentManager.popBackStack()
                        progressDialog.dismiss()
                    }.addOnFailureListener {
                    Toast.makeText(requireContext(), "$it", Toast.LENGTH_SHORT).show()
                    progressDialog.dismiss()
                }
            }
            btnTolak.setOnClickListener {
                tvProgress.text = "Memuat pesanan..."
                progressDialog.show()
                db.collection("pesanan").document(pesananId).update("status", "ditolak")
                    .addOnSuccessListener {
                        Toast.makeText(
                            requireContext(),
                            "Pesanan telah Anda tolak",
                            Toast.LENGTH_SHORT
                        ).show()
                        parentFragmentManager.popBackStack()
                        progressDialog.dismiss()
                    }.addOnFailureListener {
                    Toast.makeText(requireContext(), "$it", Toast.LENGTH_SHORT).show()
                    progressDialog.dismiss()
                }
            }
        }
    }

    private fun initAdapter() {
        rvKeranjang.layoutManager = LinearLayoutManager(activity)
        listKeranjangAdapter = ListKeranjangAdapter(arrayListOf(), true, "", false)
        rvKeranjang.adapter = listKeranjangAdapter
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