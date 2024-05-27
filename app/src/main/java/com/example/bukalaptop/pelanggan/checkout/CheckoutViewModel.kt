package com.example.bukalaptop.pelanggan.checkout

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.bukalaptop.model.Keranjang
import com.example.bukalaptop.pegawai.barang.model.Barang
import com.example.bukalaptop.utils.SingleLiveEvent
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class CheckoutViewModel : ViewModel() {

    private val _listKeranjang = MutableLiveData<List<Keranjang>>()
    val listKeranjang: LiveData<List<Keranjang>> = _listKeranjang

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _total = MutableLiveData<Int>()
    val total: LiveData<Int> = _total

    internal val toast = SingleLiveEvent<String>()

    private val db = Firebase.firestore
    private val auth = Firebase.auth
    private val pelangganId = auth.currentUser?.uid ?: ""

    init {
        fetchKeranjang()
    }

    private fun fetchKeranjang() {
        _isLoading.value = true
        db.collection("pengguna").document(pelangganId).collection("keranjang")
            .addSnapshotListener { keranjang, error ->
                if (keranjang != null) {
                    val list = arrayListOf<Keranjang>()
                    var totalAmount = 0

                    for (krnjng in keranjang) {
                        db.collection("barang")
                            .addSnapshotListener { barang, error1 ->
                                if (barang != null) {
                                    for (brng in barang) {
                                        if (brng.id == krnjng.id) {
                                            val mBarang = brng.toObject(Barang::class.java)
                                            val jumlah = krnjng.get("jumlah").toString().toInt()
                                            totalAmount += (mBarang.biayaSewa * jumlah)

                                            val mKeranjang = Keranjang(mBarang, jumlah)
                                            mKeranjang.barang = brng.toObject(Barang::class.java)
                                            mKeranjang.jumlah = jumlah

                                            list.add(mKeranjang)
                                        }
                                    }
                                }
                                _listKeranjang.value = list
                                _total.value = totalAmount
                            }
                    }
                    _isLoading.value = false
                } else if (error != null) {
                    _isLoading.value = false
                    setToast(toast, error.toString())
                }
            }
    }

    fun deleteItem(barang: Barang) {
        _isLoading.value = true
        db.collection("pengguna").document(pelangganId).collection("keranjang")
            .document(barang.barangId)
            .delete()
            .addOnSuccessListener {
                fetchKeranjang()
            }
            .addOnFailureListener { e ->
                _isLoading.value = false
                setToast(toast, e.toString())
            }
    }

    private fun setToast(toast: SingleLiveEvent<String>, e: String) {
        toast.value = e
    }

}