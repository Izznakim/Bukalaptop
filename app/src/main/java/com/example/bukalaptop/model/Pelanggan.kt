package com.example.bukalaptop.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Pelanggan(
    val namaLengkap: String = "",
    val username: String = "",
    val email: String = "",
    val nomorHp: String = "",
    val fotoKtp: String = "",
    val nomorKtp: String = ""
) : Parcelable
