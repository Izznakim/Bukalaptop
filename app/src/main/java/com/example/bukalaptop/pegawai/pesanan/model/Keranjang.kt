package com.example.bukalaptop.pegawai.pesanan.model

import android.os.Parcelable
import com.example.bukalaptop.pegawai.barang.model.Barang
import kotlinx.parcelize.Parcelize

@Parcelize
data class Keranjang(
    var barang: Barang,
    var jumlah: Int = 0
) : Parcelable {
    constructor(barang: Barang) : this(barang, 0)
}
