package com.example.bukalaptop.pelanggan.barang.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.bukalaptop.R
import com.example.bukalaptop.model.Alamat

class ListAlamatAdapter(private val listAlamat: ArrayList<Alamat>) :
    RecyclerView.Adapter<ListAlamatAdapter.ListViewHolder>() {
    fun setData(data: List<Alamat>) {
        listAlamat.clear()
        listAlamat.addAll(data)
        notifyDataSetChanged()
    }

    class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvAtasNama: TextView = itemView.findViewById(R.id.tv_atas_nama)
        val tvNomorPengiriman: TextView = itemView.findViewById(R.id.tv_nomor_pengiriman)
        val tvAlamatLengkapPengiriman: TextView =
            itemView.findViewById(R.id.tv_alamat_lengkap_pengiriman)
        val tvAlamatSingkatPengiriman: TextView =
            itemView.findViewById(R.id.tv_alamat_singkat_pengiriman)
        val tvUbah: TextView = itemView.findViewById(R.id.tv_ubah)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ListViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.list_item_alamat, parent, false)
        return ListViewHolder(view)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val (atasNama, nomorPengiriman, alamatLengkapPengiriman, alamatSingkatPengiriman) = listAlamat[position]
        holder.apply {
            tvAtasNama.text=atasNama
            tvNomorPengiriman.text=nomorPengiriman
            tvAlamatLengkapPengiriman.text=alamatLengkapPengiriman
            tvAlamatSingkatPengiriman.text=alamatSingkatPengiriman

            tvUbah.setOnClickListener {  }
            itemView.setOnClickListener {  }
        }
    }

    override fun getItemCount(): Int = listAlamat.size
}