package com.example.bukalaptop.pegawai.auth.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bukalaptop.pegawai.auth.domain.model.SignInResult
import com.example.bukalaptop.pegawai.auth.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignInPegawaiViewModel @Inject constructor(private val repository: AuthRepository) :
    ViewModel() {

    private val emailPattern = Regex("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")

    var email by mutableStateOf("")
        private set

    var password by mutableStateOf("")
        private set

    var isEmailValid = false
        private set

    var isPasswordValid = false
        private set

    val canSignIn: Boolean
        get() = isEmailValid && isPasswordValid

    private val _signInState = MutableStateFlow<SignInState>(SignInState.Idle)
    val signInState = _signInState.asStateFlow()

    fun onEmailChange(newEmail: String) {
        email = newEmail
        validateEmail(newEmail)
    }

    fun onPasswordChange(newPassword: String) {
        password = newPassword
        validatePassword(newPassword)
    }

    fun validateEmail(email: String) {
        isEmailValid = email.isNotBlank() && emailPattern.matches(email)
    }

    fun validatePassword(password: String) {
        isPasswordValid = password.isNotBlank() && password.length >= 6
    }

    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            _signInState.value = SignInState.Loading
            val result = repository.signIn(email, password)
            _signInState.value = result.toSignInState()
        }
    }

    fun checkCurrentUser() {
        viewModelScope.launch {
            _signInState.value = SignInState.Loading
            val result = repository.getCurrentUser()

            _signInState.value = when(result){
                SignInResult.Success -> SignInState.Success
                SignInResult.NotPegawai -> SignInState.Idle
                SignInResult.NoUser -> SignInState.Idle
                is SignInResult.Error -> SignInState.Error(result.message)
            }
        }
    }

    private fun SignInResult.toSignInState(): SignInState {
        return when (this) {
            SignInResult.Success -> SignInState.Success
            SignInResult.NotPegawai -> SignInState.NotPegawai
            SignInResult.NoUser -> SignInState.Idle
            is SignInResult.Error -> SignInState.Error(message)
        }
    }
}