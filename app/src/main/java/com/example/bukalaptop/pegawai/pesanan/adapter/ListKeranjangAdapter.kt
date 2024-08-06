package com.example.bukalaptop.pegawai.pesanan.adapter

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.bukalaptop.R
import com.example.bukalaptop.model.Keranjang
import com.example.bukalaptop.pegawai.barang.DetailBarangFragment
import com.example.bukalaptop.pelanggan.barang.DetailBarangPelangganFragment
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.NumberFormat
import java.util.Currency

class ListKeranjangAdapter(
    private val listKeranjang: ArrayList<Keranjang>,
    private val isPegawai: Boolean,
    private val pelangganId: String,
    private val vsblHapus: Boolean
) :
    RecyclerView.Adapter<ListKeranjangAdapter.ListViewHolder>() {
    fun setData(data: List<Keranjang>) {
        listKeranjang.clear()
        listKeranjang.addAll(data)
        notifyDataSetChanged()
    }

    class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivBarang: ImageView = itemView.findViewById(R.id.iv_tambah_barang)
        val tvMerek: TextView = itemView.findViewById(R.id.et_merek)
        val tvModel: TextView = itemView.findViewById(R.id.tv_model)
        val tvProsesor: TextView = itemView.findViewById(R.id.tv_prosesor)
        val tvBiayaSewa: TextView = itemView.findViewById(R.id.tv_biaya_sewa)
        val tvJumlah: TextView = itemView.findViewById(R.id.tv_jumlah)
        val tvSubtotal: TextView = itemView.findViewById(R.id.tv_subtotal)
        val btnHapus: ImageButton = itemView.findViewById(R.id.btn_hapus)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ListViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.list_item_keranjang, parent, false)
        return ListViewHolder(view)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val currencyFormat = NumberFormat.getCurrencyInstance()
        currencyFormat.maximumFractionDigits = 2
        currencyFormat.currency = Currency.getInstance("IDR")
        val (barang, jumlah) = listKeranjang[position]
        val db = Firebase.firestore

        holder.apply {
            if (vsblHapus) {
                btnHapus.visibility = View.VISIBLE
            } else {
                btnHapus.visibility = View.GONE
            }
            Glide.with(itemView.context)
                .load(barang.fotoBarang)
                .apply(RequestOptions())
                .into(ivBarang)
            tvMerek.text = barang.merek
            tvModel.text = barang.model
            tvProsesor.text = barang.prosesor
            tvBiayaSewa.text = "${currencyFormat.format(barang.biayaSewa)}/Hari"
            tvJumlah.text = "$jumlah Unit"
            tvSubtotal.text = currencyFormat.format(barang.biayaSewa * jumlah)
            btnHapus.setOnClickListener {
                val builder = AlertDialog.Builder(itemView.context)

                builder.setMessage(
                    HtmlCompat.fromHtml(
                        "Anda yakin ingin menghapus <b>${barang.merek}</b> <b>${barang.model}</b> dari Keranjang Anda?",
                        HtmlCompat.FROM_HTML_MODE_LEGACY
                    )
                )
                    .setTitle("Konfirmasi")

                builder.setPositiveButton("Ya") { dialog, which ->
                    if (listKeranjang.size <= 1) {
                        listKeranjang.clear()
                        notifyItemChanged(position)
                    }
                    barang.let { mBarang ->
                        db.collection("pengguna").addSnapshotListener { value, error ->
                            if (error != null) {
                                Toast.makeText(
                                    holder.itemView.context,
                                    "$error",
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                                return@addSnapshotListener
                            }
                            if (value != null) {
                                for (document in value) {
                                    if (document.getString("id") == pelangganId) {
                                        CoroutineScope(Dispatchers.Main).launch {
                                            try {
                                                document.reference.collection("keranjang")
                                                    .document(mBarang.barangId).delete().await()

                                                Toast.makeText(
                                                    itemView.context,
                                                    "${mBarang.merek} ${mBarang.model} berhasil dihapus",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }catch (e: Exception){
                                                Toast.makeText(
                                                    itemView.context,
                                                    "$e",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                builder.setNegativeButton("Tidak") { dialog, which ->
                    dialog.cancel()
                }

                val dialog = builder.create()
                dialog.show()
            }

            itemView.setOnClickListener {
                val detailBarangFragment = DetailBarangFragment()
                val detailBarangPelangganFragment = DetailBarangPelangganFragment()
                val mFragmentManager =
                    (holder.itemView.context as AppCompatActivity).supportFragmentManager
                val bundle = Bundle()
                bundle.putString(DetailBarangFragment.EXTRA_IDBARANG, barang.barangId)
                detailBarangFragment.arguments = bundle
                detailBarangPelangganFragment.arguments = bundle

                mFragmentManager.beginTransaction().apply {
                    if (isPegawai) {
                        replace(
                            R.id.fragment_pegawai_container,
                            detailBarangFragment,
                            DetailBarangFragment::class.java.simpleName
                        )
                    } else {
                        replace(
                            R.id.fragment_pelanggan_container,
                            detailBarangPelangganFragment,
                            DetailBarangPelangganFragment::class.java.simpleName
                        )
                        addToBackStack(null)
                        commit()
                    }
                }
            }
        }
    }

    override fun getItemCount(): Int = listKeranjang.size
}