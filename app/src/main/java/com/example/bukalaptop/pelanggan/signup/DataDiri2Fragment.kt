package com.example.bukalaptop.pelanggan.signup

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.bukalaptop.R
import com.example.bukalaptop.databinding.FragmentDataDiri2Binding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DataDiri2Fragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var databaseRef: FirebaseFirestore
    private lateinit var storageRef: StorageReference
    private lateinit var tvProgress: TextView
    private lateinit var builder: AlertDialog.Builder
    private lateinit var progressDialog: AlertDialog

    private var isDataValid = false
    private var imageUri: Uri? = null

    private var _binding: FragmentDataDiri2Binding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDataDiri2Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()
        databaseRef = FirebaseFirestore.getInstance()
        storageRef = FirebaseStorage.getInstance().reference

        builder = AlertDialog.Builder(requireContext())
        val inflater = layoutInflater
        val dialogView = inflater.inflate(R.layout.progress_layout, null)
        builder.setView(dialogView)
        builder.setCancelable(false)
        progressDialog = builder.create()

        tvProgress = dialogView.findViewById(R.id.tv_progress)

        val dataEmail = DataDiri2FragmentArgs.fromBundle(arguments as Bundle).email
        val dataPassword = DataDiri2FragmentArgs.fromBundle(arguments as Bundle).password
        val dataNama = DataDiri2FragmentArgs.fromBundle(arguments as Bundle).namaLengkap
        val dataUsername = DataDiri2FragmentArgs.fromBundle(arguments as Bundle).username
        val dataNomorHp = DataDiri2FragmentArgs.fromBundle(arguments as Bundle).nomorHp
        var nomorKtp = ""

        with(binding) {
            ibSignUp.isEnabled = false

            etKtp.doOnTextChanged { text, _, _, _ ->
                if (text != null) {
                    val ktpPattern = "^[0-9]{16}$"
                    if (text.isBlank()) {
                        etKtp.error = "KTP harus diisi"
                        isDataValid = false
                    } else if (!text.matches(ktpPattern.toRegex())) {
                        etKtp.error = "Format KTP tidak valid"
                        isDataValid = false
                    } else {
                        etKtp.error = null
                        isDataValid = true
                        nomorKtp = text.toString()
                    }
                }
                updateSignUpButtonState()
            }

            ivKtp.setOnClickListener {
                checkCameraPermissionAndOpenCamera()
            }

            ibSignUp.setOnClickListener {

                if (imageUri == null) {
                    Toast.makeText(
                        requireContext(),
                        "KTP belum dicantumkan",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                }

                auth.createUserWithEmailAndPassword(dataEmail, dataPassword)
                    .addOnSuccessListener {
                        tvProgress.text = "Membuat akun..."
                        progressDialog.show()
                        val id = auth.currentUser?.uid
                        if (id != null) {
                            val pelanggan = hashMapOf(
                                "id" to id,
                                "email" to dataEmail,
                                "namaLengkap" to dataNama,
                                "username" to dataUsername,
                                "nomorHp" to dataNomorHp,
                                "nomorKtp" to nomorKtp,
                                "fotoKtp" to "",
                                "jenis" to "pelanggan"
                            )

                            databaseRef.collection("pengguna")
                                .add(pelanggan)
                                .addOnSuccessListener {
                                    tvProgress.text = "Membuat akun..."
                                    progressDialog.show()

                                    val imageRef = storageRef.child("pelanggan/${id}.jpg")

                                    val uploadTask = imageRef.putFile(imageUri!!)
                                    uploadTask.addOnSuccessListener {
                                        tvProgress.text = "Membuat akun..."
                                        progressDialog.show()
                                        imageRef.downloadUrl.addOnSuccessListener { uri ->
                                            val imageUrl = uri.toString()
                                            databaseRef.collection("pengguna").addSnapshotListener{ value, error ->
                                                tvProgress.text = "Membuat akun..."
                                                progressDialog.show()
                                                if (value != null) {
                                                    for (document in value) {
                                                        if (document.getString("id") == id) {
                                                            val userType = document.getString("jenis")
                                                            if (userType == "pelanggan") {
                                                                document.reference.update("fotoKtp", imageUrl)
                                                                Toast.makeText(
                                                                    context,
                                                                    "Anda telah terdaftar sebagai pelanggan",
                                                                    Toast.LENGTH_SHORT
                                                                )
                                                                    .show()
                                                                requireActivity().finish()
                                                            } else {
                                                                Toast.makeText(
                                                                    requireContext(),
                                                                    "Anda belum mempunyai akun sebagai pelanggan.",
                                                                    Toast.LENGTH_SHORT
                                                                ).show()
                                                            }
                                                        }
                                                    }
                                                    progressDialog.dismiss()
                                                } else if (error != null) {
                                                    Toast.makeText(requireContext(), "$error", Toast.LENGTH_SHORT).show()
                                                    progressDialog.dismiss()
                                                }
                                            }
                                        }
                                    }.addOnFailureListener { e ->
                                        Toast.makeText(context, "$e", Toast.LENGTH_SHORT).show()
                                        progressDialog.dismiss()
                                        return@addOnFailureListener
                                    }
                                    progressDialog.dismiss()
                                }.addOnFailureListener { e ->
                                    Toast.makeText(context, "$e", Toast.LENGTH_SHORT).show()
                                    progressDialog.dismiss()
                                }
                        }
                    }
            }

            ibBack.setOnClickListener {
                findNavController().navigate(R.id.action_dataDiri2Fragment_to_dataDiriFragment)
            }
        }
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

    private fun createImageFile(): File {
        val timestamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val storageDir: File? = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timestamp}_",
            ".jpg",
            storageDir
        )
    }

    private val takePictureLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                imageUri?.let {
                    binding.ivKtp.setImageURI(it)
                }
            } else {
                Toast.makeText(requireContext(), "Failed to capture image", Toast.LENGTH_SHORT)
                    .show()
                imageUri = null
            }
        }

    private fun updateSignUpButtonState() {
        binding.ibSignUp.isEnabled = isDataValid
        if (binding.ibSignUp.isEnabled) {
            binding.ibSignUp.setBackgroundColor(resources.getColor(R.color.yelowrangeLight))
        } else {
            binding.ibSignUp.setBackgroundColor(Color.GRAY)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}