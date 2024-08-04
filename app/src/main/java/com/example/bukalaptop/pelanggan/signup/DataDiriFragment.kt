package com.example.bukalaptop.pelanggan.signup

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.bukalaptop.R
import com.example.bukalaptop.databinding.FragmentDataDiriBinding

class DataDiriFragment : Fragment() {

    private var isNamaValid = false
    private var isUsernameValid = false
    private var isPhoneValid = false
    private var dataEnable = false

    private var _binding: FragmentDataDiriBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentDataDiriBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val dataEmail=DataDiriFragmentArgs.fromBundle(arguments as Bundle).email
        val dataPassword=DataDiriFragmentArgs.fromBundle(arguments as Bundle).password
        val dataNama = DataDiriFragmentArgs.fromBundle(arguments as Bundle).namaLengkap
        val dataUsername = DataDiriFragmentArgs.fromBundle(arguments as Bundle).username
        val dataNomorHp = DataDiriFragmentArgs.fromBundle(arguments as Bundle).nomorHp
        dataEnable = DataDiriFragmentArgs.fromBundle(arguments as Bundle).isEnable

        with(binding) {
            initSignUpButtonState()
            etNama.setText(dataNama)
            etUsername.setText(dataUsername)
            etNomorHp.setText(dataNomorHp)

            etNama.doOnTextChanged { text, _, _, _ ->
                if (text != null) {
                    if (text.isBlank()) {
                        etNama.error = "Nama Lengkap harus diisi"
                        isNamaValid = false
                    } else {
                        etNama.error = null
                        isNamaValid = true
                    }
                }
                updateSignUpButtonState()
            }

            etUsername.doOnTextChanged { text, _, _, _ ->
                if (text != null) {
                    if (text.isBlank()) {
                        etUsername.error = "Username harus diisi"
                        isUsernameValid = false
                    } else {
                        etUsername.error = null
                        isUsernameValid = true
                    }
                }
                updateSignUpButtonState()
            }

            etNomorHp.doOnTextChanged { text, _, _, _ ->
                if (text != null) {
                    val phonePattern = "^\\+62[0-9]{9,12}$"
                    if (text.isBlank()) {
                        etNomorHp.error = "Nomor HP harus diisi"
                        isPhoneValid = false
                    } else if (!text.matches(phonePattern.toRegex())) {
                        etNomorHp.error = "Format nomor HP tidak valid (contoh: +6281234567890)"
                        isPhoneValid = false
                    } else {
                        etNomorHp.error = null
                        isPhoneValid = true
                    }
                }
                updateSignUpButtonState()
            }

            ibNext.setOnClickListener {
                val toDataDiri2Fragment = DataDiriFragmentDirections.actionDataDiriFragmentToDataDiri2Fragment()
                toDataDiri2Fragment.email = dataEmail
                toDataDiri2Fragment.password = dataPassword
                toDataDiri2Fragment.namaLengkap = etNama.text.toString()
                toDataDiri2Fragment.username = etUsername.text.toString()
                toDataDiri2Fragment.nomorHp = etNomorHp.text.toString()
                findNavController().navigate(toDataDiri2Fragment)
            }

            ibBack.setOnClickListener {
                val toSignUpFragment = DataDiriFragmentDirections.actionDataDiriFragmentToSignUpFragment()
                toSignUpFragment.email = dataEmail
                toSignUpFragment.password = dataPassword
                toSignUpFragment.isEnable=true
                findNavController().navigate(toSignUpFragment)
            }
        }

    }

    private fun initSignUpButtonState() {
        if (dataEnable){
            binding.ibNext.isEnabled=true
            isNamaValid = true
            isUsernameValid = true
            isPhoneValid = true
            binding.ibNext.setBackgroundColor(resources.getColor(R.color.yelowrangeLight))
        }else{
            binding.ibNext.isEnabled=false
            isNamaValid = false
            isUsernameValid = false
            isPhoneValid = false
            binding.ibNext.setBackgroundColor(Color.GRAY)
        }
    }

    private fun updateSignUpButtonState() {
        dataEnable = isNamaValid && isUsernameValid && isPhoneValid
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