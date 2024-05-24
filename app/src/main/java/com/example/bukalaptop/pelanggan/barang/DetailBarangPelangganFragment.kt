package com.example.bukalaptop.pelanggan.barang

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.bukalaptop.R
import com.example.bukalaptop.databinding.FragmentDetailBarangPelangganBinding
import com.example.bukalaptop.utils.ZoomImageActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.NumberFormat
import java.util.Currency

class DetailBarangPelangganFragment : Fragment() {

    private var _binding: FragmentDetailBarangPelangganBinding? = null

    private lateinit var detailBarangPelangganViewModel: DetailBarangPelangganViewModel
    private lateinit var tvProgress: TextView
    private lateinit var builder: AlertDialog.Builder
    private lateinit var progressDialog: AlertDialog
    private lateinit var currencyFormat: NumberFormat
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    private var barangId = ""

    private val binding get() = _binding!!

    companion object {
        var EXTRA_IDBARANG = "extra_idbarang"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        detailBarangPelangganViewModel =
            ViewModelProvider(this)[DetailBarangPelangganViewModel::class.java]

        currencyFormat = NumberFormat.getCurrencyInstance()
        currencyFormat.maximumFractionDigits = 2
        currencyFormat.currency = Currency.getInstance("IDR")

        builder = AlertDialog.Builder(requireContext())
        val dialogView = inflater.inflate(R.layout.progress_layout, null)
        builder.setView(dialogView)
        builder.setCancelable(false)
        progressDialog = builder.create()

        tvProgress = dialogView.findViewById(R.id.tv_progress)

        db = Firebase.firestore
        auth = Firebase.auth

        _binding = FragmentDetailBarangPelangganBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (arguments != null) {
            barangId = arguments?.getString(EXTRA_IDBARANG).toString()
        }

        binding.apply {
            include2.apply {
                viewModel(barangId)
            }
        }
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                parentFragmentManager.popBackStack()
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }

    private fun viewModel(barangId: String) {
        with(detailBarangPelangganViewModel) {
            binding.apply {
                include2.apply {
                    getDetailBarang(db, barangId)
                    detailBarang.observe(viewLifecycleOwner) { barang ->
                        if (isAdded) {
                            Glide.with(requireActivity())
                                .load(barang.fotoBarang)
                                .apply(RequestOptions())
                                .into(ivBarang)
                        }
                        tvMerek.text = "${barang.merek} ${barang.model}"
                        tvProsesor.text = barang.prosesor
                        tvRam.text = barang.ram
                        tvOs.text = barang.sistemOperasi
                        tvGrafis.text = barang.kartuGrafis
                        tvPenyimpanan.text = barang.penyimpanan
                        tvUkuranLayar.text = barang.ukuranLayar
                        tvPerangkatLunak.text = barang.perangkatLunak
                        tvAksesoris.text = barang.aksesoris
                        tvKondisi.text = barang.kondisi
                        tvBiayaSewa.text =
                            "${currencyFormat.format(barang.biayaSewa)} /Hari"
                        tvStok.text = barang.stok.toString()


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
                            if (jumlah > 0) {
                                tambahKeranjang(db, auth, jumlah, barangId, barang)
                            }
                        }
                    }
                    toast.observe(viewLifecycleOwner) {
                        val toast = it.format(this)
                        Toast.makeText(activity, toast, Toast.LENGTH_SHORT).show()
                    }
                    isLoading.observe(viewLifecycleOwner) {
                        showLoading(it)
                    }
                    isSuccess.observe(viewLifecycleOwner) {
                        if (it){
                            parentFragmentManager.popBackStack()
                        }
                    }
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            tvProgress.text="Memuat barang..."
            progressDialog.show()
        } else {
            progressDialog.dismiss()
        }
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}