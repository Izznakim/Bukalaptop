package com.example.bukalaptop.pelanggan.barang

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bukalaptop.R
import com.example.bukalaptop.databinding.FragmentBarangPelangganBinding
import com.example.bukalaptop.pegawai.barang.adapter.ListBarangPelangganAdapter
import com.example.bukalaptop.pegawai.barang.model.Barang
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class BarangPelangganFragment : Fragment() {

    private var _binding: FragmentBarangPelangganBinding? = null

    private lateinit var barangPelangganViewModel: BarangPelangganViewModel
    private lateinit var adapter: ListBarangPelangganAdapter
    private lateinit var listBarang: ArrayList<Barang>
    private lateinit var tvProgress: TextView
    private lateinit var builder: AlertDialog.Builder
    private lateinit var progressDialog: AlertDialog
    private lateinit var db: FirebaseFirestore

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        barangPelangganViewModel =
            ViewModelProvider(this)[BarangPelangganViewModel::class.java]

        builder = AlertDialog.Builder(requireContext())

        db = Firebase.firestore

        val dialogView=layoutInflater.inflate(R.layout.progress_layout,null)
        builder.setView(dialogView)
        builder.setCancelable(false)
        progressDialog = builder.create()

        tvProgress=dialogView.findViewById(R.id.tv_progress)

        listBarang = arrayListOf()

        _binding = FragmentBarangPelangganBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvBarang.setHasFixedSize(true)

        initAdapter()
        viewModel()
    }

    private fun initAdapter() {
        binding.rvBarang.layoutManager = LinearLayoutManager(activity)
        adapter = ListBarangPelangganAdapter(arrayListOf())
        binding.rvBarang.adapter = adapter
    }

    private fun viewModel(){
        with(barangPelangganViewModel) {
            getListBarang(db)
            listBarang.observe(viewLifecycleOwner) {
                if (it != null) {
                    adapter.setData(it)
                }
            }
            toast.observe(viewLifecycleOwner) {
                if (it != null) {
                    val toast = it.format(this)
                    Toast.makeText(activity, toast, Toast.LENGTH_SHORT).show()
                }
            }
            isLoading.observe(viewLifecycleOwner) {
                showLoading(it)
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            tvProgress.text="Memuat barang..."
            progressDialog.show()
        } else {
            progressDialog.dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}