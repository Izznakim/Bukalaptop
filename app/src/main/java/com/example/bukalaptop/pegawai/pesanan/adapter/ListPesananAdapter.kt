package com.example.bukalaptop.pegawai.pesanan.adapter

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.bukalaptop.R
import com.example.bukalaptop.pegawai.pesanan.DetailPesananFragment
import com.example.bukalaptop.model.Pelanggan
import com.example.bukalaptop.model.Pesanan
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class ListPesananAdapter(private val listPesanan: ArrayList<Pesanan>) :
    RecyclerView.Adapter<ListPesananAdapter.ListViewHolder>() {
    fun setData(data: List<Pesanan>) {
        listPesanan.clear()
        listPesanan.addAll(data)
        notifyDataSetChanged()
    }

    class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvNama: TextView = itemView.findViewById(R.id.et_nama_lengkap)
        val tvTelepon: TextView = itemView.findViewById(R.id.tv_nomor_telepon)
        val cardView: CardView = itemView.findViewById(R.id.card_view)
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
        val pesanan = listPesanan[position]
        val pesananId = pesanan.id
        val idPelanggan = pesanan.idPelanggan
        holder.apply {
            val db = Firebase.firestore
            db.collection("pengguna").addSnapshotListener { valuePelanggan, errorPelanggan ->
                if (errorPelanggan != null) {
                    Log.d("List Pesanan Error", errorPelanggan.toString())
                    return@addSnapshotListener
                }
                if (valuePelanggan != null) {
                    for (document in valuePelanggan) {
                        if (document.getString("id") == idPelanggan) {
                            val pelanggan = document.toObject(Pelanggan::class.java)
                            tvNama.text = pelanggan.namaLengkap
                            tvTelepon.text = pelanggan.nomorHp
                        }
                    }
                }
            }

            if (pesanan.status == "diterima" || pesanan.status == "dikembalikan") {
                cardView.setCardBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.green))
            } else {
                cardView.setCardBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.default_card_color))
            }

            itemView.setOnClickListener {
                val detailPesananFragment = DetailPesananFragment()
                val mFragmentManager =
                    (holder.itemView.context as AppCompatActivity).supportFragmentManager
                val bundle = Bundle()

                bundle.putString(DetailPesananFragment.EXTRA_IDPESANAN, pesananId)
                detailPesananFragment.arguments = bundle
                mFragmentManager.beginTransaction().apply {
                    replace(
                        R.id.fragment_pegawai_container,
                        detailPesananFragment,
                        DetailPesananFragment::class.java.simpleName
                    )
                    addToBackStack(null)
                    commit()
                }
            }
        }
    }

    override fun getItemCount(): Int = listPesanan.size
}