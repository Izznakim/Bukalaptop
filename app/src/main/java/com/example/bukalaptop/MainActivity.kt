package com.example.bukalaptop

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.example.bukalaptop.databinding.ActivityMainBinding
import com.example.bukalaptop.pegawai.SignInPegawaiActivity
import com.example.bukalaptop.pelanggan.PelangganActivity
import com.example.bukalaptop.pelanggan.SignInPelangganActivity

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnPegawai.setOnClickListener {
            val pegawaiIntent = Intent(this, SignInPegawaiActivity::class.java)
            startActivity(pegawaiIntent)
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        }
        binding.btnPelanggan.setOnClickListener {
            val pelangganIntent = Intent(this, SignInPelangganActivity::class.java)
            startActivity(pelangganIntent)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }
    }
}