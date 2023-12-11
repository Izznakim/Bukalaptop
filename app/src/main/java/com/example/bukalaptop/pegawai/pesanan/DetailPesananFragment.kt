package com.example.bukalaptop.pegawai.pesanan

import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
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

    companion object {
        var EXTRA_IDPESANAN = "extra_idpesanan"
        var EXTRA_NAMALENGKAP = "extra_namalengkap"
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
        tvAlamat = view.findViewById(R.id.tv_alamat)
        btnTolak = view.findViewById(R.id.btn_tolak)
        btnTerima = view.findViewById(R.id.btn_terima)
        ivBukti = view.findViewById(R.id.iv_bukti)

        cvPelangganProfil.setOnClickListener {
            val profilPelangganFragment = ProfilPelangganFragment()
            val mFragmentManager = activity?.supportFragmentManager
            val bundle = Bundle()

            bundle.putString(EXTRA_IDPELANGGAN, pesanan.idPelanggan)
            profilPelangganFragment.arguments = bundle
            mFragmentManager?.beginTransaction()?.apply {
                replace(R.id.fragment_pegawai_container,profilPelangganFragment, ProfilPelangganFragment::class.java.simpleName)
                addToBackStack(null)
                commit()
            }
        }

        rvKeranjang = view.findViewById(R.id.rv_keranjang)
        rvKeranjang.setHasFixedSize(true)

        initAdapter()

        var pesananId = ""

        val db = Firebase.firestore
        listKeranjang = arrayListOf()

        if (arguments != null) {
            pesananId = arguments?.getString(EXTRA_IDPESANAN).toString()

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
                            pesanan.idPelanggan=document.getString("idPelanggan").toString()
                            pesanan.alamatLengkap = document.getString("alamatLengkap").toString()
                            pesanan.buktiBayar = document.getString("buktiBayar").toString()
                            pesanan.latitude = document.getGeoPoint("alamat")?.latitude ?: 0.0
                            pesanan.longitude = document.getGeoPoint("alamat")?.longitude ?: 0.0
                            pesanan.tglPengiriman =
                                document.getTimestamp("tglPengiriman")?.toDate()
                            pesanan.tglPengambilan =
                                document.getTimestamp("tglPengambilan")?.toDate()
                        }
                    }

                    val alamat = SpannableString(pesanan.alamatLengkap)
                    alamat.setSpan(UnderlineSpan(), 0, pesanan.alamatLengkap.length, 0)

                    val currencyFormat = NumberFormat.getCurrencyInstance()
                    currencyFormat.maximumFractionDigits = 2
                    currencyFormat.currency = Currency.getInstance("IDR")

                    val sdf = SimpleDateFormat("dd/MM/yyy", Locale.getDefault())
                    val diff =
                        (pesanan.tglPengambilan?.time ?: 0) - (pesanan.tglPengiriman?.time ?: 0)
                    val masaSewa = (diff / 1000 / 60 / 60 / 24).toInt()

                    db.collection("pelanggan").addSnapshotListener{valuePelanggan,errorPelanggan->
                        if (errorPelanggan != null) {
                            Log.d("List Pesanan Error", errorPelanggan.toString())
                            return@addSnapshotListener
                        }
                        if (valuePelanggan != null) {
                            for (document in valuePelanggan) {
                                if (document.id == pesanan.idPelanggan) {
                                    pelanggan=document.toObject(Pelanggan::class.java)
                                    tvNama.text = pelanggan.namaLengkap
                                    tvEmail.text = pelanggan.email
                                }
                            }
                        }
                    }

                    tvTglPengiriman.text = sdf.format(pesanan.tglPengiriman ?: Date())
                    tvTglPengambilan.text = sdf.format(pesanan.tglPengambilan ?: Date())
                    tvHari.text = masaSewa.toString()
                    tvAlamat.text = alamat

                    Glide.with(requireContext())
                        .load(pesanan.buktiBayar)
                        .apply(RequestOptions())
                        .into(ivBukti)

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
            }

            tvAlamat.setOnClickListener {
                val mapsFragment = MapsFragment()
                val mFragmentManager = activity?.supportFragmentManager
                val bundle = Bundle()

                bundle.putString(EXTRA_IDPESANAN, pesananId)
                bundle.putString(EXTRA_NAMALENGKAP, pelanggan.namaLengkap)
                mapsFragment.arguments = bundle
                mFragmentManager?.beginTransaction()?.apply {
                    replace(R.id.fragment_pegawai_container,mapsFragment, MapsFragment::class.java.simpleName)
                    addToBackStack(null)
                    commit()
                }
            }
            ivBukti.setOnClickListener {
                Intent(activity, ZoomImageActivity::class.java).also {
                    it.putExtra(ZoomImageActivity.EXTRA_IMAGE, pesanan.buktiBayar)
                    startActivity(it)
                }
            }
            btnTerima.setOnClickListener {
                Toast.makeText(
                    requireContext(),
                    "Coming Soon di Terima tapi masih ada di daftar pesanan tetapi cardView item berwarna hijau",
                    Toast.LENGTH_SHORT
                ).show()
            }
            btnTolak.setOnClickListener {
                Toast.makeText(
                    requireContext(),
                    "Coming Soon di Tolak dan item langsung hilang di daftar pesanan",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                parentFragmentManager.popBackStack()
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }

    private fun initAdapter() {
        rvKeranjang.layoutManager = LinearLayoutManager(activity)
        listKeranjangAdapter = ListKeranjangAdapter(arrayListOf(),true)
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