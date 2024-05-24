package com.example.bukalaptop.pegawai.barang.adapter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.bukalaptop.R
import com.example.bukalaptop.databinding.ListItemBarangBinding
import com.example.bukalaptop.pegawai.barang.model.Barang
import com.example.bukalaptop.pelanggan.barang.DetailBarangPelangganFragment
import java.text.NumberFormat
import java.util.Currency

class ListBarangPelangganAdapter(private val listBarang: ArrayList<Barang>) :
    RecyclerView.Adapter<ListBarangPelangganAdapter.ListViewHolder>() {
    fun setData(data: List<Barang>) {
        listBarang.clear()
        listBarang.addAll(data)
        notifyDataSetChanged()
    }

    class ListViewHolder(private val binding: ListItemBarangBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(barang: Barang) {
            val currencyFormat = NumberFormat.getCurrencyInstance()
            currencyFormat.maximumFractionDigits = 2
            currencyFormat.currency = Currency.getInstance("IDR")

            with(binding) {
                Glide.with(itemView.context)
                    .load(barang.fotoBarang)
                    .apply(RequestOptions())
                    .into(ivTambahBarang)
                tvMerek.text = "${barang.merek} ${barang.model}"
                tvStok.text = "Stok: ${barang.stok}"
                tvProsesor.text = barang.prosesor
                tvRam.text = barang.ram
                tvBiayaSewa.text = "${currencyFormat.format(barang.biayaSewa)} /Hari"
                itemView.setOnClickListener {
                    val detailBarangPelangganFragment = DetailBarangPelangganFragment()
                    val mFragmentManager =
                        (itemView.context as AppCompatActivity).supportFragmentManager
                    val bundle = Bundle()

                    bundle.putString(DetailBarangPelangganFragment.EXTRA_IDBARANG, barang.barangId)
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
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ListViewHolder {
        val binding =
            ListItemBarangBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) =
        holder.bind(listBarang[position])

    override fun getItemCount(): Int = listBarang.size
}