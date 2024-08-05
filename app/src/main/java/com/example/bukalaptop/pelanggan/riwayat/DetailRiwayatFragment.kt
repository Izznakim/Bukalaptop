package com.example.bukalaptop.pelanggan.riwayat

import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.bukalaptop.R
import com.example.bukalaptop.ZoomImageActivity
import com.example.bukalaptop.model.Keranjang
import com.example.bukalaptop.model.Pesanan
import com.example.bukalaptop.pegawai.barang.model.Barang
import com.example.bukalaptop.pegawai.pesanan.adapter.ListKeranjangAdapter
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Currency
import java.util.Date
import java.util.Locale

class DetailRiwayatFragment : Fragment() {

    private lateinit var rvKeranjang: RecyclerView
    private lateinit var tvTglPengiriman: TextView
    private lateinit var tvHari: TextView
    private lateinit var tvTglPengambilan: TextView
    private lateinit var tvTotal: TextView
    private lateinit var tvAlamat: TextView
    private lateinit var ivBukti: ImageView
    private lateinit var tvValidasi: TextView
    private lateinit var etNomorWa: EditText
    private lateinit var btnHapus: Button
    private lateinit var listKeranjangAdapter: ListKeranjangAdapter
    private lateinit var listKeranjang: ArrayList<Keranjang>
    private lateinit var pesanan: Pesanan
    private lateinit var storageBuktiRef: StorageReference
    private lateinit var tvProgress: TextView
    private lateinit var builder: AlertDialog.Builder
    private lateinit var progressDialog: AlertDialog

    companion object {
        var EXTRA_IDPELANGGAN = "extra_idpelanggan"
        var EXTRA_IDPESANAN = "extra_idpesanan"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_detail_riwayat, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rvKeranjang = view.findViewById(R.id.rv_keranjang)
        tvTglPengiriman = view.findViewById(R.id.tv_tglpengiriman)
        tvHari = view.findViewById(R.id.tv_hari)
        tvTglPengambilan = view.findViewById(R.id.tv_tglpengambilan)
        tvTotal = view.findViewById(R.id.tv_total)
        tvAlamat = view.findViewById(R.id.tv_alamat)
        ivBukti = view.findViewById(R.id.iv_bukti)
        tvValidasi = view.findViewById(R.id.tv_validasi)
        etNomorWa = view.findViewById(R.id.et_nomorWa)
        btnHapus = view.findViewById(R.id.btn_hapus)

        builder = AlertDialog.Builder(requireContext())
        val inflater = layoutInflater
        val dialogView = inflater.inflate(R.layout.progress_layout, null)
        builder.setView(dialogView)
        builder.setCancelable(false)
        progressDialog = builder.create()

        tvProgress = dialogView.findViewById(R.id.tv_progress)

        tvAlamat.paintFlags = tvAlamat.paintFlags or Paint.UNDERLINE_TEXT_FLAG

        rvKeranjang.setHasFixedSize(true)

        initAdapter()

        val pelangganId: String
        val pesananId: String

        val db = Firebase.firestore
        storageBuktiRef = FirebaseStorage.getInstance().getReference("bukti/")
        listKeranjang = arrayListOf()

        if (arguments != null) {
            pelangganId = arguments?.getString(EXTRA_IDPELANGGAN).toString()
            pesananId = arguments?.getString(EXTRA_IDPESANAN).toString()
            tvProgress.text = "Memuat informasi riwayat..."
            progressDialog.show()

            db.collection("pesanan").addSnapshotListener { valueRiwayat, errorRiwayat ->
                if (errorRiwayat != null) {
                    Log.d("Detail Riwayat Error", errorRiwayat.toString())
                    return@addSnapshotListener
                }
                if (valueRiwayat != null) {
                    for (document in valueRiwayat) {
                        if (document.id == pesananId) {
                            pesanan = Pesanan()
                            pesanan.id = document.id
                            pesanan.idPelanggan = document.getString("idPelanggan").toString()
                            pesanan.buktiBayar = document.getString("buktiBayar").toString()
                            pesanan.tglPengiriman =
                                document.getTimestamp("tglPengiriman")?.toDate()
                            pesanan.tglPengambilan =
                                document.getTimestamp("tglPengambilan")?.toDate()
                            pesanan.status = document.getString("status")
                            pesanan.alamat = document.getString("alamat")
                            pesanan.latitute = document.getDouble("latitude")
                            pesanan.longitude = document.getDouble("longitude")

                            val currencyFormat = NumberFormat.getCurrencyInstance()
                            currencyFormat.maximumFractionDigits = 2
                            currencyFormat.currency = Currency.getInstance("IDR")

                            val sdf = SimpleDateFormat("dd/MM/yyy", Locale.getDefault())
                            val diff =
                                (pesanan.tglPengambilan?.time ?: 0) - (pesanan.tglPengiriman?.time
                                    ?: 0)
                            val masaSewa = (diff / 1000 / 60 / 60 / 24).toInt()

                            tvTglPengiriman.text = sdf.format(pesanan.tglPengiriman ?: Date())
                            tvTglPengambilan.text = sdf.format(pesanan.tglPengambilan ?: Date())
                            tvHari.text = masaSewa.toString()
                            tvAlamat.text = pesanan.alamat

                            tvAlamat.setOnClickListener {
                                val uri =
                                    Uri.parse("geo:${pesanan.latitute},${pesanan.longitude}?q=${pesanan.latitute},${pesanan.longitude}")
                                val intent = Intent(Intent.ACTION_VIEW, uri)
                                intent.setPackage("com.google.android.apps.maps")
                                if (intent.resolveActivity(requireActivity().packageManager) != null) {
                                    startActivity(intent)
                                } else {
                                    Toast.makeText(
                                        requireContext(),
                                        "Google Maps belum diinstall",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            }

                            if (isAdded) {
                                Glide.with(requireContext())
                                    .load(pesanan.buktiBayar)
                                    .apply(RequestOptions())
                                    .into(ivBukti)
                            }

                            ivBukti.setOnClickListener {
                                Intent(activity, ZoomImageActivity::class.java).also {
                                    it.putExtra(ZoomImageActivity.EXTRA_IMAGE, pesanan.buktiBayar)
                                    startActivity(it)
                                }
                            }

                            if (pesanan.status != null) {
                                when (pesanan.status) {
                                    "diterima" -> {
                                        tvValidasi.text =
                                            "Pesanan anda di terima. Kami akan mengirim barang yang sesuai.\nHubungi nomor di bawah ini untuk informasi lebih lanjut."
                                        tvValidasi.setTextColor(Color.parseColor("#43a047"))
                                        tvValidasi.typeface = Typeface.DEFAULT_BOLD
                                        etNomorWa.setOnClickListener {
                                            val phone = etNomorWa.text
                                            intentToWhatsApp(phone)
                                        }

                                        val tanggalSaatIni = Date()

                                        if (pesanan.tglPengambilan != null && tanggalSaatIni >= pesanan.tglPengambilan) {
                                            btnHapus.isEnabled = true
                                            btnHapus.text = "Barang sudah dikembalikan?"
                                        } else {
                                            btnHapus.isEnabled = false
                                            btnHapus.text =
                                                "Barang akan kami ambil pada tanggal ${tvTglPengambilan.text}"
                                        }

                                        btnHapus.setOnClickListener {

                                            val builder = AlertDialog.Builder(requireContext())

                                            builder.setMessage(
                                                HtmlCompat.fromHtml(
                                                    "Anda yakin sudah mengembalikan barang ini?",
                                                    HtmlCompat.FROM_HTML_MODE_LEGACY
                                                )
                                            )
                                                .setTitle("Konfirmasi")

                                            builder.setPositiveButton("Ya") { dialog, which ->
                                                db.collection("pesanan")
                                                    .document(pesananId)
                                                    .update("status", "dikembalikan")
                                            }

                                            builder.setNegativeButton("Tidak") { dialog, which ->
                                                dialog.cancel()
                                            }

                                            val dialog = builder.create()
                                            dialog.show()
                                        }
                                    }

                                    "ditolak" -> {
                                        tvValidasi.text =
                                            "MAAF, PESANAN ANDA TIDAK BISA KAMI PROSES KARENA TIDAK VALID.\nHubungi nomor di bawah ini untuk informasi lebih lanjut."
                                        tvValidasi.setTextColor(Color.parseColor("#FF0A0A"))
                                        tvValidasi.typeface = Typeface.DEFAULT_BOLD
                                        etNomorWa.setOnClickListener {
                                            val phone = etNomorWa.text
                                            intentToWhatsApp(phone)
                                        }
                                        btnHapus.text = "Hapus pesanan"
                                        HapusRiwayat(db, pesananId)
                                    }

                                    "netral" -> {
                                        tvValidasi.text =
                                            "MAAF, PESANAN ANDA BELUM KAMI KONFIRMASI. DIMOHON UNTUK MENUNGGU BEBERAPA SAAT LAGI.\nHubungi nomor di bawah ini untuk informasi lebih lanjut."
                                        etNomorWa.setOnClickListener {
                                            val phone = etNomorWa.text
                                            intentToWhatsApp(phone)
                                        }
                                        btnHapus.text = "Batalkan pemesanan"
                                        HapusRiwayat(db, pesananId)
                                    }

                                    "dikembalikan" -> {
                                        tvValidasi.text =
                                            "RIWAYAT PESANAN INI AKAN TERHAPUS OTOMATIS SETELAH KONFIRMASI PENGEMBALIAN BARANG DITERIMA PEGAWAI.\nHubungi nomor di bawah ini untuk informasi lebih lanjut."
                                        etNomorWa.setOnClickListener {
                                            val phone = etNomorWa.text
                                            intentToWhatsApp(phone)
                                        }

                                        btnHapus.visibility = View.INVISIBLE
                                    }
                                }
                            }

                            if (pesanan.idPelanggan == pelangganId) {
                                db.collection("pesanan").document(pesananId).collection("keranjang")
                                    .addSnapshotListener { valueKeranjang, errorKeranjang ->
                                        var total = 0
                                        listKeranjang.clear()
                                        if (errorKeranjang != null) {
                                            Log.d("List Keranjang", errorKeranjang.toString())
                                            return@addSnapshotListener
                                        }
                                        if (valueKeranjang != null) {
                                            for (docKeranjang in valueKeranjang) {
                                                val barang =
                                                    docKeranjang.toObject(Barang::class.java)
                                                val jumlah =
                                                    docKeranjang.get("jumlah").toString().toInt()
                                                total += (barang.biayaSewa * jumlah)

                                                val keranjang = Keranjang(barang, jumlah)
                                                keranjang.barang =
                                                    docKeranjang.toObject(Barang::class.java)
                                                keranjang.jumlah = jumlah

                                                listKeranjang.add(keranjang)
                                            }
                                        }
                                        listKeranjangAdapter.setData(listKeranjang)
                                        tvTotal.text =
                                            currencyFormat.format(total * masaSewa)
                                    }
                            }
                        }
                    }

                } else {
                    Log.d("List Riwayat", "Data Kosong")
                }
                progressDialog.dismiss()
            }
        }
    }

    private fun HapusRiwayat(db: FirebaseFirestore, pesananId: String) {
        btnHapus.setOnClickListener {
            tvProgress.text = "Sedang menghapus riwayat pesanan..."
            progressDialog.show()

            CoroutineScope(Dispatchers.Main).launch {
                try {
                    db.collection("pesanan").document(pesananId).delete().await()
                    val task = db.collection("pesanan").document(pesananId)
                        .collection("keranjang")
                        .get().await()
                    task.forEach {
                        val doc = db.collection("barang").document(it.id)
                            .get().await()

                        val stok =
                            doc.get("stok")
                                .toString().toInt()
                        val jumlah =
                            it.get("jumlah").toString()
                                .toInt()
                        db.collection("barang")
                            .document(it.id)
                            .update(
                                "stok",
                                stok + jumlah
                            ).await()
                        it.reference.delete().await()
                    }

                    storageBuktiRef.child("${pesananId}.jpg").delete().await()

                    parentFragmentManager.popBackStack()
                    Toast.makeText(
                        requireContext(),
                        "Berhasil dihapus",
                        Toast.LENGTH_SHORT
                    ).show()
                } catch (e: Exception) {
                    Toast.makeText(
                        requireContext(),
                        e.toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                } finally {
                    progressDialog.dismiss()
                }
            }
        }
    }

    private fun intentToWhatsApp(phone: Editable) {
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

    private fun initAdapter() {
        rvKeranjang.layoutManager = LinearLayoutManager(activity)
        listKeranjangAdapter = ListKeranjangAdapter(arrayListOf(), true, "", false)
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
}