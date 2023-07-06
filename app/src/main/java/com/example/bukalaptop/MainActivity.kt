package com.example.bukalaptop

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.example.bukalaptop.pegawai.PegawaiActivity

class MainActivity : AppCompatActivity() {

    private lateinit var btnPegawai: Button
    private lateinit var btnPelanggan: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnPegawai = findViewById(R.id.btn_pegawai)
        btnPelanggan = findViewById(R.id.btn_pelanggan)

        btnPelanggan.setOnClickListener {
            val pelangganIntent = Intent(this, PelangganActivity::class.java)
            startActivity(pelangganIntent)
        }
        btnPegawai.setOnClickListener {
            val pegawaiIntent = Intent(this, PegawaiActivity::class.java)
            startActivity(pegawaiIntent)
        }
    }
}