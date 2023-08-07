package com.example.bukalaptop.pegawai

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
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
        val tvNama: TextView = itemView.findViewById(R.id.tv_nama)
        val tvTelepon: TextView = itemView.findViewById(R.id.tv_nomor)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ListViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.list_item_pesanan, parent, false)
        return ListViewHolder(view)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val (_,nama, _, telepon) = listPesanan[position]
        holder.apply {
            tvNama.text = nama
            tvTelepon.text = telepon
            itemView.setOnClickListener {
                Toast.makeText(holder.itemView.context, "Coming soon", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun getItemCount(): Int = listPesanan.size
}