package com.example.bukalaptop.pelanggan.checkout

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bukalaptop.R
import com.example.bukalaptop.databinding.FragmentCheckoutBinding
import com.example.bukalaptop.model.Keranjang
import com.example.bukalaptop.pegawai.barang.DetailBarangFragment
import com.example.bukalaptop.pegawai.barang.model.Barang
import com.example.bukalaptop.pelanggan.barang.DetailBarangPelangganFragment
import com.example.bukalaptop.pelanggan.checkout.adapter.ListBarangCheckoutAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.NumberFormat
import java.util.Currency

class CheckoutFragment : Fragment(), ListBarangCheckoutAdapter.OnItemClickListener {

    private var _binding: FragmentCheckoutBinding? = null

    private lateinit var adapter: ListBarangCheckoutAdapter
    private lateinit var listKeranjang: ArrayList<Keranjang>
    private lateinit var tvProgress: TextView
    private lateinit var builder: AlertDialog.Builder
    private lateinit var progressDialog: AlertDialog
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var checkoutViewModel: CheckoutViewModel

    private val binding get() = _binding!!
    private var pelangganId: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment

        builder = AlertDialog.Builder(requireContext())
        val dialogView = layoutInflater.inflate(R.layout.progress_layout, null)
        builder.setView(dialogView)
        builder.setCancelable(false)
        progressDialog = builder.create()

        tvProgress = dialogView.findViewById(R.id.tv_progress)

        auth = Firebase.auth
        db = Firebase.firestore

        listKeranjang = arrayListOf()

        _binding = FragmentCheckoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        checkoutViewModel = ViewModelProvider(this).get(CheckoutViewModel::class.java)

        binding.rvKeranjang.setHasFixedSize(true)

        pelangganId = auth.currentUser?.uid ?: ""
        initAdapter()
        viewModel()

        binding.btnCheckout.setOnClickListener {
            if (adapter.itemCount <= 0) {
                Toast.makeText(activity, "Keranjang masih kosong", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val paymentFragment = PaymentFragment()
            val mFragmentManager = activity?.supportFragmentManager
            val bundle = Bundle()

            bundle.putString(PaymentFragment.EXTRA_PELANGGANID, pelangganId)
            bundle.putInt(PaymentFragment.EXTRA_TOTAL, checkoutViewModel.total.value?: 0)
            bundle.putParcelableArrayList(
                PaymentFragment.EXTRA_KERANJANG,
                ArrayList(checkoutViewModel.listKeranjang.value ?: listOf())
            )
            paymentFragment.arguments = bundle
            mFragmentManager?.beginTransaction()?.apply {
                replace(
                    R.id.fragment_pelanggan_container,
                    paymentFragment,
                    PaymentFragment::class.java.simpleName
                )
                addToBackStack(null)
                commit()
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

    private fun viewModel() {
        with(checkoutViewModel) {
            listKeranjang.observe(viewLifecycleOwner) { keranjang ->
                adapter.setData(keranjang)
            }

            total.observe(viewLifecycleOwner) { total ->
                val currencyFormat = NumberFormat.getCurrencyInstance()
                currencyFormat.maximumFractionDigits = 2
                currencyFormat.currency = Currency.getInstance("IDR")
                binding.tvTotal.text = currencyFormat.format(total)
                progressDialog.dismiss()
            }

            toast.observe(viewLifecycleOwner) {
                val toast = it.format(this)
                Toast.makeText(activity, toast, Toast.LENGTH_SHORT).show()
            }
            isLoading.observe(viewLifecycleOwner) {
                showLoading(it)
            }
        }
    }

    private fun initAdapter() {
        binding.rvKeranjang.layoutManager = LinearLayoutManager(activity)
        adapter = ListBarangCheckoutAdapter(arrayListOf(), this)
        binding.rvKeranjang.adapter = adapter
    }

    override fun onItemClick(barang: Barang) {
        val detailBarangPelangganFragment = DetailBarangPelangganFragment()
        val mFragmentManager = requireActivity().supportFragmentManager
        val bundle = Bundle()
        bundle.putString(DetailBarangFragment.EXTRA_IDBARANG, barang.barangId)
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

    override fun onDeleteClick(position: Int, barang: Barang) {
        val builder = AlertDialog.Builder(requireContext())

        builder.setMessage(
            HtmlCompat.fromHtml(
                "Anda yakin ingin menghapus <b>${barang.merek}</b> <b>${barang.model}</b> dari Keranjang Anda?",
                HtmlCompat.FROM_HTML_MODE_LEGACY
            )
        )
            .setTitle("Konfirmasi")

        builder.setPositiveButton("Ya") { _, _ ->
            checkoutViewModel.deleteItem(barang)
            if (adapter.itemCount <= 1) {
                adapter.setData(emptyList())
                binding.tvTotal.text = "Total semua barang: Rp.0"
            }
        }

        builder.setNegativeButton("Tidak") { dialog, _ ->
            dialog.cancel()
        }

        val dialog = builder.create()
        dialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}