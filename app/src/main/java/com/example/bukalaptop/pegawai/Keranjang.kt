package com.example.bukalaptop.pegawai

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Keranjang(
    var merek: String = "",
    var model: String = "",
    val jumlah: Int = 0,
    var subtotal: Int = 0
) : Parcelable
