package com.example.bukalaptop.pegawai.pesanan

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
import com.example.bukalaptop.pegawai.pesanan.adapter.ListKeranjangAdapter
import com.example.bukalaptop.pegawai.pesanan.model.Keranjang
import com.example.bukalaptop.pegawai.pesanan.model.Pesanan
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

    companion object {
        var EXTRA_PESANAN = "extra_pesanan"
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
            Toast.makeText(activity, "Coming Soon!", Toast.LENGTH_SHORT).show()
        }

        rvKeranjang = view.findViewById(R.id.rv_keranjang)
        rvKeranjang.setHasFixedSize(true)

        initAdapter()

        val db = Firebase.firestore
        listKeranjang = arrayListOf()

        if (arguments != null) {
            val pesanan = arguments?.getParcelable<Pesanan>(EXTRA_PESANAN)
            var total = 0

            val alamat = SpannableString(pesanan?.alamatLengkap)
            alamat.setSpan(UnderlineSpan(), 0, pesanan?.alamatLengkap?.length ?: 0, 0)

            val currencyFormat = NumberFormat.getCurrencyInstance()
            currencyFormat.maximumFractionDigits = 2
            currencyFormat.currency = Currency.getInstance("IDR")

            val sdf = SimpleDateFormat("dd/MM/yyy", Locale.getDefault())
            val diff =
                (pesanan?.tglPengambilan?.time ?: 0) - (pesanan?.tglPengiriman?.time ?: 0)
            val masaSewa = (diff / 1000 / 60 / 60 / 24).toInt()

            tvNama.text = pesanan?.namaLengkap
            tvEmail.text = pesanan?.email
            tvTglPengiriman.text = sdf.format(pesanan?.tglPengiriman ?: Date())
            tvTglPengambilan.text = sdf.format(pesanan?.tglPengambilan ?: Date())
            tvHari.text = masaSewa.toString()
            tvAlamat.text = alamat

            Glide.with(requireContext())
                .load(pesanan?.buktiBayar)
                .apply(RequestOptions())
                .into(ivBukti)

            if (pesanan != null) {
                db.collection("pesanan").document(pesanan.id).collection("keranjang")
                    .addSnapshotListener { value, error ->
                        listKeranjang.clear()
                        if (value != null) {
                            for (document in value) {
                                val biayaSewa = document.get("biayaSewa").toString().toInt()
                                val jumlah = document.get("jumlah").toString().toInt()
                                total += (biayaSewa * jumlah)
                                val keranjang = document.toObject(Keranjang::class.java)
                                listKeranjang.add(keranjang)
                            }
                        } else if (error != null) {
                            Log.d("List Keranjang", error.toString())
                        }
                        listKeranjangAdapter.setData(listKeranjang)
                        tvTotal.text =
                            currencyFormat.format(total * masaSewa)
                    }
            }

            tvAlamat.setOnClickListener {
                Toast.makeText(requireContext(), "Coming Soon ke Maps Fragment", Toast.LENGTH_SHORT)
                    .show()
            }
            ivBukti.setOnClickListener {
                Toast.makeText(requireContext(), "Coming Soon ke Zoom Image", Toast.LENGTH_SHORT)
                    .show()
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
        listKeranjangAdapter = ListKeranjangAdapter(arrayListOf())
        rvKeranjang.adapter = listKeranjangAdapter
    }
}