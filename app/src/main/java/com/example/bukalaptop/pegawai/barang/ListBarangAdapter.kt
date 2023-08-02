package com.example.bukalaptop.pegawai.barang

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.bukalaptop.R
import com.example.bukalaptop.pegawai.pesanan.DetailPesananFragment
import com.example.bukalaptop.pegawai.pesanan.ListPesananAdapter
import com.example.bukalaptop.pegawai.pesanan.Pesanan
import java.awt.font.TextAttribute

class ListBarangAdapter(private val listBarang: ArrayList<Barang>) :
    RecyclerView.Adapter<ListBarangAdapter.ListViewHolder>() {
    fun setData(data: List<Barang>) {
        listBarang.clear()
        listBarang.addAll(data)
        notifyDataSetChanged()
    }

    class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivBarang: ImageView = itemView.findViewById(R.id.iv_barang)
        val tvMerek: TextView = itemView.findViewById(R.id.tv_merek)
        val tvModel: TextView = itemView.findViewById(R.id.tv_model)
        val tvPeyimpanan: TextView = itemView.findViewById(R.id.tv_penyimpanan)
        val tvProsesor: TextView = itemView.findViewById(R.id.tv_prosesor)
        val tvRam: TextView = itemView.findViewById(R.id.tv_ram)
        val tvBiayaSewa: TextView = itemView.findViewById(R.id.tv_biayaSewa)
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
        val (_, fotoBarang, merek, model, prosesor, _, ram, penyimpanan, _, _, _, _, _, biayaSewa) = listBarang[position]
        holder.apply {
            Glide.with(holder.itemView.context)
                .load(fotoBarang)
                .apply(RequestOptions())
                .into(ivBarang)
            tvMerek.text = merek
            tvModel.text = model
            tvPeyimpanan.text = penyimpanan
            tvProsesor.text = prosesor
            tvRam.text = ram
            tvBiayaSewa.text = biayaSewa.toString()
            itemView.setOnClickListener {
                Toast.makeText(holder.itemView.context, "Coming soon", Toast.LENGTH_SHORT).show()
//                val detailPesananFragment = DetailPesananFragment()
//                val mFragmentManager =
//                    (holder.itemView.context as AppCompatActivity).supportFragmentManager
//                val bundle = Bundle()
//
//                bundle.putParcelable(DetailPesananFragment.EXTRA_PESANAN, listPesanan[position])
//                detailPesananFragment.arguments = bundle
//                mFragmentManager.beginTransaction().apply {
//                    replace(R.id.fragment_container,detailPesananFragment, DetailPesananFragment::class.java.simpleName)
//                    addToBackStack(null)
//                    commit()
//                }
            }
        }
    }

    override fun getItemCount(): Int = listBarang.size
}