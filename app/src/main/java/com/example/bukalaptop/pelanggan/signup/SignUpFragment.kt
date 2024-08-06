package com.example.bukalaptop.pelanggan.signup

import android.graphics.Color
import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.bukalaptop.R
import com.example.bukalaptop.databinding.FragmentSignUpBinding

class SignUpFragment : Fragment() {

    private var _binding: FragmentSignUpBinding? = null
    private var isEmailValid = false
    private var isPasswordValid = false
    private var isUlangPasswordValid = false
    private var dataEnable = false

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentSignUpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val dataEmail=SignUpFragmentArgs.fromBundle(arguments as Bundle).email
        val dataPassword=SignUpFragmentArgs.fromBundle(arguments as Bundle).password
        val dataNama = SignUpFragmentArgs.fromBundle(arguments as Bundle).namaLengkap
        val dataUsername = SignUpFragmentArgs.fromBundle(arguments as Bundle).username
        val dataNomorHp = SignUpFragmentArgs.fromBundle(arguments as Bundle).nomorHp
        dataEnable = SignUpFragmentArgs.fromBundle(arguments as Bundle).isEnable

        with(binding) {
            initSignUpButtonState()
            etEmail.setText(dataEmail)
            etPassword.setText(dataPassword)
            etUlangiPassword.setText(dataPassword)

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
                updateSignUpButtonState()
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
                updateSignUpButtonState()
            }

            etUlangiPassword.doOnTextChanged { text, _, _, _ ->
                if (text != null) {
                    if (text.toString() != etPassword.text.toString()) {
                        etUlangiPassword.error = "Password tidak sama"
                        isUlangPasswordValid = false
                    } else {
                        etPassword.error = null
                        isUlangPasswordValid = true
                    }
                }
                updateSignUpButtonState()
            }

            ibNext.setOnClickListener {
                val toDataDiriFragment = SignUpFragmentDirections.actionSignUpFragmentToDataDiriFragment()
                toDataDiriFragment.email = etEmail.text.toString()
                toDataDiriFragment.password = etPassword.text.toString()
                toDataDiriFragment.namaLengkap = dataNama
                toDataDiriFragment.username = dataUsername
                toDataDiriFragment.nomorHp = dataNomorHp
                findNavController().navigate(toDataDiriFragment)
            }
        }
    }

    private fun initSignUpButtonState() {
        if (dataEnable){
            binding.ibNext.isEnabled=true
            isEmailValid = true
            isPasswordValid = true
            isUlangPasswordValid = true
            binding.ibNext.setBackgroundColor(resources.getColor(R.color.yelowrangeLight))
        }else{
            binding.ibNext.isEnabled=false
            isEmailValid = false
            isPasswordValid = false
            isUlangPasswordValid = false
            binding.ibNext.setBackgroundColor(Color.GRAY)
        }
    }

    private fun updateSignUpButtonState() {
        dataEnable = isEmailValid && isPasswordValid && isUlangPasswordValid
        if (dataEnable){
            binding.ibNext.isEnabled=true
            binding.ibNext.setBackgroundColor(resources.getColor(R.color.yelowrangeLight))
        }else{
            binding.ibNext.isEnabled=false
            binding.ibNext.setBackgroundColor(Color.GRAY)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}