package com.example.bukalaptop.pelanggan

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.bukalaptop.pelanggan.barang.BarangPelangganFragment
import com.example.bukalaptop.pelanggan.checkout.CheckoutFragment
import com.example.bukalaptop.pelanggan.riwayat.RiwayatFragment

class SectionPagerPelangganAdapter(activity: AppCompatActivity) : FragmentStateAdapter(activity) {
    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {
        var fragment: Fragment? = null
        when (position) {
            0 -> fragment = BarangPelangganFragment()
            1 -> fragment = CheckoutFragment()
            2 -> fragment = RiwayatFragment()
        }
        return fragment as Fragment
    }

}