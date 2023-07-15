package com.example.bukalaptop.pegawai

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Pesanan(
    var id: String = "",
    var namaLengkap: String = "",
    var email: String = "",
    var nomorTelepon: String = "",
    var alamatLengkap: String = "",
    var buktiBayar: String = "",
    var latitude: Double = 0.0,
    var longitude: Double = 0.0
) : Parcelable