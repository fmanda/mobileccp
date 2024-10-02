package com.ts.mobileccp.ui.customer

//import Customer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.ts.mobileccp.adapter.CustomerSelectListener
import com.ts.mobileccp.adapter.ListCustomerAdapter
import com.ts.mobileccp.adapter.ListKelurahanAdapter
import com.ts.mobileccp.adapter.SelectKelurahanListener
import com.ts.mobileccp.databinding.FragmentDialogCustomerBinding
import com.ts.mobileccp.db.entity.Customer
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment



class DialogCustomerFragment : BottomSheetDialogFragment(), CustomerSelectListener, SelectKelurahanListener {
    private var _binding: FragmentDialogCustomerBinding? = null

    private val binding get() = _binding!!
    private lateinit var customerViewModel: CustomerViewModel;
    val adapter = ListCustomerAdapter(emptyList(), this, true)
    val adapterKelurahan = ListKelurahanAdapter(emptyList(), 0, this)

    private var filterQuery: String = ""
    private var filterKelurahan: String = ""

    interface DialogCustomerListener {
        fun onSelectDialogCustomer(cust: Customer)
    }

    // Use this interface to communicate with the host
    private var listener: DialogCustomerListener? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val factory = CustomerViewModelFactory(requireActivity().application)
        customerViewModel = ViewModelProvider(this, factory).get(CustomerViewModel::class.java)

        _binding = FragmentDialogCustomerBinding.inflate(inflater, container, false)

        binding.rvKelurahan.adapter = adapterKelurahan
        binding.rvKelurahan.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.rvKelurahan.setNestedScrollingEnabled(false);

        binding.rvListCustomer.adapter = adapter
        binding.rvListCustomer.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
//        binding.rvListCustomer.setNestedScrollingEnabled(false);

        loadKelurahan()
        searchIt()
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    filterQuery = it
                    searchIt(filterQuery, filterKelurahan)
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let {
                    filterQuery = it
                    searchIt(filterQuery, filterKelurahan)
                }
                return false
            }
        })

        val root: View = binding.root
        return root
    }

    private fun searchIt(query: String="", kelurahan: String = "") {
        customerViewModel.searchCustomer(query, kelurahan).observe(viewLifecycleOwner, Observer { items ->
            adapter.updateData(items)
        })
    }

    private fun loadKelurahan() {
        customerViewModel.loadKelurahan().observe(viewLifecycleOwner) { items ->
            adapterKelurahan.updateData(items)
        }
    }

    override fun onSelect(cust: Customer, position: Int) {
        listener?.onSelectDialogCustomer(cust)
        dismiss()
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
    }

    fun setOnDialogCustomerSelect(listener: DialogCustomerListener) {
        this.listener = listener
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): BottomSheetDialog {
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog

        dialog.setOnShowListener { dialogInterface ->
            val bottomSheetDialog = dialogInterface as BottomSheetDialog
            val bottomSheet = bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as View
            val behavior = BottomSheetBehavior.from(bottomSheet)
            behavior.state = BottomSheetBehavior.STATE_EXPANDED

            // Set the BottomSheet to full height
            bottomSheet.layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
        }

        return dialog
    }

    override fun onSelected(position: Int, kelurahan: String) {
        adapterKelurahan.setSelectKelurahan(position)

        if (position == 0) filterKelurahan = "" else filterKelurahan = kelurahan

        searchIt(filterQuery, filterKelurahan)
    }


}
