package com.example.bukalaptop.pelanggan.riwayat.adapter

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.bukalaptop.R
import com.example.bukalaptop.model.Pelanggan
import com.example.bukalaptop.model.Pesanan
import com.example.bukalaptop.pelanggan.riwayat.DetailRiwayatFragment
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class ListRiwayatAdapter(private val listRiwayat: ArrayList<Pesanan>) :
    RecyclerView.Adapter<ListRiwayatAdapter.ListViewHolder>() {
    fun setData(data: List<Pesanan>) {
        listRiwayat.clear()
        listRiwayat.addAll(data)
        notifyDataSetChanged()
    }

    class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvNama: TextView =itemView.findViewById(R.id.et_nama_lengkap)
        val tvTelepon:TextView=itemView.findViewById(R.id.tv_nomor_telepon)
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
        val (pesananId,idPelanggan) = listRiwayat[position]
        holder.apply {
            val db = Firebase.firestore
            db.collection("pengguna").addSnapshotListener{valuePelanggan,errorPelanggan->
                if (errorPelanggan != null) {
                    Log.d("List Pesanan Error", errorPelanggan.toString())
                    return@addSnapshotListener
                }
                if (valuePelanggan != null) {
                    for (document in valuePelanggan) {
                        if (document.getString("id") == idPelanggan) {
                            val pelanggan=document.toObject(Pelanggan::class.java)
                            tvNama.text = pelanggan.namaLengkap
                            tvTelepon.text = pelanggan.nomorHp
                        }
                    }
                }
            }
            itemView.setOnClickListener {
                val detailRiwayatFragment = DetailRiwayatFragment()
                val mFragmentManager =
                    (holder.itemView.context as AppCompatActivity).supportFragmentManager
                val bundle = Bundle()

                bundle.putString(DetailRiwayatFragment.EXTRA_IDPELANGGAN, idPelanggan)
                bundle.putString(DetailRiwayatFragment.EXTRA_IDPESANAN, pesananId)
                detailRiwayatFragment.arguments = bundle
                mFragmentManager.beginTransaction().apply {
                    replace(R.id.fragment_pelanggan_container,detailRiwayatFragment, DetailRiwayatFragment::class.java.simpleName)
                    addToBackStack(null)
                    commit()
                }
            }
        }
    }

    override fun getItemCount(): Int =listRiwayat.size
}