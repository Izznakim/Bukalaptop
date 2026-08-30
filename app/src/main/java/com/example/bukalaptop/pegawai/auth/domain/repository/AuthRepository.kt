package com.example.bukalaptop.pegawai.auth.domain.repository

import com.example.bukalaptop.pegawai.auth.domain.model.SignInResult

interface AuthRepository {

    suspend fun signIn(email: String, password: String): SignInResult

    suspend fun checkUserType(userId: String): SignInResult

    suspend fun getCurrentUser(): SignInResult
}