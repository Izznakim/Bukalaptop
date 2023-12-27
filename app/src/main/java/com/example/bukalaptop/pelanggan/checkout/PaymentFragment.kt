package com.example.bukalaptop.pelanggan.checkout

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.example.bukalaptop.R
import com.example.bukalaptop.pegawai.pesanan.ProfilPelangganFragment
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Currency
import java.util.Locale
import kotlin.math.abs

class PaymentFragment : Fragment() {

    private lateinit var tvNamaLengkap: TextView
    private lateinit var tvEmail: TextView
    private lateinit var llAlamat: LinearLayout
    private lateinit var tvAtasNama: TextView
    private lateinit var tvNomorPengirim: TextView
    private lateinit var tvAlamatLengkap: TextView
    private lateinit var tvAlamatSingkat: TextView
    private lateinit var btnPengiriman: Button
    private lateinit var tvHari: TextView
    private lateinit var btnPengambilan: Button
    private lateinit var tvTotal: TextView
    private lateinit var ivBuktiPembayaran: ImageView
    private lateinit var btnSewa: Button
    private var imageBitmap: Bitmap? = null

    var selectedPengirimanDate: Calendar = Calendar.getInstance()
    var selectedPengambilanDate: Calendar = Calendar.getInstance()

    companion object {
        var EXTRA_PELANGGANID = "extra_pelangganId"
        var EXTRA_TOTAL = "extra_total"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_payment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tvNamaLengkap = view.findViewById(R.id.tv_nama_lengkap)
        tvEmail = view.findViewById(R.id.tv_email)
        llAlamat = view.findViewById(R.id.ll_alamat)
        tvAtasNama = view.findViewById(R.id.tv_atas_nama)
        tvNomorPengirim = view.findViewById(R.id.tv_nomor_pengiriman)
        tvAlamatLengkap = view.findViewById(R.id.tv_alamat_lengkap_pengiriman)
        tvAlamatSingkat = view.findViewById(R.id.tv_alamat_singkat_pengiriman)
        tvHari = view.findViewById(R.id.tv_hari)
        btnPengiriman = view.findViewById(R.id.btn_pengiriman)
        btnPengambilan = view.findViewById(R.id.btn_pengambilan)
        tvTotal = view.findViewById(R.id.tv_total)
        ivBuktiPembayaran = view.findViewById(R.id.iv_bukti_pembayaran)
        btnSewa = view.findViewById(R.id.btn_sewa)

        var pelangganId = ""
        val db = Firebase.firestore

        updateButtonLabel(0)

        if (arguments != null) {
            pelangganId = arguments?.getString(EXTRA_PELANGGANID).toString()
            val total = arguments?.getInt(EXTRA_TOTAL)

            db.collection("pelanggan").document(pelangganId).addSnapshotListener { value, error ->
                if (error != null) {
                    Log.d("List Pesanan Error", error.toString())
                    return@addSnapshotListener
                }
                if (value != null) {
                    tvNamaLengkap.text = value.getString("namaLengkap")
                    tvEmail.text = value.getString("email")
                }
            }

            llAlamat.setOnClickListener {
                val listAlamatFragment = ListAlamatFragment()
                val mFragmentManager = activity?.supportFragmentManager

                mFragmentManager?.beginTransaction()?.apply {
                    replace(R.id.fragment_pelanggan_container,listAlamatFragment, ListAlamatFragment::class.java.simpleName)
                    addToBackStack(null)
                    commit()
                }
            }

            btnPengiriman.setOnClickListener {
                showDatePickerDialog(selectedPengirimanDate, false) { updatedDate ->
                    selectedPengirimanDate = updatedDate

                    selectedPengambilanDate.timeInMillis = selectedPengirimanDate.timeInMillis
                    updateButtonLabel(total)
                }
            }

            btnPengambilan.setOnClickListener {
                showDatePickerDialog(selectedPengambilanDate, true) { updatedDate ->
                    selectedPengambilanDate = updatedDate

                    updateButtonLabel(total)
                }

            }

            ivBuktiPembayaran.setOnClickListener {
                onPickImageClick()
            }

            btnSewa.setOnClickListener {
                Toast.makeText(requireContext(), "Coming Soon", Toast.LENGTH_SHORT).show()
            }
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

    fun hitungSelisihHari(tanggal1: Calendar, tanggal2: Calendar): Long {
        val waktu1 = tanggal1.timeInMillis
        val waktu2 = tanggal2.timeInMillis

        val selisihHari = abs((waktu2 - waktu1) / (1000 * 60 * 60 * 24))

        return selisihHari
    }

    private fun showDatePickerDialog(
        selectedDate: Calendar, pengambilan: Boolean,
        onDateSetListener: (Calendar) -> Unit
    ) {
        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                selectedDate.set(year, month, dayOfMonth)

                onDateSetListener(selectedDate)
            },
            selectedDate.get(Calendar.YEAR),
            selectedDate.get(Calendar.MONTH),
            selectedDate.get(Calendar.DAY_OF_MONTH)
        )

        if (pengambilan) {
            datePickerDialog.datePicker.minDate = selectedDate.timeInMillis
        } else {
            datePickerDialog.datePicker.minDate = System.currentTimeMillis()
        }

        datePickerDialog.show()
    }

    private fun updateButtonLabel(total: Int? = 0) {
        val currencyFormat = NumberFormat.getCurrencyInstance()
        currencyFormat.maximumFractionDigits = 2
        currencyFormat.currency = Currency.getInstance("IDR")

        val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        val pengirimanDateString = dateFormat.format(selectedPengirimanDate.time)
        val pengambilanDateString = dateFormat.format(selectedPengambilanDate.time)
        btnPengiriman.text = pengirimanDateString
        btnPengambilan.text = pengambilanDateString

        var tanggal1 = Calendar.getInstance().apply {
            time = dateFormat.parse(btnPengiriman.text.toString())
        }

        var tanggal2 = Calendar.getInstance().apply {
            time = dateFormat.parse(btnPengambilan.text.toString())
        }

        val selisihHari = hitungSelisihHari(tanggal1, tanggal2)

        tvHari.text = "$selisihHari Hari"
        tvTotal.text = currencyFormat.format(selisihHari * (total ?: 0))
    }

    private fun onPickImageClick() {
        val options = arrayOf("Kamera", "Galeri")
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Pilih Sumber Gambar")
        builder.setItems(options) { _, which ->
            when (which) {
                0 -> dispatchTakePictureIntent()
                1 -> dispatchPickImageIntent()
            }
        }
        builder.show()
    }

    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(requireContext().packageManager) != null) {
            takePictureLauncher.launch(takePictureIntent)
        }
    }

    private fun dispatchPickImageIntent() {
        val pickPhotoIntent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )
        pickImageLauncher.launch(pickPhotoIntent)
    }

    private val takePictureLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                imageBitmap = data?.extras?.get("data") as Bitmap
                ivBuktiPembayaran.setImageBitmap(imageBitmap)
            }
        }

    private val pickImageLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                val imageUri = data?.data
                imageBitmap =
                    MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, imageUri)
                ivBuktiPembayaran.setImageBitmap(imageBitmap)
            }
        }
}