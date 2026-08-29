package com.example.bukalaptop.pegawai

import com.example.bukalaptop.pegawai.auth.domain.AuthRepository
import com.example.bukalaptop.pegawai.auth.presentation.SignInPegawaiViewModel
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock

class SignInPegawaiViewModelTest {

    private lateinit var viewModel: SignInPegawaiViewModel
    private lateinit var repository: AuthRepository

    @Before
    fun setup() {
        repository = mock(AuthRepository::class.java)
        viewModel = SignInPegawaiViewModel(repository)
    }

    @Test
    fun `can sign in when email and password are valid`() {
        // email valid
        // password valid
        // expected: canSignIn = true
        viewModel.validateEmail("a@gmail.com")
        viewModel.validatePassword("abcdef")

        assertTrue(viewModel.canSignIn)
    }

    @Test
    fun `can not sign in when email is invalid`() {
        // email invalid
        // password valid
        // expected: canSignIn = false
        viewModel.validateEmail("a")
        viewModel.validatePassword("abcdef")

        assertFalse(viewModel.canSignIn)
    }

    @Test
    fun `can not sign in when password is invalid`() {
        // email valid
        // password invalid
        // expected: canSignIn = false
        viewModel.validateEmail("a@gmail.com")
        viewModel.validatePassword("abcde")

        assertFalse(viewModel.canSignIn)
    }

    @Test
    fun `can not sign in when email and password are invalid`() {
        // email invalid
        // password invalid
        // expected: canSignIn = false
        viewModel.validateEmail("a")
        viewModel.validatePassword("abcde")

        assertFalse(viewModel.canSignIn)
    }

    @Test
    fun `email is valid when email format is valid`() {
        // expected: isEmailValid = true
        viewModel.validateEmail("a@gmail.com")

        assertTrue(viewModel.isEmailValid)
    }

    @Test
    fun `email is invalid when email is empty`() {
        // expected: isEmailValid = false
        viewModel.validateEmail("")

        assertFalse(viewModel.isEmailValid)
    }

    @Test
    fun `email is invalid when email format is invalid`() {
        // expected: isEmailValid = false
        viewModel.validateEmail("a")

        assertFalse(viewModel.isEmailValid)
    }

    @Test
    fun `password is valid when password has at least 6 characters`() {
        // expected: isPasswordValid = true
        viewModel.validatePassword("abcdef")

        assertTrue(viewModel.isPasswordValid)
    }

    @Test
    fun `password is invalid when password is empty`() {
        // expected: isPasswordValid = false
        viewModel.validatePassword("")

        assertFalse(viewModel.isPasswordValid)
    }

    @Test
    fun `password is invalid when password has less than 6 characters`() {
        // expected: isPasswordValid = false
        viewModel.validatePassword("abcde")

        assertFalse(viewModel.isPasswordValid)
    }
}