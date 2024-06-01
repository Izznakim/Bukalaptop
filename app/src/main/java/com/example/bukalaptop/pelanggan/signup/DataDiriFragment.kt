package com.example.bukalaptop.pelanggan.signup

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.util.Patterns
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

        Log.d("mData", "onViewCreated: $dataEmail")
        Log.d("mData", "onViewCreated: $dataPassword")

        with(binding) {
            ibNext.isEnabled = false
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
                findNavController().navigate(R.id.action_dataDiriFragment_to_signUpFragment)
            }
        }

    }

    private fun updateSignUpButtonState() {
        binding.ibNext.isEnabled = isNamaValid && isUsernameValid && isPhoneValid
        if (binding.ibNext.isEnabled){
            binding.ibNext.setBackgroundColor(resources.getColor(R.color.yelowrangeLight))
        }else{
            binding.ibNext.setBackgroundColor(Color.GRAY)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}