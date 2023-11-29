package com.example.bukalaptop.pegawai.pesanan.adapter

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
        val ivBarang: ImageView = itemView.findViewById(R.id.iv_tambah_barang)
        val tvMerek: TextView = itemView.findViewById(R.id.et_merek)
        val tvModel: TextView = itemView.findViewById(R.id.tv_model)
        val tvProsesor: TextView = itemView.findViewById(R.id.tv_prosesor)
        val tvBiayaSewa: TextView = itemView.findViewById(R.id.tv_biaya_sewa)
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
        val (barang, jumlah) = listKeranjang[position]

        holder.apply {
            Glide.with(itemView.context)
                .load(barang.fotoBarang)
                .apply(RequestOptions())
                .into(ivBarang)
            tvMerek.text = barang.merek
            tvModel.text = barang.model
            tvProsesor.text = barang.prosesor
            tvBiayaSewa.text = "${currencyFormat.format(barang.biayaSewa)}/Unit"
            tvJumlah.text = "$jumlah Unit"
            tvSubtotal.text = currencyFormat.format(barang.biayaSewa * jumlah)
            itemView.setOnClickListener {
                val detailBarangFragment = DetailBarangFragment()
                val mFragmentManager =
                    (holder.itemView.context as AppCompatActivity).supportFragmentManager
                val bundle = Bundle()

                bundle.putString(DetailBarangFragment.EXTRA_IDBARANG, barang.barangId)
                detailBarangFragment.arguments = bundle
                mFragmentManager.beginTransaction().apply {
                    replace(R.id.fragment_pegawai_container,detailBarangFragment, DetailBarangFragment::class.java.simpleName)
                    addToBackStack(null)
                    commit()
                }
            }
        }
    }

    override fun getItemCount(): Int = listKeranjang.size
}