package com.example.bukalaptop.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.Date

@Parcelize
data class Pesanan(
    var id: String = "",
    var idPelanggan: String = "",
    var buktiBayar: String = "",
    var tglPengiriman: Date? = Date(),
    var tglPengambilan: Date? = Date(),
    var status: String? = "",
    var alamat: String? = "",
    var latitute: Double? = 0.0,
    var longitude: Double? = 0.0
) : Parcelable
