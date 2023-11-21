package com.example.bukalaptop.pegawai.pesanan

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.bukalaptop.R
import com.example.bukalaptop.pegawai.pesanan.model.Pelanggan
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class ProfilPelangganFragment : Fragment() {

    private lateinit var ivKtpPelanggan: ImageView
    private lateinit var tvNamaLengkap: TextView
    private lateinit var tvUsername: TextView
    private lateinit var tvEmail: TextView
    private lateinit var tvNomorTelpon: TextView
    private lateinit var tvAlamatAsal: TextView

    companion object {
        var EXTRA_IDPELANGGAN = "extra_idpelanggan"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profil_pelanggan, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ivKtpPelanggan = view.findViewById(R.id.iv_ktp_pelanggan)
        tvNamaLengkap = view.findViewById(R.id.tv_nama_lengkap)
        tvUsername = view.findViewById(R.id.tv_username)
        tvEmail = view.findViewById(R.id.tv_email)
        tvNomorTelpon = view.findViewById(R.id.tv_nomor_telpon)
        tvAlamatAsal = view.findViewById(R.id.tv_alamat_asal)

        val db = Firebase.firestore

        if (arguments != null) {
            val pelangganId = arguments?.getString(EXTRA_IDPELANGGAN).toString()

            db.collection("pelanggan").addSnapshotListener { value, error ->
                if (error != null) {
                    Log.d("List Pesanan Error", error.toString())
                    return@addSnapshotListener
                }
                if (value != null) {
                    for (document in value) {
                        if (document.id == pelangganId) {
                            val pelanggan = document.toObject(Pelanggan::class.java)
                            Glide.with(requireContext())
                                .load(pelanggan.fotoKtp)
                                .apply(RequestOptions())
                                .into(ivKtpPelanggan)
                            tvNamaLengkap.text = pelanggan.namaLengkap
                            tvUsername.text = pelanggan.username
                            tvEmail.text = pelanggan.email
                            tvNomorTelpon.text = pelanggan.nomorTelepon
                            tvAlamatAsal.text = pelanggan.alamatAsal
                        }
                    }
                }
            }
        }

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                parentFragmentManager.popBackStack()
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }
}