package com.example.bukalaptop.pegawai.auth.presentation

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Patterns
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.bukalaptop.R
import com.example.bukalaptop.databinding.ActivitySignInPegawaiBinding
import com.example.bukalaptop.pegawai.PegawaiActivity
import com.example.bukalaptop.pegawai.SignInState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SignInPegawaiActivity : AppCompatActivity() {

    private val viewModel: SignInPegawaiViewModel by viewModels()

    private lateinit var binding: ActivitySignInPegawaiBinding
    private lateinit var tvProgress: TextView
    private lateinit var builder: AlertDialog.Builder
    private lateinit var progressDialog: AlertDialog

    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            finish()
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInPegawaiBinding.inflate(layoutInflater)
        setContentView(binding.root)

        builder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        val dialogView = inflater.inflate(R.layout.progress_layout, null)
        builder.setView(dialogView)
        builder.setCancelable(false)
        progressDialog = builder.create()

        tvProgress = dialogView.findViewById(R.id.tv_progress)
        binding.btnSignIn.isEnabled = false

        binding.etEmail.doOnTextChanged { text, _, _, _ ->
            viewModel.validateEmail(text?.toString().orEmpty())

            binding.etEmail.error = when {
                text.isNullOrBlank() -> getString(R.string.email_harus_diisi)
                !Patterns.EMAIL_ADDRESS.matcher(text)
                    .matches() -> getString(R.string.email_tidak_valid)

                else -> null
            }
            updateSigninButtonState()
        }

        binding.etPassword.doOnTextChanged { text, _, _, _ ->
            viewModel.validatePassword(text?.toString().orEmpty())

            binding.etPassword.error = when {
                text.isNullOrBlank() -> getString(R.string.password_harus_diisi)
                text.length < 6 -> getString(R.string.password_minimal_harus_6_karakter)
                else -> null
            }
            updateSigninButtonState()
        }

        binding.btnSignIn.setOnClickListener {
            viewModel.signIn(
                binding.etEmail.text.toString(),
                binding.etPassword.text.toString()
            )
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.signInState.collect { state ->
                    when (state) {
                        SignInState.Loading -> {
                            tvProgress.text = getString(R.string.signing_in)
                            progressDialog.show()
                        }

                        SignInState.Success -> {
                            progressDialog.dismiss()

                            startActivity(
                                Intent(
                                    this@SignInPegawaiActivity,
                                    PegawaiActivity::class.java
                                )
                            )
                            finish()
                        }

                        is SignInState.Error -> {
                            progressDialog.dismiss()

                            Toast.makeText(
                                this@SignInPegawaiActivity,
                                state.message,
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        SignInState.NotPegawai -> {
                            progressDialog.dismiss()

                            Toast.makeText(
                                this@SignInPegawaiActivity,
                                getString(R.string.anda_belum_mempunyai_akun_sebagai_pegawai),
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        SignInState.Idle -> Unit
                    }
                }
            }
        }
    }

    private fun updateSigninButtonState() {
        binding.btnSignIn.isEnabled = viewModel.canSignIn
        if (viewModel.canSignIn) {
            binding.btnSignIn.setBackgroundColor(ContextCompat.getColor(this, R.color.red))
        } else {
            binding.btnSignIn.setBackgroundColor(Color.GRAY)
        }
    }

    public override fun onStart() {
        super.onStart()

        viewModel.checkCurrentUser()
    }

    override fun onResume() {
        super.onResume()

        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}