package com.example.bukalaptop.pegawai.pesanan

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.bukalaptop.R

class ListKeranjangAdapter(private val listKeranjang: ArrayList<Keranjang>) :
    RecyclerView.Adapter<ListKeranjangAdapter.ListViewHolder>() {
    fun setData(data:List<Keranjang>){
        listKeranjang.clear()
        listKeranjang.addAll(data)
        notifyDataSetChanged()
    }

    class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvMerek:TextView=itemView.findViewById(R.id.tv_merekModel)
        val tvModel:TextView=itemView.findViewById(R.id.tv_biayaSewa)
        val tvJumlah:TextView=itemView.findViewById(R.id.tv_jumlah)
        val tvSubtotal:TextView=itemView.findViewById(R.id.tv_subtotal)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ListViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.item_keranjang, parent, false)
        return ListViewHolder(view)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val (merek,model,jumlah,subtotal) = listKeranjang[position]
        holder.apply {
            tvMerek.text=merek
            tvModel.text=model
            tvJumlah.text=jumlah.toString()
            tvSubtotal.text=subtotal.toString()
        }
    }

    override fun getItemCount(): Int = listKeranjang.size
}