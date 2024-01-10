package com.example.bukalaptop.pegawai

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import com.example.bukalaptop.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginPegawaiActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnSignIn: Button

    private var isEmailValid = false
    private var isPasswordValid = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_pegawai)

        auth = Firebase.auth

        etEmail = findViewById(R.id.et_email)
        etPassword = findViewById(R.id.et_password)
        btnSignIn = findViewById(R.id.btn_signIn)

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
            auth.signInWithEmailAndPassword(etEmail.text.toString(), etPassword.text.toString())
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val pegawaiIntent = Intent(this, PegawaiActivity::class.java)
                        startActivity(pegawaiIntent)
                        finish()
                    } else {
                        Toast.makeText(
                            baseContext,
                            "Sign In gagal.",
                            Toast.LENGTH_SHORT,
                        ).show()
                    }
                }
        }
    }

    private fun updateSigninButtonState() {
        btnSignIn.isEnabled = isEmailValid && isPasswordValid
    }

    public override fun onStart() {
        super.onStart()

        val currentUser = auth.currentUser
        if (currentUser != null) {
            val pegawaiIntent = Intent(this, PegawaiActivity::class.java)
            startActivity(pegawaiIntent)
            finish()
        }
    }
}