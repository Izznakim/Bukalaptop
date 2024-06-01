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
import com.example.bukalaptop.databinding.FragmentDataDiri2Binding
import com.example.bukalaptop.databinding.FragmentDataDiriBinding

class DataDiri2Fragment : Fragment() {

    private var isDataValid = false

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

        val dataEmail=DataDiri2FragmentArgs.fromBundle(arguments as Bundle).email
        val dataPassword=DataDiri2FragmentArgs.fromBundle(arguments as Bundle).password
        val dataNama=DataDiri2FragmentArgs.fromBundle(arguments as Bundle).namaLengkap
        val dataUsername=DataDiri2FragmentArgs.fromBundle(arguments as Bundle).username
        val dataNomorHp=DataDiri2FragmentArgs.fromBundle(arguments as Bundle).nomorHp

        Log.d("mData", "onViewCreated: $dataEmail")
        Log.d("mData", "onViewCreated: $dataPassword")
        Log.d("mData", "onViewCreated: $dataNama")
        Log.d("mData", "onViewCreated: $dataUsername")
        Log.d("mData", "onViewCreated: $dataNomorHp")

        with(binding){
            ibSignUp.isEnabled=false

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
                    }
                }
                updateSignUpButtonState()
            }

            ibSignUp.setOnClickListener {
//                TODO: Upload data ke pengguna
            }

            ibBack.setOnClickListener {
                findNavController().navigate(R.id.action_dataDiri2Fragment_to_dataDiriFragment)
            }
        }
    }

    private fun updateSignUpButtonState() {
        binding.ibSignUp.isEnabled = isDataValid
        if (binding.ibSignUp.isEnabled){
            binding.ibSignUp.setBackgroundColor(resources.getColor(R.color.yelowrangeLight))
        }else{
            binding.ibSignUp.setBackgroundColor(Color.GRAY)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}