package com.example.bukalaptop.pelanggan.barang

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.bukalaptop.R
import com.example.bukalaptop.ZoomImageActivity
import com.example.bukalaptop.pegawai.barang.model.Barang
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.NumberFormat
import java.util.Currency

class DetailBarangPelangganFragment : Fragment() {

    private lateinit var ivBarang: ImageView
    private lateinit var tvMerekModel: TextView
    private lateinit var tvBiayaSewa: TextView
    private lateinit var tvProsesor: TextView
    private lateinit var tvRam: TextView
    private lateinit var tvOs: TextView
    private lateinit var tvGrafis: TextView
    private lateinit var tvPenyimpanan: TextView
    private lateinit var tvUkuranLayar: TextView
    private lateinit var tvPerangkatLunak: TextView
    private lateinit var tvAksesoris: TextView
    private lateinit var tvKondisi: TextView
    private lateinit var tvStok: TextView
    private lateinit var btnDecrease: Button
    private lateinit var etJumlah: EditText
    private lateinit var btnIncrease: Button
    private lateinit var btnTambahKeranjang: ImageButton
    private lateinit var barang: Barang
    private lateinit var tvProgress: TextView
    private lateinit var builder: AlertDialog.Builder
    private lateinit var progressDialog: AlertDialog

    private var jumlah: Int = 0

    companion object {
        var EXTRA_IDBARANG = "extra_idbarang"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_detail_barang_pelanggan, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val currencyFormat = NumberFormat.getCurrencyInstance()
        currencyFormat.maximumFractionDigits = 2
        currencyFormat.currency = Currency.getInstance("IDR")

        ivBarang = view.findViewById(R.id.iv_barang)
        tvMerekModel = view.findViewById(R.id.tv_merek)
        tvBiayaSewa = view.findViewById(R.id.tv_biaya_sewa)
        tvProsesor = view.findViewById(R.id.tv_prosesor)
        tvRam = view.findViewById(R.id.tv_ram)
        tvOs = view.findViewById(R.id.tv_os)
        tvGrafis = view.findViewById(R.id.tv_grafis)
        tvUkuranLayar = view.findViewById(R.id.tv_ukuran_layar)
        tvPerangkatLunak = view.findViewById(R.id.tv_perangkat_lunak)
        tvAksesoris = view.findViewById(R.id.tv_aksesoris)
        tvKondisi = view.findViewById(R.id.tv_kondisi)
        tvPenyimpanan = view.findViewById(R.id.tv_penyimpanan)
        tvStok = view.findViewById(R.id.tv_stok)
        btnIncrease = view.findViewById(R.id.btn_increase)
        etJumlah = view.findViewById(R.id.et_jumlah)
        btnDecrease = view.findViewById(R.id.btn_decrease)
        btnTambahKeranjang = view.findViewById(R.id.btn_tambah_keranjang)

        builder = AlertDialog.Builder(requireContext())
        val inflater=layoutInflater
        val dialogView=inflater.inflate(R.layout.progress_layout,null)
        builder.setView(dialogView)
        builder.setCancelable(false)
        progressDialog = builder.create()

        tvProgress=dialogView.findViewById(R.id.tv_progress)

        var barangId = ""

        if (arguments != null) {
            barangId = arguments?.getString(EXTRA_IDBARANG).toString()
        }

        val db = Firebase.firestore
        val auth = Firebase.auth
        tvProgress.text="Memuat informasi barang..."
        progressDialog.show()
        db.collection("barang").addSnapshotListener { value, error ->
            if (value != null) {
                for (document in value) {
                    if (document.id == barangId) {
                        barang = document.toObject(Barang::class.java)
                        if (isAdded) {
                            Glide.with(requireActivity())
                                .load(barang.fotoBarang)
                                .apply(RequestOptions())
                                .into(ivBarang)
                        }
                        tvMerekModel.text = "${barang.merek} ${barang.model}"
                        tvProsesor.text = barang.prosesor
                        tvRam.text = barang.ram
                        tvOs.text = barang.sistemOperasi
                        tvGrafis.text = barang.kartuGrafis
                        tvPenyimpanan.text = barang.penyimpanan
                        tvUkuranLayar.text = barang.ukuranLayar
                        tvPerangkatLunak.text = barang.perangkatLunak
                        tvAksesoris.text = barang.aksesoris
                        tvKondisi.text = barang.kondisi
                        tvBiayaSewa.text = "${currencyFormat.format(barang.biayaSewa)} /Hari"
                        tvStok.text = barang.stok.toString()
                    }
                }
                progressDialog.dismiss()
            } else if (error != null) {
                Log.d("Detail Barang", error.toString())
                progressDialog.dismiss()
            }
        }

        ivBarang.setOnClickListener {
            Intent(activity, ZoomImageActivity::class.java).also {
                it.putExtra(ZoomImageActivity.EXTRA_IMAGE, barang.fotoBarang)
                startActivity(it)
            }
        }

        etJumlah.doOnTextChanged { text, _, _, _ ->
            try {
                when {
                    text.isNullOrEmpty() -> jumlah = 0
                    text.toString().toInt() > barang.stok -> {
                        jumlah = barang.stok
                        etJumlah.setText(jumlah.toString())
                    }

                    else -> jumlah = text.toString().toInt()
                }
                if (jumlah == 0) {
                    btnTambahKeranjang.isEnabled = false
                    btnTambahKeranjang.setBackgroundColor(Color.GRAY)
                } else {
                    btnTambahKeranjang.isEnabled = true
                    btnTambahKeranjang.setBackgroundColor(Color.GREEN)
                }
            } catch (_: NumberFormatException) {
            }
        }

        btnDecrease.setOnClickListener {
            jumlah--
            if (jumlah < 1) {
                jumlah = 0
            }
            etJumlah.setText(jumlah.toString())
        }
        btnIncrease.setOnClickListener {
            jumlah++
            if (jumlah > barang.stok) {
                jumlah = barang.stok
            }
            etJumlah.setText(jumlah.toString())
        }

        if (jumlah == 0) {
            btnTambahKeranjang.isEnabled = false
            btnTambahKeranjang.setBackgroundColor(Color.GRAY)
        } else {
            btnTambahKeranjang.isEnabled = true
            btnTambahKeranjang.setBackgroundColor(Color.GREEN)
        }

        btnTambahKeranjang.setOnClickListener {
            tvProgress.text="Menambahkan ke keranjang..."
            progressDialog.show()
            if (jumlah > 0) {
                val keranjang = hashMapOf(
                    "jumlah" to jumlah
                )

                db.collection("pengguna").document(auth.currentUser?.uid ?: "")
                    .collection("keranjang").document(barangId)
                    .set(keranjang)
                    .addOnSuccessListener { documentReference ->
                        Toast.makeText(
                            activity,
                            "${barang.merek} ${barang.model} telah ditambahkan ke keranjang.",
                            Toast.LENGTH_SHORT
                        ).show()

                        parentFragmentManager.popBackStack()
                        progressDialog.dismiss()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(requireContext(), e.toString(), Toast.LENGTH_SHORT).show()
                        progressDialog.dismiss()
                    }
            }
        }

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                parentFragmentManager.popBackStack()
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
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