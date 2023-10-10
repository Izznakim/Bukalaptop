package com.example.bukalaptop.pegawai.barang.adapter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.bukalaptop.R
import com.example.bukalaptop.pegawai.barang.DetailBarangFragment
import com.example.bukalaptop.pegawai.barang.model.Barang
import java.text.NumberFormat
import java.util.Currency

class ListBarangAdapter(private val listBarang: ArrayList<Barang>) :
    RecyclerView.Adapter<ListBarangAdapter.ListViewHolder>() {
    fun setData(data: List<Barang>) {
        listBarang.clear()
        listBarang.addAll(data)
        notifyDataSetChanged()
    }

    class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivBarang: ImageView = itemView.findViewById(R.id.iv_tambah_barang)
        val tvMerek: TextView = itemView.findViewById(R.id.tv_merek)
        val tvStok: TextView = itemView.findViewById(R.id.tv_stok)
        val tvProsesor: TextView = itemView.findViewById(R.id.tv_prosesor)
        val tvRam: TextView = itemView.findViewById(R.id.tv_ram)
        val tvBiayaSewa: TextView = itemView.findViewById(R.id.tv_biaya_sewa)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ListViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.list_item_barang, parent, false)
        return ListViewHolder(view)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val currencyFormat = NumberFormat.getCurrencyInstance()
        currencyFormat.maximumFractionDigits = 2
        currencyFormat.currency = Currency.getInstance("IDR")
        val (_, fotoBarang, merek, model, prosesor, _, ram, _, _, _, _, _, _, biayaSewa, stok) = listBarang[position]

        holder.apply {
            Glide.with(holder.itemView.context)
                .load(fotoBarang)
                .apply(RequestOptions())
                .into(ivBarang)
            tvMerek.text = "$merek $model"
            tvStok.text = "Stok: $stok"
            tvProsesor.text = prosesor
            tvRam.text = ram
            tvBiayaSewa.text = "${currencyFormat.format(biayaSewa)} /Hari"
            itemView.setOnClickListener {
                val detailBarangFragment = DetailBarangFragment()
                val mFragmentManager =
                    (holder.itemView.context as AppCompatActivity).supportFragmentManager
                val bundle = Bundle()

                bundle.putParcelable(DetailBarangFragment.EXTRA_BARANG, listBarang[position])
                detailBarangFragment.arguments = bundle
                mFragmentManager.beginTransaction().apply {
                    replace(R.id.fragment_container,detailBarangFragment, DetailBarangFragment::class.java.simpleName)
                    addToBackStack(null)
                    commit()
                }
            }
        }
    }

    override fun getItemCount(): Int = listBarang.size
}