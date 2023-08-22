package com.example.bukalaptop.pegawai.pesanan.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.bukalaptop.R
import com.example.bukalaptop.pegawai.pesanan.model.Keranjang
import java.text.NumberFormat
import java.util.Currency

class ListKeranjangAdapter(private val listKeranjang: ArrayList<Keranjang>) :
    RecyclerView.Adapter<ListKeranjangAdapter.ListViewHolder>() {
    fun setData(data: List<Keranjang>) {
        listKeranjang.clear()
        listKeranjang.addAll(data)
        notifyDataSetChanged()
    }

    class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivBarang: ImageView = itemView.findViewById(R.id.iv_barang)
        val tvMerek: TextView = itemView.findViewById(R.id.tv_merek_model)
        val tvModel: TextView = itemView.findViewById(R.id.tv_model)
        val tvProsesor: TextView = itemView.findViewById(R.id.tv_prosesor)
        val tvBiayaSewa: TextView = itemView.findViewById(R.id.tv_biayaSewa)
        val tvJumlah: TextView = itemView.findViewById(R.id.tv_jumlah)
        val tvSubtotal: TextView = itemView.findViewById(R.id.tv_subtotal)
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
        val (fotoBarang, merek, model, prosesor, biayaSewa, jumlah) = listKeranjang[position]

        holder.apply {
            Glide.with(itemView.context)
                .load(fotoBarang)
                .apply(RequestOptions())
                .into(ivBarang)
            tvMerek.text = merek
            tvModel.text = model
            tvProsesor.text = prosesor
            tvBiayaSewa.text = "${currencyFormat.format(biayaSewa)}/Unit"
            tvJumlah.text = "$jumlah Unit"
            tvSubtotal.text = currencyFormat.format(biayaSewa * jumlah)
            itemView.setOnClickListener {
                Toast.makeText(
                    itemView.context,
                    "Coming Soon ke halaman detail barang",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun getItemCount(): Int = listKeranjang.size
}