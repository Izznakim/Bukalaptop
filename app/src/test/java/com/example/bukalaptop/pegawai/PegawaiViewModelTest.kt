package com.example.bukalaptop.pegawai

import com.example.bukalaptop.pegawai.auth.domain.repository.AuthRepository
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify

class PegawaiViewModelTest {

    private lateinit var viewModel: PegawaiViewModel
    private lateinit var repository: AuthRepository

    @Before
    fun setup() {
        repository = mock()
        viewModel = PegawaiViewModel(repository)
    }

    @Test
    fun `signOut calls repository signOut`() {
        viewModel.signOut()
        verify(repository).signOut()
    }
}