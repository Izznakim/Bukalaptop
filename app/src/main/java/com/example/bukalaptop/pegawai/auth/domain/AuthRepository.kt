package com.example.bukalaptop.pegawai.auth.domain

import com.example.bukalaptop.pegawai.SignInState

interface AuthRepository {

    suspend fun signIn(email: String, password: String): SignInState

    suspend fun checkUserType(userId: String): SignInState

    suspend fun getCurrentUser(): SignInState
}