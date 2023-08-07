package com.example.bukalaptop.pegawai

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Pesanan(
    var id:String="",
    var namaLengkap:String="",
    var alamatEmail:String="",
    var nomorTelepon:String="",
) : Parcelable
