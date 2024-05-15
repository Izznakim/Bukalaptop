package com.example.bukalaptop.pelanggan

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.doOnTextChanged
import com.example.bukalaptop.R
import com.example.bukalaptop.pegawai.PegawaiActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import org.w3c.dom.Text

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
        btnSignIn = findViewById(R.id.btn_signIn)

        builder = AlertDialog.Builder(this)
        val inflater=layoutInflater
        val dialogView=inflater.inflate(R.layout.progress_layout,null)
        builder.setView(dialogView)
        builder.setCancelable(false)
        progressDialog = builder.create()

        tvProgress=dialogView.findViewById(R.id.tv_progress)

        btnSignIn.isEnabled = false

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
                    etEmail.error = null
                    isPasswordValid = true
                }
            }
            updateSigninButtonState()
        }

        btnSignIn.setOnClickListener {
            tvProgress.text="Signing in..."
            progressDialog.show()
            auth.signInWithEmailAndPassword(etEmail.text.toString(), etPassword.text.toString())
                .addOnSuccessListener { task ->
                    val user = task.user?.uid
                    jenisPengguna(user)
                    progressDialog.dismiss()
                }.addOnFailureListener {
                    Toast.makeText(
                        baseContext,
                        "Sign In gagal.",
                        Toast.LENGTH_SHORT,
                    ).show()
                    progressDialog.dismiss()
                }
        }
    }

    private fun jenisPengguna(userId: String?) {
        if (userId != null) {
            tvProgress.text="Signing in..."
            progressDialog.show()
            val db = Firebase.firestore
            val penggunaRef = db.collection("pengguna").document(userId)
            penggunaRef.get().addOnSuccessListener { snapshot ->
                if (snapshot.exists()) {
                    val userType = snapshot.getString("jenis")
                    if (userType == "pelanggan") {
                        startActivity(Intent(this, PelangganActivity::class.java))
                        finish()
                    } else {
                        Toast.makeText(
                            this,
                            "Anda belum mempunyai akun sebagai pelanggan.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                progressDialog.dismiss()
            }
        }
    }

    private fun updateSigninButtonState() {
        btnSignIn.isEnabled = isEmailValid && isPasswordValid
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