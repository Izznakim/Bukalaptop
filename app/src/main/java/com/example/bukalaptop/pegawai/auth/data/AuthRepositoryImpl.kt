package com.example.bukalaptop.pegawai.auth.data

import com.example.bukalaptop.pegawai.auth.domain.model.SignInResult
import com.example.bukalaptop.pegawai.auth.domain.repository.AuthRepository
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await

class AuthRepositoryImpl(
    private val auth: FirebaseAuth,
) : AuthRepository {

    override suspend fun signIn(email: String, password: String): SignInResult {
        return try {
            val task = auth.signInWithEmailAndPassword(
                email, password
            ).await()
            val user = task.user?.uid ?: return SignInResult.Error("User tidak ditemukan")
            checkUserType(user)
        } catch (e: Exception) {
            SignInResult.Error(e.message ?: "Sign In gagal")
        }
    }

    override suspend fun checkUserType(userId: String): SignInResult {
        return try {
            val snapshot =
                Firebase.firestore.collection("pengguna").document(userId).get().await()

            val userType = snapshot.getString("jenis")
            if (userType == "pegawai") {
                SignInResult.Success
            } else {
                SignInResult.NotPegawai
            }
        } catch (e: Exception) {
            SignInResult.Error(e.message ?: "Gagal mengambil data pengguna")
        }
    }

    override suspend fun getCurrentUser(): SignInResult {
        val userId = auth.currentUser?.uid ?: return SignInResult.NoUser
        return checkUserType(userId)
    }
}