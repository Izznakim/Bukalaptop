package com.example.bukalaptop.pegawai.barang

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.bukalaptop.R
import com.example.bukalaptop.pegawai.barang.model.Barang
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class UpdateBarangFragment : Fragment() {

    private lateinit var ivEditBarang: ImageView
    private lateinit var etMerek: EditText
    private lateinit var etModel: EditText
    private lateinit var etProsesor: EditText
    private lateinit var etRam: EditText
    private lateinit var etOs: EditText
    private lateinit var etGrafis: EditText
    private lateinit var etPenyimpanan: EditText
    private lateinit var etUkuranLayar: EditText
    private lateinit var etPerangkatLunak: EditText
    private lateinit var etAksesoris: EditText
    private lateinit var etKondisi: EditText
    private lateinit var etBiayaSewa: EditText
    private lateinit var etStok: EditText
    private lateinit var btnBatal: Button
    private lateinit var btnTerapkan: Button
    private lateinit var storageRef: StorageReference
    private lateinit var databaseRef: FirebaseFirestore
    private lateinit var tvProgress: TextView
    private lateinit var builder: AlertDialog.Builder
    private lateinit var progressDialog: AlertDialog

    private var barang: Barang? = null
    private var imageUri: Uri? = null
    private var merek: String = ""
    private var model: String = ""
    private var prosesor: String = ""
    private var ram: String = ""
    private var os: String = ""
    private var grafis: String = ""
    private var penyimpanan: String = ""
    private var ukuranLayar: String = ""
    private var perangkatLunak: String = ""
    private var aksesoris: String = ""
    private var kondisi: String = ""
    private var biayaSewa: Int = 0
    private var stok: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_update_barang, container, false)
    }

    @OptIn(DelicateCoroutinesApi::class)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        storageRef = FirebaseStorage.getInstance().reference

        databaseRef = FirebaseFirestore.getInstance()

        ivEditBarang = view.findViewById(R.id.iv_barang)
        etMerek = view.findViewById(R.id.et_merek)
        etModel = view.findViewById(R.id.et_model)
        etProsesor = view.findViewById(R.id.et_prosesor)
        etRam = view.findViewById(R.id.et_ram)
        etOs = view.findViewById(R.id.et_os)
        etGrafis = view.findViewById(R.id.et_grafis)
        etPenyimpanan = view.findViewById(R.id.et_penyimpanan)
        etUkuranLayar = view.findViewById(R.id.et_ukuran_layar)
        etPerangkatLunak = view.findViewById(R.id.et_perangkat_lunak)
        etAksesoris = view.findViewById(R.id.et_aksesoris)
        etKondisi = view.findViewById(R.id.et_kondisi)
        etBiayaSewa = view.findViewById(R.id.et_biaya_sewa)
        etStok = view.findViewById(R.id.et_stok)
        btnTerapkan = view.findViewById(R.id.btn_terapkan)
        btnBatal = view.findViewById(R.id.btn_batal)

        builder = AlertDialog.Builder(requireContext())
        val inflater = layoutInflater
        val dialogView = inflater.inflate(R.layout.progress_layout, null)
        builder.setView(dialogView)
        builder.setCancelable(false)
        progressDialog = builder.create()

        tvProgress = dialogView.findViewById(R.id.tv_progress)

        var barangId = ""
        var barangImageUrl = ""

        if (arguments != null) {
            barang = arguments?.getParcelable(DetailBarangFragment.EXTRA_BARANG)
            barangImageUrl = barang?.fotoBarang.toString()
            Glide.with(requireContext())
                .load(barang?.fotoBarang)
                .apply(RequestOptions())
                .into(ivEditBarang)
            etMerek.setText(barang?.merek ?: "")
            etModel.setText(barang?.model ?: "")
            etProsesor.setText(barang?.prosesor ?: "")
            etRam.setText(barang?.ram?.split(" ")?.take(1)?.joinToString(" ") ?: "")
            etOs.setText(barang?.sistemOperasi ?: "")
            etGrafis.setText(barang?.kartuGrafis ?: "")
            etPenyimpanan.setText(barang?.penyimpanan ?: "")
            etUkuranLayar.setText(barang?.ukuranLayar ?: "")
            etPerangkatLunak.setText(barang?.perangkatLunak ?: "")
            etAksesoris.setText(barang?.aksesoris ?: "")
            etKondisi.setText(barang?.kondisi ?: "")
            etBiayaSewa.setText(barang?.biayaSewa.toString())
            etStok.setText(barang?.stok.toString())
            barangId = barang?.barangId.toString()
        }

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                parentFragmentManager.popBackStack()
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

        ivEditBarang.setOnClickListener {
            onPickImageClick()
        }

        btnTerapkan.setOnClickListener {
            merek = etMerek.text.toString()
            model = etModel.text.toString()
            prosesor = etProsesor.text.toString()
            ram = etRam.text.toString()+" GB"
            os = etOs.text.toString()
            grafis = etGrafis.text.toString()
            penyimpanan = etPenyimpanan.text.toString()
            ukuranLayar = etUkuranLayar.text.toString()
            perangkatLunak = etPerangkatLunak.text.toString()
            aksesoris = etAksesoris.text.toString()
            kondisi = etKondisi.text.toString()
            biayaSewa = etBiayaSewa.text.toString().toInt()
            stok = etStok.text.toString().toInt()

            if (merek.isEmpty()) {
                etMerek.requestFocus()
                etMerek.error = "Merek harus diisi"
                return@setOnClickListener
            }

            if (model.isEmpty()) {
                etModel.requestFocus()
                etModel.error = "Model harus diisi"
                return@setOnClickListener
            }

            if (prosesor.isEmpty()) {
                etProsesor.requestFocus()
                etProsesor.error = "Prosesor harus diisi"
                return@setOnClickListener
            }

            if (ram.isEmpty()) {
                etRam.requestFocus()
                etRam.error = "RAM harus diisi"
                return@setOnClickListener
            }

            if (os.isEmpty()) {
                etOs.requestFocus()
                etOs.error = "Sistem Operasi harus diisi"
                return@setOnClickListener
            }

            if (grafis.isEmpty()) {
                etGrafis.requestFocus()
                etGrafis.error = "Kartu Grafis harus diisi"
                return@setOnClickListener
            }

            if (penyimpanan.isEmpty()) {
                etPenyimpanan.requestFocus()
                etPenyimpanan.error = "Penyimpanan harus diisi"
                return@setOnClickListener
            }

            if (ukuranLayar.isEmpty()) {
                etUkuranLayar.requestFocus()
                etUkuranLayar.error = "Ukuran Layar harus diisi"
                return@setOnClickListener
            }

            if (perangkatLunak.isEmpty()) {
                etPerangkatLunak.requestFocus()
                etPerangkatLunak.error = "Perangkat Lunak harus diisi"
                return@setOnClickListener
            }

            if (aksesoris.isEmpty()) {
                etAksesoris.requestFocus()
                etAksesoris.error = "Aksesoris harus diisi"
                return@setOnClickListener
            }

            if (kondisi.isEmpty()) {
                etKondisi.requestFocus()
                etKondisi.error = "Kondisi harus diisi"
                return@setOnClickListener
            }

            if (biayaSewa == 0) {
                etBiayaSewa.requestFocus()
                etBiayaSewa.error = "Biaya Sewa harus diberi biaya"
                return@setOnClickListener
            }

            if (stok == 0) {
                etStok.requestFocus()
                etStok.error = "Stok harus diberi biaya"
                return@setOnClickListener
            }

            tvProgress.text = "Memperbarui barang..."
            progressDialog.show()
            GlobalScope.launch(Dispatchers.IO) {
                try {
                    getImageUriFromUrl(barangImageUrl) {
                        if (it != null) {
                            ivEditBarang.setImageURI(it)
                            imageUri = it
                        } else {
                            Toast.makeText(
                                requireContext(),
                                "Gagal mengunduh gambar",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    val imageRef = storageRef.child("barang/${barangId}.jpg")

                    if (imageUri != null) {
                        val uploadTask = imageRef.putFile(imageUri!!)
                        uploadTask.addOnSuccessListener {
                            imageRef.downloadUrl.addOnSuccessListener { uri ->
                                uploadDatabase(barangId, uri.toString())
                            }
                            progressDialog.dismiss()
                        }
                    } else {
                        uploadDatabase(barangId, barangImageUrl)
                        progressDialog.dismiss()
                    }

                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }

            parentFragmentManager.popBackStack()
        }
        btnBatal.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    private fun uploadDatabase(barangId: String, imageUrl: String) {
        databaseRef.collection("barang").document(barangId)
            .update(
                "fotoBarang", imageUrl,
                "barangId", barangId,
                "merek", merek,
                "model", model,
                "prosesor", prosesor,
                "ram", ram,
                "sistemOperasi", os,
                "kartuGrafis", grafis,
                "penyimpanan", penyimpanan,
                "ukuranLayar", ukuranLayar,
                "perangkatLunak", perangkatLunak,
                "aksesoris", aksesoris,
                "kondisi", kondisi,
                "biayaSewa", biayaSewa,
                "stok", stok,
            )
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

    private fun getImageUriFromUrl(imageUrl: String, callback: (Uri?) -> Unit) {
        Thread {
            try {
                val bitmap = Glide.with(requireContext())
                    .asBitmap()
                    .load(imageUrl)
                    .submit()
                    .get()

                val uri = saveImageToExternalStorage(bitmap)
                (context as Activity).runOnUiThread {
                    callback(uri)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }.start()
    }

    private fun saveImageToExternalStorage(bitmap: Bitmap): Uri? {
        val filename = "IMG_${System.currentTimeMillis()}.jpg"
        var fos: OutputStream? = null
        var imageUri: Uri? = null

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                context?.contentResolver?.also { resolver ->
                    val contentValues = ContentValues().apply {
                        put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                        put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
                        put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
                    }

                    imageUri =
                        resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                    fos = imageUri?.let { resolver.openOutputStream(it) }
                }
            } else {
                val imagesDir =
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                val image = File(imagesDir, filename)
                fos = FileOutputStream(image)
                imageUri = Uri.fromFile(image)
            }

            fos?.use {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return imageUri
    }

    private fun onPickImageClick() {
        val options = arrayOf("Kamera", "Galeri")
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Pilih Sumber Gambar")
        builder.setItems(options) { _, which ->
            when (which) {
                0 -> checkCameraPermissionAndOpenCamera()
                1 -> dispatchPickImageIntent()
            }
        }
        builder.show()
    }

    private fun checkCameraPermissionAndOpenCamera() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
            == PackageManager.PERMISSION_GRANTED
        ) {
            dispatchTakePictureIntent()
        } else {
            requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(requireContext().packageManager) != null) {
            val photoFile: File? = try {
                createImageFile()
            } catch (ex: IOException) {
                null
            }
            photoFile?.also {
                imageUri = FileProvider.getUriForFile(
                    requireContext(),
                    "${requireContext().packageName}.provider",
                    it
                )
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
                takePictureLauncher.launch(takePictureIntent)
            }
        }
    }

    private fun createImageFile(): File {
        val timestamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val storageDir: File? = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timestamp}_",
            ".jpg",
            storageDir
        )
    }

    private fun dispatchPickImageIntent() {
        val pickPhotoIntent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )
        pickImageLauncher.launch(pickPhotoIntent)
    }

    private val requestCameraPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                dispatchTakePictureIntent()
            } else {
                Toast.makeText(
                    requireContext(),
                    "Izin kamera diperlukan untuk mengambil gambar",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    private val takePictureLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                imageUri?.let {
                    ivEditBarang.setImageURI(it)
                }
            } else {
                Toast.makeText(requireContext(), "Failed to capture image", Toast.LENGTH_SHORT)
                    .show()
            }
        }

    private val pickImageLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                imageUri = data?.data
                ivEditBarang.setImageURI(imageUri)
            }
        }
}