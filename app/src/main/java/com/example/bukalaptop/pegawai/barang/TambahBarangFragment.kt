package com.example.bukalaptop.pegawai.barang

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.example.bukalaptop.R
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.ByteArrayOutputStream

class TambahBarangFragment : Fragment() {

    private lateinit var ivTambahBarang: ImageView
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
    private lateinit var btnTambah: Button
    private lateinit var storageRef: StorageReference
    private lateinit var databaseRef: FirebaseFirestore

    private var imageBitmap: Bitmap? = null
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
    private var biayaSewa: String = ""
    private var stok: String = ""


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_tambah_barang, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        storageRef = FirebaseStorage.getInstance().reference

        databaseRef = FirebaseFirestore.getInstance()

        ivTambahBarang = view.findViewById(R.id.iv_tambah_barang)
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
        btnTambah = view.findViewById(R.id.btn_tambah)
        btnBatal = view.findViewById(R.id.btn_batal)

        ivTambahBarang.setOnClickListener {
            onPickImageClick()
        }

        btnTambah.setOnClickListener {
            merek = etMerek.text.toString()
            model = etModel.text.toString()
            prosesor = etProsesor.text.toString()
            ram = etRam.text.toString()
            os = etOs.text.toString()
            grafis = etGrafis.text.toString()
            penyimpanan = etPenyimpanan.text.toString()
            ukuranLayar = etUkuranLayar.text.toString()
            perangkatLunak = etPerangkatLunak.text.toString()
            aksesoris = etAksesoris.text.toString()
            kondisi = etKondisi.text.toString()
            biayaSewa = etBiayaSewa.text.toString()
            stok = etStok.text.toString()


            if (imageBitmap == null) {
                ivTambahBarang.requestFocus()
                Toast.makeText(context, "Gambar masih kosong!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

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

            if (biayaSewa.isEmpty()) {
                etBiayaSewa.requestFocus()
                etBiayaSewa.error = "Biaya Sewa harus diisi"
                return@setOnClickListener
            }

            if (stok.isEmpty()) {
                etStok.requestFocus()
                etStok.error = "Stok harus diisi"
                return@setOnClickListener
            }

            Toast.makeText(context, "Tambahkan ke firebase dan firestore", Toast.LENGTH_SHORT)
                .show()
            uploadImageAndAddData(
                imageBitmap,
                merek,
                model,
                prosesor,
                ram,
                os,
                grafis,
                penyimpanan,
                ukuranLayar,
                perangkatLunak,
                aksesoris,
                kondisi,
                biayaSewa,
                stok
            )
        }
        btnBatal.setOnClickListener {
            parentFragmentManager.popBackStack()
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


    private fun uploadImageAndAddData(
        imageBitmap: Bitmap?,
        merek: String,
        model: String,
        prosesor: String,
        ram: String,
        os: String,
        grafis: String,
        penyimpanan: String,
        ukuranLayar: String,
        perangkatLunak: String,
        aksesoris: String,
        kondisi: String,
        biayaSewa: String,
        stok: String
    ) {
        val baos = ByteArrayOutputStream()
        imageBitmap?.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val imageData = baos.toByteArray()

        val newImageRef = storageRef.child("barang/${System.currentTimeMillis()}.jpg")

        val uploadTask = newImageRef.putBytes(imageData)
        uploadTask.addOnSuccessListener {
            newImageRef.downloadUrl.addOnSuccessListener { uri ->
                val imageUrl = uri.toString()
                addDataToFirestore(
                    imageUrl,
                    merek,
                    model,
                    prosesor,
                    ram,
                    os,
                    grafis,
                    penyimpanan,
                    ukuranLayar,
                    perangkatLunak,
                    aksesoris,
                    kondisi,
                    biayaSewa,
                    stok
                )
            }
        }.addOnFailureListener {
            Toast.makeText(context, "$it", Toast.LENGTH_SHORT).show()
        }
    }

    private fun addDataToFirestore(
        imageUrl: String,
        merek: String,
        model: String,
        prosesor: String,
        ram: String,
        os: String,
        grafis: String,
        penyimpanan: String,
        ukuranLayar: String,
        perangkatLunak: String,
        aksesoris: String,
        kondisi: String,
        biayaSewa: String,
        stok: String
    ) {
        val data = hashMapOf(
            "fotoBarang" to imageUrl,
            "merek" to merek,
            "model" to model,
            "prosesor" to prosesor,
            "ram" to ram,
            "sistemOperasi" to os,
            "kartuGrafis" to grafis,
            "penyimpanan" to penyimpanan,
            "ukuranLayar" to ukuranLayar,
            "perangkatLunak" to perangkatLunak,
            "aksesoris" to aksesoris,
            "kondisi" to kondisi,
            "biayaSewa" to biayaSewa.toInt(),
            "stok" to stok.toInt(),
        )

        databaseRef.collection("barang")
            .add(data)
            .addOnSuccessListener { document ->
                val newImageRef = storageRef.child("barang/${document.id}.jpg")
                newImageRef.downloadUrl.addOnSuccessListener { uri ->
                    val newImageUrl = uri.toString()
                    addDataToFirestore(
                        newImageUrl,
                        merek,
                        model,
                        prosesor,
                        ram,
                        os,
                        grafis,
                        penyimpanan,
                        ukuranLayar,
                        perangkatLunak,
                        aksesoris,
                        kondisi,
                        biayaSewa,
                        stok
                    )
                }
                // Sekarang, Anda dapat menggabungkan ID dokumen dengan nama file gambar di Firebase Storage

                parentFragmentManager.popBackStack()
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "$e", Toast.LENGTH_SHORT).show()
            }
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
            if (result.resultCode == RESULT_OK) {
                val data: Intent? = result.data
                imageBitmap = data?.extras?.get("data") as Bitmap
                ivTambahBarang.setImageBitmap(imageBitmap)
            }
        }

    private val pickImageLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val data: Intent? = result.data
                val imageUri = data?.data
                imageBitmap =
                    MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, imageUri)
                ivTambahBarang.setImageBitmap(imageBitmap)
            }
        }
}