package com.example.bukalaptop.pegawai.auth.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.bukalaptop.pegawai.auth.domain.AuthRepository

class SignInPegawaiViewModelFactory(private val repository: AuthRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SignInPegawaiViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SignInPegawaiViewModel(repository) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")

    }
}