package com.example.bukalaptop.pegawai.barang

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.bukalaptop.R
import com.example.bukalaptop.pegawai.pesanan.DetailPesananFragment
import com.example.bukalaptop.pegawai.pesanan.KeranjangFragment
import com.example.bukalaptop.pegawai.pesanan.MapsFragment
import com.example.bukalaptop.pegawai.pesanan.Pesanan

class DetailBarangFragment : Fragment() {

    private lateinit var ivBarang: ImageView
    private lateinit var tvMerekModel: TextView
    private lateinit var tvBiayaSewa: TextView
    private lateinit var tvProsesor: TextView
    private lateinit var tvRam: TextView
    private lateinit var tvOs: TextView
    private lateinit var tvGrafik: TextView
    private lateinit var tvPenyimpanan: TextView
    private lateinit var tvStok: TextView
    private lateinit var btnUpdate: Button

    companion object {
        var EXTRA_BARANG = "extra_barang"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_detail_barang, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ivBarang=view.findViewById(R.id.iv_barang)
        tvMerekModel = view.findViewById(R.id.tv_merekModel)
        tvBiayaSewa = view.findViewById(R.id.tv_biayaSewa)
        tvProsesor = view.findViewById(R.id.tv_prosesor)
        tvRam = view.findViewById(R.id.tv_ram)
        tvOs=view.findViewById(R.id.tv_os)
        tvGrafik=view.findViewById(R.id.tv_grafik)
        tvPenyimpanan=view.findViewById(R.id.tv_penyimpanan)
        tvStok=view.findViewById(R.id.tv_stok)
        btnUpdate=view.findViewById(R.id.btn_update)

        if (arguments != null) {
            val barang = arguments?.getParcelable<Barang>(EXTRA_BARANG)
            Glide.with(requireContext())
                .load(barang?.fotoBarang)
                .apply(RequestOptions())
                .into(ivBarang)
            tvMerekModel.text = "${barang?.merek} ${barang?.model}"
            tvProsesor.text = barang?.prosesor
            tvRam.text = barang?.ram
            tvOs.text = barang?.sistemOperasi
            tvGrafik.text = barang?.kartuGrafis
            tvPenyimpanan.text = barang?.penyimpanan

            val mapsFragment = MapsFragment()

            val bundle = Bundle()
            if (barang != null) {
                bundle.putParcelable(MapsFragment.EXTRA_PESANAN, barang)
            }
            mapsFragment.arguments = bundle

            val mFragmentManager = parentFragmentManager
            mFragmentManager.beginTransaction().apply {
                replace(R.id.maps_container, mapsFragment, MapsFragment::class.java.simpleName)
                commit()
            }

            val keranjangFragment = KeranjangFragment()

            if (barang != null) {
                bundle.putParcelable(KeranjangFragment.EXTRA_PESANAN, barang)
            }
            keranjangFragment.arguments = bundle

            mFragmentManager.beginTransaction().apply {
                replace(R.id.keranjangFragmentContainer, keranjangFragment, KeranjangFragment::class.java.simpleName)
                commit()
            }
        }
    }
}