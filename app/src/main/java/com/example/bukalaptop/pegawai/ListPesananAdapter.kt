package com.example.bukalaptop.pegawai

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.bukalaptop.R

class ListPesananAdapter(private val listPesanan: ArrayList<Pesanan>) :
    RecyclerView.Adapter<ListPesananAdapter.ListViewHolder>() {
    fun setData(data:List<Pesanan>){
        listPesanan.clear()
        listPesanan.addAll(data)
        notifyDataSetChanged()
    }

    class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvNama: TextView = itemView.findViewById(R.id.tv_namaPelanggan)
        val tvTelepon: TextView = itemView.findViewById(R.id.tv_nomorTelepon)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ListViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.item_barang, parent, false)
        return ListViewHolder(view)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val (_,nama, _, telepon) = listPesanan[position]
        holder.tvNama.text = nama
        holder.tvTelepon.text = telepon
    }

    override fun getItemCount(): Int = listPesanan.size
}