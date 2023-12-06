package com.example.bukalaptop.model

import android.os.Parcelable
import com.example.bukalaptop.pegawai.barang.model.Barang
import kotlinx.parcelize.Parcelize

@Parcelize
data class Keranjang(
    var barang: Barang,
    var jumlah: Int = 0
) : Parcelable
