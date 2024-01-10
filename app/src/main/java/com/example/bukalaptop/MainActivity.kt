package com.example.bukalaptop

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.example.bukalaptop.pegawai.LoginPegawaiActivity
import com.example.bukalaptop.pegawai.PegawaiActivity
import com.example.bukalaptop.pelanggan.PelangganActivity

class MainActivity : AppCompatActivity() {

    private lateinit var btnPegawai: LinearLayout
    private lateinit var btnPelanggan: LinearLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnPegawai = findViewById(R.id.btn_pegawai)
        btnPelanggan = findViewById(R.id.btn_pelanggan)

        btnPegawai.setOnClickListener {
            val pegawaiIntent = Intent(this, LoginPegawaiActivity::class.java)
            startActivity(pegawaiIntent)
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        }
        btnPelanggan.setOnClickListener {
            val pelangganIntent = Intent(this, PelangganActivity::class.java)
            startActivity(pelangganIntent)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }
    }
}