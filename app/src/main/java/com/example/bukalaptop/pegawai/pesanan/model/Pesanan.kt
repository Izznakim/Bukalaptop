package com.example.bukalaptop.pegawai.pesanan.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.Date

@Parcelize
data class Pesanan(
    var id: String = "",
    var idPelanggan: String = "",
    var alamatLengkap: String = "",
    var buktiBayar: String = "",
    var latitude: Double = 0.0,
    var longitude: Double = 0.0,
    var tglPengiriman: Date? = Date(),
    var tglPengambilan: Date? = Date()
) : Parcelable
