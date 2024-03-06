package com.example.bukalaptop.pegawai.pesanan

import android.app.AlertDialog
import android.content.Intent
import android.opengl.Visibility
import android.os.Bundle
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.cardview.widget.CardView
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.bukalaptop.R
import com.example.bukalaptop.ZoomImageActivity
import com.example.bukalaptop.pegawai.barang.model.Barang
import com.example.bukalaptop.pegawai.pesanan.ProfilPelangganFragment.Companion.EXTRA_IDPELANGGAN
import com.example.bukalaptop.pegawai.pesanan.adapter.ListKeranjangAdapter
import com.example.bukalaptop.model.Keranjang
import com.example.bukalaptop.model.Pelanggan
import com.example.bukalaptop.model.Pesanan
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Currency
import java.util.Date
import java.util.Locale

class DetailPesananFragment : Fragment() {

    private lateinit var cvPelangganProfil: CardView
    private lateinit var tvNama: TextView
    private lateinit var tvEmail: TextView
    private lateinit var tvTglPengiriman: TextView
    private lateinit var tvTglPengambilan: TextView
    private lateinit var tvHari: TextView
    private lateinit var tvTotal: TextView
    private lateinit var ivBukti: ImageView
    private lateinit var btnTerima: Button
    private lateinit var btnTolak: Button
    private lateinit var btnDikembalikan: Button
    private lateinit var rvKeranjang: RecyclerView
    private lateinit var listKeranjangAdapter: ListKeranjangAdapter
    private lateinit var listKeranjang: ArrayList<Keranjang>
    private lateinit var pesanan: Pesanan
    private lateinit var pelanggan: Pelanggan

    private var pesananListenerReg: ListenerRegistration? = null
    private var penggunaListenerReg: ListenerRegistration? = null

    companion object {
        var EXTRA_IDPESANAN = "extra_idpesanan"
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
        cvPelangganProfil = view.findViewById(R.id.cv_pelanggan_profil)
        tvNama = view.findViewById(R.id.tv_namaLengkap)
        tvEmail = view.findViewById(R.id.tv_email)
        tvTglPengiriman = view.findViewById(R.id.tv_tglpengiriman)
        tvTglPengambilan = view.findViewById(R.id.tv_tglpengambilan)
        tvHari = view.findViewById(R.id.tv_hari)
        tvTotal = view.findViewById(R.id.tv_total)
        btnTolak = view.findViewById(R.id.btn_tolak)
        btnTerima = view.findViewById(R.id.btn_terima)
        btnDikembalikan = view.findViewById(R.id.btn_dikembalikan)
        ivBukti = view.findViewById(R.id.iv_bukti)

        cvPelangganProfil.setOnClickListener {
            val profilPelangganFragment = ProfilPelangganFragment()
            val mFragmentManager = activity?.supportFragmentManager
            val bundle = Bundle()

            bundle.putString(EXTRA_IDPELANGGAN, pesanan.idPelanggan)
            profilPelangganFragment.arguments = bundle
            mFragmentManager?.beginTransaction()?.apply {
                replace(
                    R.id.fragment_pegawai_container,
                    profilPelangganFragment,
                    ProfilPelangganFragment::class.java.simpleName
                )
                addToBackStack(null)
                commit()
            }
        }

        rvKeranjang = view.findViewById(R.id.rv_keranjang)
        rvKeranjang.setHasFixedSize(true)

        initAdapter()

        var pesananId = ""

        val db = Firebase.firestore
        listKeranjang = arrayListOf()

        if (arguments != null) {
            pesananId = arguments?.getString(EXTRA_IDPESANAN).toString()

            pesananListenerReg =
                db.collection("pesanan").addSnapshotListener { valuePesanan, errorPesanan ->
                    if (errorPesanan != null) {
                        Log.d("List Pesanan Error", errorPesanan.toString())
                        return@addSnapshotListener
                    }
                    if (valuePesanan != null) {
                        for (document in valuePesanan) {
                            if (document.id == pesananId) {
                                pesanan = Pesanan()
                                pesanan.id = document.id
                                pesanan.idPelanggan = document.getString("idPelanggan").toString()
                                pesanan.buktiBayar = document.getString("buktiBayar").toString()
                                pesanan.tglPengiriman =
                                    document.getTimestamp("tglPengiriman")?.toDate()
                                pesanan.tglPengambilan =
                                    document.getTimestamp("tglPengambilan")?.toDate()
                                pesanan.status = document.getString("status").toString()
                            }
                        }

                        val currencyFormat = NumberFormat.getCurrencyInstance()
                        currencyFormat.maximumFractionDigits = 2
                        currencyFormat.currency = Currency.getInstance("IDR")

                        val sdf = SimpleDateFormat("dd/MM/yyy", Locale.getDefault())
                        val diff =
                            (pesanan.tglPengambilan?.time ?: 0) - (pesanan.tglPengiriman?.time ?: 0)
                        val masaSewa = (diff / 1000 / 60 / 60 / 24).toInt()

                        penggunaListenerReg = db.collection("pengguna")
                            .addSnapshotListener { valuePelanggan, errorPelanggan ->
                                if (errorPelanggan != null) {
                                    Log.d("List Pesanan Error", errorPelanggan.toString())
                                    return@addSnapshotListener
                                }
                                if (valuePelanggan != null) {
                                    for (document in valuePelanggan) {
                                        if (document.id == pesanan.idPelanggan) {
                                            pelanggan = document.toObject(Pelanggan::class.java)
                                            tvNama.text = pelanggan.namaLengkap
                                            tvEmail.text = pelanggan.email
                                        }
                                    }
                                }
                            }

                        tvTglPengiriman.text = sdf.format(pesanan.tglPengiriman ?: Date())
                        tvTglPengambilan.text = sdf.format(pesanan.tglPengambilan ?: Date())
                        tvHari.text = masaSewa.toString()
                        if (pesanan.status == "diterima") {
                            btnTerima.visibility = View.GONE
                            btnTolak.visibility = View.GONE
                            btnDikembalikan.visibility = View.VISIBLE
                        } else if (pesanan.status == "netral") {
                            btnTerima.visibility = View.VISIBLE
                            btnTolak.visibility = View.VISIBLE
                            btnDikembalikan.visibility = View.GONE
                        }

                        Glide.with(requireContext())
                            .load(pesanan.buktiBayar)
                            .apply(RequestOptions())
                            .into(ivBukti)

                        pesananListenerReg =
                            db.collection("pesanan").document(pesananId).collection("keranjang")
                                .addSnapshotListener { valueKeranjang, errorKeranjang ->
                                    var total = 0
                                    listKeranjang.clear()
                                    if (valueKeranjang != null) {
                                        for (document in valueKeranjang) {
                                            val barang = document.toObject(Barang::class.java)
                                            val jumlah = document.get("jumlah").toString().toInt()
                                            total += (barang.biayaSewa * jumlah)

                                            val keranjang = Keranjang(barang, jumlah)
                                            keranjang.barang = document.toObject(Barang::class.java)
                                            keranjang.barang.barangId = document.id
                                            keranjang.jumlah = jumlah

                                            listKeranjang.add(keranjang)
                                        }
                                    } else if (errorKeranjang != null) {
                                        Log.d("List Keranjang", errorKeranjang.toString())
                                    }
                                    listKeranjangAdapter.setData(listKeranjang)
                                    tvTotal.text =
                                        currencyFormat.format(total * masaSewa)
                                }
                    } else {
                        Log.d("List Pesanan", "Data Kosong")
                    }
                }

            ivBukti.setOnClickListener {
                Intent(activity, ZoomImageActivity::class.java).also {
                    it.putExtra(ZoomImageActivity.EXTRA_IMAGE, pesanan.buktiBayar)
                    startActivity(it)
                }
            }
            btnTerima.setOnClickListener {
                db.collection("pesanan").document(pesananId).update("status", "diterima")
                    .addOnSuccessListener {
                        btnTerima.visibility = View.GONE
                        btnTolak.visibility = View.GONE
                        btnDikembalikan.visibility = View.VISIBLE
                        Toast.makeText(
                            requireContext(),
                            "Pesanan telah Anda setujui",
                            Toast.LENGTH_SHORT
                        ).show()
                    }.addOnFailureListener {
                        Toast.makeText(requireContext(), "$it", Toast.LENGTH_SHORT).show()
                    }
            }
            btnTolak.setOnClickListener {
                db.collection("pesanan").document(pesananId).update("status", "ditolak")
                    .addOnSuccessListener {
                        Toast.makeText(
                            requireContext(),
                            "Pesanan telah Anda tolak",
                            Toast.LENGTH_SHORT
                        ).show()
                        parentFragmentManager.popBackStack()
                    }.addOnFailureListener {
                        Toast.makeText(requireContext(), "$it", Toast.LENGTH_SHORT).show()
                    }
            }
            btnDikembalikan.setOnClickListener {
                val builder = AlertDialog.Builder(requireContext())

                builder.setMessage(
                    HtmlCompat.fromHtml(
                        "Apakah Anda yakin pelanggan dengan nama <b>${tvNama.text}</b> sudah mengembalikan barang yang disewa?",
                        HtmlCompat.FROM_HTML_MODE_LEGACY
                    )
                )
                    .setTitle("Konfirmasi")

                builder.setPositiveButton("Sudah") { dialog, which ->
                    val storageRef = FirebaseStorage.getInstance().reference
                    val pesananIdRef = storageRef.child("bukti/${pesananId}.jpg")

                    db.collection("pesanan").document(pesananId).get()
                        .addOnSuccessListener { snapshot ->
                            val subCollections = snapshot.reference.collection("keranjang")
                            subCollections.get().addOnSuccessListener { subSnapshot ->
                                for (doc in subSnapshot.documents) {
                                    doc.reference.delete()
                                }
                            }
                            subCollections.document().delete()
                            pesananIdRef.delete()
                            db.collection("pesanan").document(pesananId).delete()
                                .addOnSuccessListener {
                                    Toast.makeText(
                                        requireContext(),
                                        "Pesanan berhasil dihapus",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    parentFragmentManager.popBackStack()
                                }.addOnFailureListener { e ->
                                    Toast.makeText(
                                        requireContext(),
                                        e.toString(),
                                        Toast.LENGTH_SHORT
                                    )
                                        .show()
                                }
                        }
                }

                builder.setNegativeButton("Belum") { dialog, which ->
                    dialog.cancel()
                }

                val dialog = builder.create()
                dialog.show()
            }
        }
    }

    private fun initAdapter() {
        rvKeranjang.layoutManager = LinearLayoutManager(activity)
        listKeranjangAdapter = ListKeranjangAdapter(arrayListOf())
        rvKeranjang.adapter = listKeranjangAdapter
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

    override fun onStop() {
        super.onStop()

        pesananListenerReg?.remove()
        penggunaListenerReg?.remove()
    }
}