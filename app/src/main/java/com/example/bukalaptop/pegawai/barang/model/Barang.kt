package com.example.bukalaptop.pegawai.barang.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Barang(
    var barangId: String = "",
    var fotoBarang: String = "",
    var merek: String = "",
    var model: String = "",
    var prosesor: String = "",
    var kartuGrafis: String = "",
    var ram: String = "",
    var penyimpanan: String = "",
    var sistemOperasi: String = "",
    var perangkatLunak: String = "",
    var ukuranLayar: String = "",
    var aksesoris: String = "",
    var kondisi: String = "",
    var biayaSewa: Int = 0,
    var stok: Int = 0
) : Parcelable
