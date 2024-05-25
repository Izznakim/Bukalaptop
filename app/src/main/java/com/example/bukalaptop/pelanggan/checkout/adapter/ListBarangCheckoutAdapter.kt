package com.example.bukalaptop.pelanggan.checkout.adapter

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.bukalaptop.R
import com.example.bukalaptop.databinding.ListItemKeranjangBinding
import com.example.bukalaptop.model.Keranjang
import com.example.bukalaptop.pegawai.barang.DetailBarangFragment
import com.example.bukalaptop.pelanggan.barang.DetailBarangPelangganFragment
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.NumberFormat
import java.util.Currency

class ListBarangCheckoutAdapter(
    private val listBarangKeranjang: ArrayList<Keranjang>,
    private val pelangganId: String
) :
    RecyclerView.Adapter<ListBarangCheckoutAdapter.ListViewHolder>() {
    fun setData(data: List<Keranjang>) {
        listBarangKeranjang.clear()
        listBarangKeranjang.addAll(data)
        notifyDataSetChanged()
    }
    class ListViewHolder(val binding: ListItemKeranjangBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ListViewHolder {
        val binding =
            ListItemKeranjangBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val currencyFormat = NumberFormat.getCurrencyInstance()
        currencyFormat.maximumFractionDigits = 2
        currencyFormat.currency = Currency.getInstance("IDR")
        val (barang, jumlah) = listBarangKeranjang[position]
        val db = Firebase.firestore

        holder.binding.apply {
            btnHapus.visibility = View.VISIBLE
            Glide.with(holder.itemView.context)
                .load(barang.fotoBarang)
                .apply(RequestOptions())
                .into(ivTambahBarang)
            tvMerek.text = barang.merek
            tvModel.text = barang.model
            tvProsesor.text = barang.prosesor
            tvBiayaSewa.text = "${currencyFormat.format(barang.biayaSewa)}/Unit"
            tvJumlah.text = "$jumlah Unit"
            tvSubtotal.text = currencyFormat.format(barang.biayaSewa * jumlah)
            btnHapus.setOnClickListener {
                val builder = AlertDialog.Builder(holder.itemView.context)

                builder.setMessage(
                    HtmlCompat.fromHtml(
                        "Anda yakin ingin menghapus <b>${barang.merek}</b> <b>${barang.model}</b> dari Keranjang Anda?",
                        HtmlCompat.FROM_HTML_MODE_LEGACY
                    )
                )
                    .setTitle("Konfirmasi")

                builder.setPositiveButton("Ya") { dialog, which ->
                    if (listBarangKeranjang.size <= 1) {
                        listBarangKeranjang.clear()
                        notifyDataSetChanged()
                    } else {
                        listBarangKeranjang.removeAt(position)
                        notifyItemRemoved(position)
                        notifyItemRangeChanged(position, listBarangKeranjang.size)
                    }
                    barang.let { mBarang ->
                        db.collection("pengguna").document(pelangganId).collection("keranjang")
                            .document(mBarang.barangId)
                            .delete()
                            .addOnSuccessListener {
                                Toast.makeText(
                                    holder.itemView.context,
                                    "${mBarang.merek} ${mBarang.model} berhasil dihapus",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            .addOnFailureListener { e ->
                                Log.w(
                                    "Error",
                                    "Error deleting document",
                                    e
                                )
                            }
                    }
                }

                builder.setNegativeButton("Tidak") { dialog, which ->
                    dialog.cancel()
                }

                val dialog = builder.create()
                dialog.show()
            }

            holder.itemView.setOnClickListener {
                val detailBarangPelangganFragment = DetailBarangPelangganFragment()
                val mFragmentManager =
                    (holder.itemView.context as AppCompatActivity).supportFragmentManager
                val bundle = Bundle()
                bundle.putString(DetailBarangFragment.EXTRA_IDBARANG, barang.barangId)
                detailBarangPelangganFragment.arguments = bundle

                mFragmentManager.beginTransaction().apply {
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

    override fun getItemCount(): Int = listBarangKeranjang.size
}