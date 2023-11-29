package com.example.bukalaptop.pelanggan

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import com.example.bukalaptop.R
import com.example.bukalaptop.pegawai.PegawaiActivity
import com.example.bukalaptop.pegawai.SectionPagerPegawaiAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class PelangganActivity : AppCompatActivity() {

    companion object{
        private val TAB_TITLES = arrayListOf("Barang", "Checkout","Riwayat")
    }

    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            finish()
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pelanggan)

        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)

        val sectionPagerPelangganAdapter = SectionPagerPelangganAdapter(this)
        val viewPager: ViewPager2 = findViewById(R.id.view_pager)
        viewPager.adapter = sectionPagerPelangganAdapter
        val tabs: TabLayout = findViewById(R.id.tabs)
        TabLayoutMediator(tabs, viewPager) { tab, position ->
            tab.text = TAB_TITLES[position]
        }.attach()

        val text: Spannable = SpannableString("Halaman Pelanggan")
        text.setSpan(
            ForegroundColorSpan(ContextCompat.getColor(this, R.color.yelowrangeLight)),
            0,
            text.length,
            Spannable.SPAN_INCLUSIVE_INCLUSIVE
        )
        supportActionBar?.title = text
        supportActionBar?.elevation = 0f
        supportActionBar?.setBackgroundDrawable(ColorDrawable(Color.BLACK))
    }
}