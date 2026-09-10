package com.example.bukalaptop.pegawai

import androidx.lifecycle.ViewModel
import com.example.bukalaptop.pegawai.auth.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PegawaiViewModel @Inject constructor(private val repository: AuthRepository) : ViewModel() {
    fun signOut(){
        repository.signOut()
    }
}