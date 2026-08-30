package com.example.bukalaptop.pegawai.auth.presentation

sealed interface SignInState {
    data object Idle : SignInState
    data object Loading : SignInState
    data object Success : SignInState
    data object NotPegawai : SignInState
    data class Error(val message: String) : SignInState
}