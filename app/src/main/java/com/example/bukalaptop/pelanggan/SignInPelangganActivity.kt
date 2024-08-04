package com.example.bukalaptop.pelanggan

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat
import androidx.core.widget.doOnTextChanged
import com.example.bukalaptop.R
import com.example.bukalaptop.pelanggan.signup.SignUpPelangganActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class SignInPelangganActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var tvLupaPassword: TextView
    private lateinit var btnSignIn: Button
    private lateinit var tvSignUp: TextView
    private lateinit var tvProgress: TextView
    private lateinit var builder: AlertDialog.Builder
    private lateinit var progressDialog: AlertDialog

    private var isEmailValid = false
    private var isPasswordValid = false

    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            finish()
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in_pelanggan)

        auth = Firebase.auth

        etEmail = findViewById(R.id.et_email)
        etPassword = findViewById(R.id.et_password)
        tvLupaPassword = findViewById(R.id.tv_lupa_password)
        btnSignIn = findViewById(R.id.btn_signIn)
        tvSignUp = findViewById(R.id.tv_signUp)

        builder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        val dialogView = inflater.inflate(R.layout.progress_layout, null)
        builder.setView(dialogView)
        builder.setCancelable(false)
        progressDialog = builder.create()

        tvProgress = dialogView.findViewById(R.id.tv_progress)

        btnSignIn.isEnabled = false

        tvLupaPassword.setOnClickListener {
            if (isEmailValid) {
                val builder = android.app.AlertDialog.Builder(this)

                builder.setMessage(
                    HtmlCompat.fromHtml(
                        "Apakah Anda sudah yakin ingin mereset password pada email <b>${etEmail.text}</b>?",
                        HtmlCompat.FROM_HTML_MODE_LEGACY
                    )
                )
                    .setTitle("Reset Password")

                builder.setPositiveButton("Ya") { _, _ ->
                    CoroutineScope(Dispatchers.Main).launch {
                        try {
                            tvProgress.text = "Mereset password..."
                            progressDialog.show()

                            auth.sendPasswordResetEmail(etEmail.text.toString()).await()
                            Toast.makeText(
                                this@SignInPelangganActivity,
                                "Silahkan cek email Anda",
                                Toast.LENGTH_LONG
                            )
                                .show()
                        } catch (e: Exception) {
                            Toast.makeText(
                                this@SignInPelangganActivity,
                                "Gagal mereset password: $e",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        } finally {
                            progressDialog.dismiss()
                        }
                    }
                }

                builder.setNegativeButton("Tidak") { dialog, _ ->
                    dialog.cancel()
                }

                val dialog = builder.create()
                dialog.show()
            } else {
                Toast.makeText(
                    this,
                    "Masukkan email yang valid terlebih dahulu",
                    Toast.LENGTH_SHORT
                ).show()
                etEmail.requestFocus()
            }
        }

        etEmail.doOnTextChanged { text, _, _, _ ->
            if (text != null) {
                if (text.isBlank()) {
                    etEmail.error = "Email harus diisi"
                    isEmailValid = false
                } else if (!Patterns.EMAIL_ADDRESS.matcher(text).matches()) {
                    etEmail.error = "Email tidak valid"
                    isEmailValid = false
                } else {
                    etEmail.error = null
                    isEmailValid = true
                }
            }
            updateSigninButtonState()
        }

        etPassword.doOnTextChanged { text, _, _, _ ->
            if (text != null) {
                if (text.isBlank() || text.length < 6) {
                    etPassword.error = "Password minimal harus 6 karakter"
                    isPasswordValid = false
                } else {
                    etPassword.error = null
                    isPasswordValid = true
                }
            }
            updateSigninButtonState()
        }

        btnSignIn.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                try {
                    tvProgress.text = "Signing in..."
                    progressDialog.show()
                    val authResult = auth.signInWithEmailAndPassword(
                        etEmail.text.toString(),
                        etPassword.text.toString()
                    )
                        .await()

                    val user = authResult.user?.uid
                    jenisPengguna(user)
                } catch (e: Exception) {
                    Toast.makeText(baseContext, "Sign In gagal.", Toast.LENGTH_SHORT).show()
                } finally {
                    progressDialog.dismiss()
                }
            }
        }

        tvSignUp.setOnClickListener {
            startActivity(Intent(this, SignUpPelangganActivity::class.java))
        }
    }

    private fun jenisPengguna(userId: String?) {
        if (userId != null) {
            CoroutineScope(Dispatchers.Main).launch {
                try {
                    tvProgress.text = "Signing in..."
                    progressDialog.show()

                    val db = Firebase.firestore
                    val snapshot = db.collection("pengguna").get().await()
                    val document = snapshot.documents.firstOrNull { it.getString("id") == userId }

                    if (document != null) {
                        val userType = document.getString("jenis")
                        if (userType == "pelanggan") {
                            startActivity(
                                Intent(
                                    this@SignInPelangganActivity,
                                    PelangganActivity::class.java
                                )
                            )
                            finish()
                        } else {
                            Toast.makeText(
                                this@SignInPelangganActivity,
                                "Anda belum mempunyai akun sebagai pelanggan.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        Toast.makeText(
                            this@SignInPelangganActivity,
                            "Pengguna tidak ditemukan.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(this@SignInPelangganActivity, "$e", Toast.LENGTH_SHORT).show()
                } finally {
                    progressDialog.dismiss()
                }
            }
        }
    }

    private fun updateSigninButtonState() {
        btnSignIn.isEnabled = isEmailValid && isPasswordValid
        if (btnSignIn.isEnabled) {
            btnSignIn.setBackgroundColor(resources.getColor(R.color.yelowrangeLight))
        } else {
            btnSignIn.setBackgroundColor(Color.GRAY)
        }
    }

    public override fun onStart() {
        super.onStart()

        val currentUser = auth.currentUser
        jenisPengguna(currentUser?.uid)
    }

    override fun onResume() {
        super.onResume()

        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
    }
}