package com.example.bukalaptop.pelanggan.checkout.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.bukalaptop.databinding.ListItemKeranjangBinding
import com.example.bukalaptop.model.Keranjang
import com.example.bukalaptop.pegawai.barang.model.Barang
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.NumberFormat
import java.util.Currency

class ListBarangCheckoutAdapter(
    val listBarangKeranjang: ArrayList<Keranjang>,
    private val onItemClickListener: OnItemClickListener
) :
    RecyclerView.Adapter<ListBarangCheckoutAdapter.ListViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(barang: Barang)
        fun onDeleteClick(position: Int, barang: Barang)
    }

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
                onItemClickListener.onDeleteClick(position, barang)
            }

            holder.itemView.setOnClickListener {
                onItemClickListener.onItemClick(barang)
            }
        }
    }

    override fun getItemCount(): Int = listBarangKeranjang.size
}