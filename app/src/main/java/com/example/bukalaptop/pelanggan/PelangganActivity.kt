package com.example.bukalaptop.pelanggan

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import com.example.bukalaptop.MainActivity
import com.example.bukalaptop.R
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class PelangganActivity : AppCompatActivity() {

    companion object {
        private var TAB_TITLES = arrayOf("Barang", "Checkout", "Riwayat")
    }

    private var sectionPagerPelangganAdapter: SectionPagerPelangganAdapter? = null
    private var viewPager: ViewPager2? = null
    private var tabLayoutMediator: TabLayoutMediator? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pelanggan)

        sectionPagerPelangganAdapter = SectionPagerPelangganAdapter(this)
        viewPager = findViewById(R.id.view_pager)
        viewPager?.adapter = sectionPagerPelangganAdapter
        val tabs: TabLayout = findViewById(R.id.tabs)
        tabLayoutMediator = TabLayoutMediator(tabs, viewPager!!) { tab, position ->
            tab.text = TAB_TITLES[position]
        }
        tabLayoutMediator?.attach()

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

    override fun onDestroy() {
        viewPager?.adapter = null
        tabLayoutMediator?.detach()
        sectionPagerPelangganAdapter = null
        viewPager = null
        tabLayoutMediator = null
        super.onDestroy()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_pelanggan, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.act_signOut -> {
                Firebase.auth.signOut()
                Intent(this, MainActivity::class.java).also { intent ->
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }
}