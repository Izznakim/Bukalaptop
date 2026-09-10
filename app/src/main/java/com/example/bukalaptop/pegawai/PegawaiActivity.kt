package com.example.bukalaptop.pegawai

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.toDrawable
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.example.bukalaptop.MainActivity
import com.example.bukalaptop.R
import com.example.bukalaptop.databinding.ActivityPegawaiBinding
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.Firebase
import com.google.firebase.auth.auth


class PegawaiActivity : AppCompatActivity() {

    companion object {
        private val TAB_TITLES = arrayListOf("Pesanan", "Barang")
    }

    private lateinit var binding: ActivityPegawaiBinding
    private var sectionPagerPegawaiAdapter: SectionPagerPegawaiAdapter? = null
    private var tabLayoutMediator: TabLayoutMediator? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPegawaiBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val topInset = insets.getInsets(
                WindowInsetsCompat.Type.statusBars()
            ).top

            view.updatePadding(
                top = topInset
            )

            insets
        }

        sectionPagerPegawaiAdapter = SectionPagerPegawaiAdapter(this)
        binding.viewPager.adapter = sectionPagerPegawaiAdapter
        tabLayoutMediator = TabLayoutMediator(binding.tabs, binding.viewPager) { tab, position ->
            tab.text = TAB_TITLES[position]
        }
        tabLayoutMediator?.attach()

        val text: Spannable = SpannableString("Halaman Pegawai")
        text.setSpan(
            ForegroundColorSpan(Color.RED),
            0,
            text.length,
            Spannable.SPAN_INCLUSIVE_INCLUSIVE
        )
        supportActionBar?.title = text
        supportActionBar?.elevation = 0f
        supportActionBar?.setBackgroundDrawable(Color.BLACK.toDrawable())
    }

    override fun onDestroy() {
        binding.viewPager.adapter = null
        tabLayoutMediator?.detach()
        sectionPagerPegawaiAdapter = null
        tabLayoutMediator = null
        super.onDestroy()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_pegawai, menu)
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