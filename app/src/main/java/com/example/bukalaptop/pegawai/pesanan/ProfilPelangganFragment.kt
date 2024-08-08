package com.example.bukalaptop.pegawai.pesanan

import android.content.Intent
import android.graphics.Paint
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.bukalaptop.R
import com.example.bukalaptop.ZoomImageActivity
import com.example.bukalaptop.model.Pelanggan
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class ProfilPelangganFragment : Fragment() {

    private lateinit var ivKtpPelanggan: ImageView
    private lateinit var tvNamaLengkap: TextView
    private lateinit var tvNomorKtp: TextView
    private lateinit var tvUsername: TextView
    private lateinit var tvEmail: TextView
    private lateinit var tvNomorTelpon: TextView
    private lateinit var tvProgress: TextView
    private lateinit var builder: AlertDialog.Builder
    private lateinit var progressDialog: AlertDialog

    private var listener: ListenerRegistration? = null

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
        tvNomorKtp = view.findViewById(R.id.tv_nomor_ktp)
        tvUsername = view.findViewById(R.id.tv_username)
        tvEmail = view.findViewById(R.id.tv_email)
        tvNomorTelpon = view.findViewById(R.id.tv_nomor_telpon)

        builder = AlertDialog.Builder(requireContext())
        val inflater = layoutInflater
        val dialogView = inflater.inflate(R.layout.progress_layout, null)
        builder.setView(dialogView)
        builder.setCancelable(false)
        progressDialog = builder.create()

        tvProgress = dialogView.findViewById(R.id.tv_progress)

        tvNomorTelpon.paintFlags = tvNomorTelpon.paintFlags or Paint.UNDERLINE_TEXT_FLAG

        val db = Firebase.firestore
        var pelanggan = Pelanggan()

        if (arguments != null) {
            val pelangganId = arguments?.getString(EXTRA_IDPELANGGAN).toString()
            tvProgress.text = "Memuat profil..."
            progressDialog.show()

            listener = db.collection("pengguna").addSnapshotListener { value, error ->
                if (error != null) {
                    Toast.makeText(requireContext(), "$error", Toast.LENGTH_SHORT).show()
                    progressDialog.dismiss()
                    return@addSnapshotListener
                }
                if (value != null) {
                    for (document in value) {
                        if (document.getString("id") == pelangganId) {
                            pelanggan = document.toObject(Pelanggan::class.java)
                            Glide.with(requireContext())
                                .load(pelanggan.fotoKtp)
                                .apply(RequestOptions())
                                .into(ivKtpPelanggan)
                            tvNamaLengkap.text = pelanggan.namaLengkap
                            tvNomorKtp.text = pelanggan.nomorKtp
                            tvUsername.text = pelanggan.username
                            tvEmail.text = pelanggan.email
                            tvNomorTelpon.text = pelanggan.nomorHp
                        }
                    }
                }
                progressDialog.dismiss()
            }
        }

        ivKtpPelanggan.setOnClickListener {
            Intent(activity, ZoomImageActivity::class.java).also {
                it.putExtra(ZoomImageActivity.EXTRA_IMAGE, pelanggan.fotoKtp)
                startActivity(it)
            }
        }

        tvNomorTelpon.setOnClickListener {
            intentToWhatsApp(tvNomorTelpon.text.toString())
        }

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                parentFragmentManager.popBackStack()
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }

    override fun onResume() {
        super.onResume()

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                parentFragmentManager.popBackStack()
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        listener?.remove()
        progressDialog.dismiss()
    }

    private fun intentToWhatsApp(phone: String) {
        val packageManager = context?.packageManager
        val intent = Intent(Intent.ACTION_VIEW)

        if (packageManager != null) {
            try {
                val url = "https://api.whatsapp.com/send?phone=$phone"
                intent.setPackage("com.whatsapp")
                intent.data = Uri.parse(url)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    context?.startActivity(intent)
                } else {
                    if (intent.resolveActivity(packageManager) != null) {
                        context?.startActivity(intent)
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "$e", Toast.LENGTH_SHORT).show()
            }
        }
    }
}