package com.example.bukalaptop.pelanggan.barang

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.bukalaptop.pegawai.barang.model.Barang
import com.example.bukalaptop.utils.SingleLiveEvent
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class DetailBarangPelangganViewModel : ViewModel() {
    private val _detailBarang = MutableLiveData<Barang>()
    val detailBarang: LiveData<Barang> = _detailBarang

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isSuccess = MutableLiveData<Boolean>()
    val isSuccess: LiveData<Boolean> = _isSuccess

    private val _jumlah = 0
    var jumlah: Int = _jumlah

    internal val toast = SingleLiveEvent<String>()

    fun getDetailBarang(db: FirebaseFirestore, barangId: String) {
        _isLoading.value = true
        db.collection("barang").addSnapshotListener { value, error ->
            if (value != null) {
                for (document in value) {
                    if (document.id == barangId) {
                        _detailBarang.value = document.toObject(Barang::class.java)
                    }
                }
                _isLoading.value = false
            } else if (error != null) {
                _isLoading.value = false
                setToast(toast, error.toString())
            }
        }
    }

    fun tambahKeranjang(db: FirebaseFirestore, auth: FirebaseAuth,jumlah: Int, barangId: String, barang: Barang) {
        val keranjang = hashMapOf(
            "jumlah" to jumlah
        )
        _isLoading.value = true
        db.collection("pengguna").document(auth.currentUser?.uid ?: "")
            .collection("keranjang").document(barangId)
            .set(keranjang)
            .addOnSuccessListener { documentReference ->
                _isLoading.value = false
                setToast(toast, "${barang.merek} ${barang.model} telah ditambahkan ke keranjang.")
                _isSuccess.value=true
            }
            .addOnFailureListener { e ->
                _isLoading.value = false
                setToast(toast, e.toString())
                _isSuccess.value=false
            }
    }

    private fun setToast(toast: SingleLiveEvent<String>, e: String) {
        toast.value = e
    }
}