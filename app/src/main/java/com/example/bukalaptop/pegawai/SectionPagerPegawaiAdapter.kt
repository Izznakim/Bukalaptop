package com.example.bukalaptop.pegawai

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.bukalaptop.pegawai.barang.BarangFragment
import com.example.bukalaptop.pegawai.pesanan.PesananFragment

class SectionPagerPegawaiAdapter(activity: AppCompatActivity) : FragmentStateAdapter(activity) {
    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        var fragment: Fragment? = null
        when (position) {
            0 -> fragment = PesananFragment()
            1 -> fragment = BarangFragment()
        }
        return fragment as Fragment
    }

}