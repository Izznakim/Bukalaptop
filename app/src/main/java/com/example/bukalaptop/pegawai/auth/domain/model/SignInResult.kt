package com.example.bukalaptop.pegawai.auth.domain.model

sealed interface SignInResult {
    data object Success : SignInResult
    data object NotPegawai : SignInResult
    data object NoUser: SignInResult
    data class Error(val message: String) : SignInResult
}