package com.example.bukalaptop.pegawai

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.bukalaptop.R
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator


class PegawaiActivity : AppCompatActivity() {

    companion object {
        private val TAB_TITLES = arrayListOf("Pesanan", "Barang")
    }

    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            finish()
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pegawai)

        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)

        val sectionPagerAdapter = SectionPagerAdapter(this)
        val viewPager: ViewPager2 = findViewById(R.id.view_pager)
        viewPager.adapter = sectionPagerAdapter
        val tabs: TabLayout = findViewById(R.id.tabs)
        TabLayoutMediator(tabs, viewPager) { tab, position ->
            tab.text = TAB_TITLES[position]
        }.attach()

        val text: Spannable = SpannableString("Halaman Pegawai")
        text.setSpan(
            ForegroundColorSpan(Color.RED),
            0,
            text.length,
            Spannable.SPAN_INCLUSIVE_INCLUSIVE
        )
        supportActionBar?.title = text
        supportActionBar?.elevation = 0f
        supportActionBar?.setBackgroundDrawable(ColorDrawable(Color.BLACK))
    }
}