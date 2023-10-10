package com.example.bukalaptop.pegawai.pesanan.adapter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.bukalaptop.R
import com.example.bukalaptop.pegawai.pesanan.DetailPesananFragment
import com.example.bukalaptop.pegawai.pesanan.model.Pesanan

class ListPesananAdapter(private val listPesanan: ArrayList<Pesanan>) :
    RecyclerView.Adapter<ListPesananAdapter.ListViewHolder>() {
    fun setData(data:List<Pesanan>){
        listPesanan.clear()
        listPesanan.addAll(data)
        notifyDataSetChanged()
    }

    class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvNama: TextView = itemView.findViewById(R.id.et_merek)
        val tvTelepon: TextView = itemView.findViewById(R.id.tv_model)
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
                val detailPesananFragment = DetailPesananFragment()
                val mFragmentManager =
                    (holder.itemView.context as AppCompatActivity).supportFragmentManager
                val bundle = Bundle()

                bundle.putParcelable(DetailPesananFragment.EXTRA_PESANAN, listPesanan[position])
                detailPesananFragment.arguments = bundle
                mFragmentManager.beginTransaction().apply {
                    replace(R.id.fragment_container,detailPesananFragment, DetailPesananFragment::class.java.simpleName)
                    addToBackStack(null)
                    commit()
                }
            }
        }
    }

    override fun getItemCount(): Int = listPesanan.size
}