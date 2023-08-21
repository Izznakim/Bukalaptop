package com.example.bukalaptop.pegawai.pesanan.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Keranjang(
    var fotoBarang: String = "",
    var merek: String = "",
    var model: String = "",
    var prosesor: String = "",
    var biayaSewa: Int = 0,
    val jumlah: Int = 0
) : Parcelable
