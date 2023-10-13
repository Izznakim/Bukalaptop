package com.example.bukalaptop.pegawai.barang

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
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.bukalaptop.R
import com.example.bukalaptop.pegawai.barang.model.Barang
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import java.text.NumberFormat
import java.util.Currency

class DetailBarangFragment : Fragment() {

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
    private lateinit var btnUpdate: Button
    private lateinit var btnHapus: Button

    private var barang: Barang? = null

    companion object {
        var EXTRA_BARANG = "extra_barang"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_detail_barang, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val currencyFormat = NumberFormat.getCurrencyInstance()
        currencyFormat.maximumFractionDigits = 2
        currencyFormat.currency = Currency.getInstance("IDR")

        ivBarang = view.findViewById(R.id.iv_tambah_barang)
        tvMerekModel = view.findViewById(R.id.et_merek)
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
        btnUpdate = view.findViewById(R.id.btn_update)
        btnHapus = view.findViewById(R.id.btn_hapus)

        if (arguments != null) {
            barang = arguments?.getParcelable(EXTRA_BARANG)
            Glide.with(requireContext())
                .load(barang?.fotoBarang)
                .apply(RequestOptions())
                .into(ivBarang)
            tvMerekModel.text = "${barang?.merek} ${barang?.model}"
            tvProsesor.text = barang?.prosesor
            tvRam.text = barang?.ram
            tvOs.text = barang?.sistemOperasi
            tvGrafis.text = barang?.kartuGrafis
            tvPenyimpanan.text = barang?.penyimpanan
            tvUkuranLayar.text = barang?.ukuranLayar
            tvPerangkatLunak.text = barang?.perangkatLunak
            tvAksesoris.text = barang?.aksesoris
            tvKondisi.text = barang?.kondisi
            tvBiayaSewa.text = "${currencyFormat.format(barang?.biayaSewa)} /Hari"
            tvStok.text = barang?.stok.toString()
        }

        btnUpdate.setOnClickListener {
            Toast.makeText(requireContext(), "Coming soon", Toast.LENGTH_SHORT).show()
        }
        btnHapus.setOnClickListener {
            val db = Firebase.firestore
            val storageRef = FirebaseStorage.getInstance().reference
            val barangIdRef = storageRef.child("barang/${barang?.barangId}.jpg")

            barang?.let { mBarang ->
                db.collection("barang").document(mBarang.barangId)
                    .delete()
                    .addOnSuccessListener {
                        Toast.makeText(
                            activity,
                            "${mBarang.merek} ${mBarang.model} berhasil dihapus",
                            Toast.LENGTH_SHORT
                        ).show()
                        parentFragmentManager.popBackStack()
                    }
                    .addOnFailureListener { e -> Log.w("Error", "Error deleting document", e) }

                barangIdRef.delete().addOnSuccessListener {
                }.addOnFailureListener { e -> Log.w("Error", "Error deleting image", e) }
            }
        }

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                parentFragmentManager.popBackStack()
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }
}