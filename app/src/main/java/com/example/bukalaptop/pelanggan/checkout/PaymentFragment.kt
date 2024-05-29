package com.example.bukalaptop.pelanggan.checkout

import android.Manifest
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Paint
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.bukalaptop.R
import com.example.bukalaptop.model.Keranjang
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Currency
import java.util.Locale
import kotlin.math.abs

class PaymentFragment : Fragment() {

    private lateinit var tvNamaLengkap: TextView
    private lateinit var tvEmail: TextView
    private lateinit var btnPengiriman: Button
    private lateinit var tvHari: TextView
    private lateinit var btnPengambilan: Button
    private lateinit var tvTotal: TextView
    private lateinit var tvAlamat: TextView
    private lateinit var ivMyLoc: ImageView
    private lateinit var ivBuktiPembayaran: ImageView
    private lateinit var btnSewa: Button
    private lateinit var storageRef: StorageReference
    private lateinit var tvProgress: TextView
    private lateinit var builder: AlertDialog.Builder
    private lateinit var progressDialog: AlertDialog
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private var imageBitmap: Bitmap? = null
    private var mSelisihHari: Long? = 0
    private var pengirimanDateString: String = ""
    private var pengambilanDateString: String = ""
    private var lat: Double = 0.0
    private var lng: Double = 0.0

    var selectedPengirimanDate: Calendar = Calendar.getInstance()
    var selectedPengambilanDate: Calendar = Calendar.getInstance()

    companion object {
        var EXTRA_PELANGGANID = "extra_pelangganId"
        var EXTRA_TOTAL = "extra_total"
        var EXTRA_KERANJANG = "extra_keranjang"
        var EXTRA_ADDRESS = "extra_address"
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
        tvHari = view.findViewById(R.id.tv_hari)
        btnPengiriman = view.findViewById(R.id.btn_pengiriman)
        btnPengambilan = view.findViewById(R.id.btn_pengambilan)
        tvTotal = view.findViewById(R.id.tv_total)
        ivMyLoc = view.findViewById(R.id.iv_my_loc)
        tvAlamat = view.findViewById(R.id.tv_alamat)
        ivBuktiPembayaran = view.findViewById(R.id.iv_bukti_pembayaran)
        btnSewa = view.findViewById(R.id.btn_sewa)

        builder = AlertDialog.Builder(requireContext())
        val inflater = layoutInflater
        val dialogView = inflater.inflate(R.layout.progress_layout, null)
        builder.setView(dialogView)
        builder.setCancelable(false)
        progressDialog = builder.create()

        tvProgress = dialogView.findViewById(R.id.tv_progress)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())

        var pelangganId = ""
        val db = Firebase.firestore
        storageRef = FirebaseStorage.getInstance().reference

        selectedPengirimanDate.add(Calendar.DAY_OF_MONTH, 1)
        selectedPengambilanDate.add(Calendar.DAY_OF_MONTH, 1)

        tvAlamat.paintFlags = tvAlamat.paintFlags or Paint.UNDERLINE_TEXT_FLAG

        updateButtonLabel(0)

        if (arguments != null) {
            pelangganId = arguments?.getString(EXTRA_PELANGGANID).toString()
            val total = arguments?.getInt(EXTRA_TOTAL)
            val listKeranjang = arguments?.getParcelableArrayList<Keranjang>(EXTRA_KERANJANG)
            val address = arguments?.getString(EXTRA_ADDRESS).toString()

            db.collection("pengguna").document(pelangganId).addSnapshotListener { value, error ->
                tvProgress.text = "Memuat data..."
                progressDialog.show()
                if (error != null) {
                    Log.d("List Pesanan Error", error.toString())
                    return@addSnapshotListener
                }
                if (value != null) {
                    tvNamaLengkap.text = value.getString("namaLengkap")
                    tvEmail.text = value.getString("email")
                }
                progressDialog.dismiss()
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

            if (address != "null") {
                tvAlamat.text = address
            }

            tvAlamat.setOnClickListener {
                val addressText = tvAlamat.text.toString()
                if (addressText.isNotEmpty() && addressText != "Alamat akan ditampilkan disini") {
                    openMapsWithAddress(lat, lng)
                } else {
                    locationPermissionRequest.launch(
                        arrayOf(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        )
                    )
                }
            }

            ivMyLoc.setOnClickListener {
                locationPermissionRequest.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            }

            ivBuktiPembayaran.setOnClickListener {
                onPickImageClick()
            }

            btnSewa.setOnClickListener {
                tvProgress.text = "Sedang menyewa..."
                progressDialog.show()
                if (mSelisihHari?.toInt() == 0) {
                    Toast.makeText(requireContext(), "Tanggal belum dipilih", Toast.LENGTH_SHORT)
                        .show()
                    progressDialog.dismiss()
                } else if (tvAlamat.text.toString() == "Alamat akan ditampilkan disini") {
                    Toast.makeText(
                    requireContext(),
                        "Alamat belum dicantumkan",
                        Toast.LENGTH_SHORT
                    ).show()
                    progressDialog.dismiss()
                } else if (imageBitmap == null) {
                    Toast.makeText(
                        requireContext(),
                        "Bukti pembayaran belum dicantumkan",
                        Toast.LENGTH_SHORT
                    ).show()
                    progressDialog.dismiss()
                } else {
                    val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
                    val formattedTglPengiriman = dateFormat.parse(pengirimanDateString)
                    val formattedTglPengambilan = dateFormat.parse(pengambilanDateString)
                    val currentTimeMillis=System.currentTimeMillis()

                    val pesanan = hashMapOf(
                        "idPelanggan" to pelangganId,
                        "tglPengiriman" to formattedTglPengiriman?.let { tgl -> Timestamp(tgl) },
                        "tglPengambilan" to formattedTglPengambilan?.let { tgl -> Timestamp(tgl) },
                        "alamat" to tvAlamat.text.toString(),
                        "latitude" to lat,
                        "longitude" to lng,
                        "status" to "netral",
                        "timestamp" to currentTimeMillis
                    )

                    db.collection("pesanan").add(pesanan).addOnSuccessListener { doc ->
                        val baos = ByteArrayOutputStream()
                        imageBitmap?.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                        val imageData = baos.toByteArray()

                        val imageRef = storageRef.child("bukti/${doc.id}.jpg")

                        val uploadTask = imageRef.putBytes(imageData)
                        uploadTask.addOnSuccessListener {
                            imageRef.downloadUrl.addOnSuccessListener { uri ->
                                val imageUrl = uri.toString()
                                db.collection("pesanan").document(doc.id)
                                    .update(
                                        "buktiBayar", imageUrl
                                    )
                            }
                        }

                        listKeranjang?.forEach {
                            val keranjang = hashMapOf(
                                "aksesoris" to it.barang.aksesoris,
                                "biayaSewa" to it.barang.biayaSewa,
                                "fotoBarang" to it.barang.fotoBarang,
                                "jumlah" to it.jumlah,
                                "kartuGrafis" to it.barang.kartuGrafis,
                                "kondisi" to it.barang.kondisi,
                                "merek" to it.barang.merek,
                                "model" to it.barang.model,
                                "penyimpanan" to it.barang.penyimpanan,
                                "perangkatLunak" to it.barang.perangkatLunak,
                                "prosesor" to it.barang.prosesor,
                                "ram" to it.barang.ram,
                                "sistemOperasi" to it.barang.sistemOperasi,
                                "stok" to (it.barang.stok - it.jumlah),
                                "ukuranLayar" to it.barang.ukuranLayar
                            )

                            db.collection("pesanan").document(doc.id).collection("keranjang")
                                .document(it.barang.barangId).set(keranjang)
                            db.collection("barang").document(it.barang.barangId)
                                .update("stok", it.barang.stok - it.jumlah)
                        }

                        Toast.makeText(
                            requireContext(),
                            "Pesanan sudah dikirim",
                            Toast.LENGTH_SHORT
                        ).show()

                        progressDialog.dismiss()
                        parentFragmentManager.popBackStack()
                    }.addOnFailureListener { e ->
                        Toast.makeText(context, "$e", Toast.LENGTH_SHORT).show()
                        progressDialog.dismiss()
                    }
                }
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
        pengirimanDateString = dateFormat.format(selectedPengirimanDate.time)
        pengambilanDateString = dateFormat.format(selectedPengambilanDate.time)
        btnPengiriman.text = pengirimanDateString
        btnPengambilan.text = pengambilanDateString

        val tanggal1 = Calendar.getInstance().apply {
            time = dateFormat.parse(btnPengiriman.text.toString())
        }

        val tanggal2 = Calendar.getInstance().apply {
            time = dateFormat.parse(btnPengambilan.text.toString())
        }

        mSelisihHari = hitungSelisihHari(tanggal1, tanggal2)

        tvHari.text = "$mSelisihHari Hari"
        tvTotal.text = currencyFormat.format((mSelisihHari ?: 0) * (total ?: 0))
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

    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false -> {
                getCurrentLocation()
            }

            permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false -> {
                getCurrentLocation()
            }

            else -> {
                tvAlamat.text = "Izin lokasi ditolak"
            }
        }
    }

    private val mapsActivityResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                val selectedAddress = data?.getStringExtra(EXTRA_ADDRESS)
                lat = data?.getDoubleExtra(MapsPelangganActivity.EXTRA_LATITUDE, 0.0) ?: 0.0
                lng = data?.getDoubleExtra(MapsPelangganActivity.EXTRA_LONGITUDE, 0.0) ?: 0.0
                tvAlamat.text = selectedAddress
            }
        }

    private fun checkPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun getCurrentLocation() {
        tvProgress.text = "Sedang memuat..."
        progressDialog.show()
        if (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION) &&
            checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
        ) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    getAddressFromLocation(location.latitude, location.longitude)
                    lat = location.latitude
                    lng = location.longitude
                }
            }
        } else {
            locationPermissionRequest.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
        progressDialog.dismiss()
    }

    private fun getAddressFromLocation(latitude: Double, longitude: Double) {
        val geocoder = Geocoder(requireContext(), Locale.getDefault())
        val addresses: List<Address>? = geocoder.getFromLocation(latitude, longitude, 1)
        if (!addresses.isNullOrEmpty()) {
            val address: Address = addresses[0]
            val addressText = address.getAddressLine(0)
            tvAlamat.text = addressText
        } else {
            tvAlamat.text = "Alamat tidak ditemukan"
        }
    }

    private fun openMapsWithAddress(latitude: Double, longitude: Double) {
        val intent = Intent(activity, MapsPelangganActivity::class.java)
        intent.putExtra(MapsPelangganActivity.EXTRA_LATITUDE, latitude)
        intent.putExtra(MapsPelangganActivity.EXTRA_LONGITUDE, longitude)

        mapsActivityResultLauncher.launch(intent)
    }
}