package com.example.bukalaptop.pegawai.pesanan

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

class DetailPesananFragment : Fragment() {

    private lateinit var tvNama: TextView
    private lateinit var tvEmail: TextView
    private lateinit var tvAlamat: TextView
    private lateinit var tvNomorTelepon: TextView
    private lateinit var btnTerima: Button
    private lateinit var btnTolak: Button
    private lateinit var ivBukti:ImageView

    companion object {
        var EXTRA_PESANAN = "extra_pesanan"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_detail_pesanan, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tvNama = view.findViewById(R.id.tv_nama)
        tvEmail = view.findViewById(R.id.tv_email)
        tvAlamat = view.findViewById(R.id.tv_alamat)
        tvNomorTelepon = view.findViewById(R.id.tv_nomorTelepon)
        btnTolak=view.findViewById(R.id.btn_tolak)
        btnTerima=view.findViewById(R.id.btn_terima)
        ivBukti=view.findViewById(R.id.iv_bukti)

        if (arguments != null) {
            val pesanan = arguments?.getParcelable<Pesanan>(EXTRA_PESANAN)
            tvNama.text = pesanan?.namaLengkap
            tvEmail.text = pesanan?.email
            tvNomorTelepon.text = pesanan?.nomorTelepon
            tvAlamat.text = pesanan?.alamatLengkap
            Glide.with(requireContext())
                .load(pesanan?.buktiBayar)
                .apply(RequestOptions())
                .into(ivBukti)

            val mapsFragment = MapsFragment()

            val bundle = Bundle()
            if (pesanan != null) {
                bundle.putParcelable(MapsFragment.EXTRA_PESANAN, pesanan)
            }
            mapsFragment.arguments = bundle

            val mFragmentManager = parentFragmentManager
            mFragmentManager.beginTransaction().apply {
                replace(R.id.maps_container, mapsFragment, MapsFragment::class.java.simpleName)
                commit()
            }

            val keranjangFragment = KeranjangFragment()

            if (pesanan != null) {
                bundle.putParcelable(KeranjangFragment.EXTRA_PESANAN, pesanan)
            }
            keranjangFragment.arguments = bundle

            mFragmentManager.beginTransaction().apply {
                replace(R.id.keranjangFragmentContainer, keranjangFragment, KeranjangFragment::class.java.simpleName)
                commit()
            }

            btnTerima.setOnClickListener {
                Toast.makeText(requireContext(), "Coming Soon", Toast.LENGTH_SHORT).show()
            }
            btnTolak.setOnClickListener {
                Toast.makeText(requireContext(), "Coming Soon", Toast.LENGTH_SHORT).show()
            }
        }
    }
}