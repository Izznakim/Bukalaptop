package com.example.bukalaptop.pegawai.auth.data

import com.example.bukalaptop.pegawai.SignInState
import com.example.bukalaptop.pegawai.auth.domain.AuthRepository
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await

class AuthRepositoryImpl(
    private val auth: FirebaseAuth,
) : AuthRepository {

    override suspend fun signIn(email: String, password: String): SignInState {
        return try {
            val task = auth.signInWithEmailAndPassword(
                email, password
            ).await()
            val user = task.user?.uid ?: return SignInState.Error("User tidak ditemukan")
            checkUserType(user)
        } catch (e: Exception) {
            SignInState.Error(e.message ?: "Sign In gagal")
        }
    }

    override suspend fun checkUserType(userId: String): SignInState {
        return try {
            val snapshot =
                Firebase.firestore.collection("pengguna").document(userId).get().await()

            val userType = snapshot.getString("jenis")
            if (userType == "pegawai") {
                SignInState.Success
            } else {
                SignInState.NotPegawai
            }
        } catch (e: Exception) {
            SignInState.Error(e.message ?: "Gagal mengambil data pengguna")
        }
    }

    override suspend fun getCurrentUser(): SignInState {
        val userId = auth.currentUser?.uid ?: return SignInState.Idle
        return checkUserType(userId)
    }
}