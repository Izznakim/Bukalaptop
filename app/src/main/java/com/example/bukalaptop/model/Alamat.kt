package com.example.bukalaptop.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Alamat(
    val atasNama:String="",
    val nomorPengiriman:String="",
    val alamatLengkapPengiriman:String="",
    val alamatSingkatPengiriman:String=""
) : Parcelable
