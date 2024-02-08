package com.example.bukalaptop

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.example.bukalaptop.pegawai.SignInPegawaiActivity

class MainActivity : AppCompatActivity() {

    private lateinit var btnPegawai: LinearLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnPegawai = findViewById(R.id.btn_pegawai)

        btnPegawai.setOnClickListener {
            val pegawaiIntent = Intent(this, SignInPegawaiActivity::class.java)
            startActivity(pegawaiIntent)
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        }
    }
}