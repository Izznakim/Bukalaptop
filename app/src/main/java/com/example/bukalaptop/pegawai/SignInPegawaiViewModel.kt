package com.example.bukalaptop.pegawai

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class SignInPegawaiViewModel : ViewModel() {

    //    private val auth = Firebase.auth
    private val emailPattern = Regex("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")

    private var snapshotListener: ListenerRegistration? = null

    var isEmailValid = false
        private set

    var isPasswordValid = false
        private set

    val canSignIn: Boolean
        get() = isEmailValid && isPasswordValid

    private val _signInState = MutableStateFlow<SignInState>(SignInState.Idle)
    val signInState = _signInState.asStateFlow()

    fun validateEmail(email: String) {
        isEmailValid = email.isNotBlank() && emailPattern.matches(email)
    }

    fun validatePassword(password: String) {
        isPasswordValid = password.isNotBlank() && password.length >= 6
    }

    fun signIn(email: String, password: String) {
        val auth = Firebase.auth

        _signInState.value = SignInState.Loading

        viewModelScope.launch {
            try {
                val task = auth.signInWithEmailAndPassword(
                    email, password
                ).await()
                val user = task.user?.uid
                checkUserType(user)
            } catch (e: Exception) {
                _signInState.value = SignInState.Error(e.message ?: "Sign In gagal")
            }
        }
    }

    fun checkUserType(userId: String?) {
        if (userId == null) return

        _signInState.value = SignInState.Loading

        val db = Firebase.firestore
        val penggunaRef = db.collection("pengguna").document(userId)

        snapshotListener = penggunaRef.addSnapshotListener { value, error ->
            if (value != null) {
                val userType = value.getString("jenis")
                if (userType == "pegawai") {
                    _signInState.value = SignInState.Success
                } else {
                    _signInState.value = SignInState.NotPegawai
                }
            } else if (error != null) {
                _signInState.value =
                    SignInState.Error(error.message ?: "Gagal mengambil data pengguna")
            }
        }

    }

    fun checkCurrentUser() {
        val auth = Firebase.auth
        checkUserType(auth.currentUser?.uid)
    }

    override fun onCleared() {
        snapshotListener?.remove()
    }

}