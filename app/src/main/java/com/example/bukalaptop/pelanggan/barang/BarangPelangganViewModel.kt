package com.example.bukalaptop.pelanggan.barang

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.bukalaptop.pegawai.barang.model.Barang
import com.example.bukalaptop.utils.SingleLiveEvent
import com.google.firebase.firestore.FirebaseFirestore

class BarangPelangganViewModel:ViewModel() {
    private val _listBarang = MutableLiveData<List<Barang>>()
    val listBarang: LiveData<List<Barang>> = _listBarang

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    internal val toast = SingleLiveEvent<String>()

    fun getListBarang(db: FirebaseFirestore){
        _isLoading.value = true
        db.collection("barang").addSnapshotListener { value, error ->
            val list: ArrayList<Barang> = arrayListOf()
            if (value != null) {
                for (document in value) {
                    val barang = document.toObject(Barang::class.java)
                    list.add(barang)
                }
                _isLoading.value = false
            } else if (error != null) {
                _isLoading.value = false
                setToast(toast, error.toString())
            }
            _listBarang.value = list
        }
    }

    private fun setToast(toast: SingleLiveEvent<String>, e: String) {
        toast.value = e
    }
}