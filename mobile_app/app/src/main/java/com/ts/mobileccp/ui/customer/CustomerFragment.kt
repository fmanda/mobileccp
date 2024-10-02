package com.ts.mobileccp.ui.customer

//import Customer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.ts.mobileccp.R
import com.ts.mobileccp.adapter.CustomerSelectListener
import com.ts.mobileccp.adapter.ListCustomerAdapter
import com.ts.mobileccp.adapter.ListKelurahanAdapter
import com.ts.mobileccp.adapter.SelectKelurahanListener
//import com.fma.mobility.adapter.ListCustomerAdapter
import com.ts.mobileccp.databinding.FragmentCustomerBinding
import com.ts.mobileccp.db.entity.Customer


class CustomerFragment : Fragment(), CustomerSelectListener, SelectKelurahanListener {
    private var _binding: FragmentCustomerBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var customerViewModel: CustomerViewModel;
    val adapter = ListCustomerAdapter(emptyList(), this)

    private var filterQuery: String = ""
    private var filterKelurahan: String = ""
    val adapterKelurahan = ListKelurahanAdapter(emptyList(), 0, this)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val factory = CustomerViewModelFactory(requireActivity().application)
        customerViewModel = ViewModelProvider(this, factory).get(CustomerViewModel::class.java)
        _binding = FragmentCustomerBinding.inflate(inflater, container, false)

        binding.rvListCustomer.layoutManager = LinearLayoutManager(requireContext())
        binding.rvListCustomer.adapter = adapter

        binding.rvKelurahan.adapter = adapterKelurahan
        binding.rvKelurahan.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)


//        autorefresh when changed, :
//        customerViewModel.listcustomer.observe(viewLifecycleOwner, Observer { data ->
//            data?.let { adapter.updateData(it) }
//        })
        loadKelurahan()
        searchIt();

        binding.btnAdd.setOnClickListener{
            findNavController().navigate(R.id.action_nav_customer_to_nav_customer_update)
        }

        // Setup SearchView
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
        //do nothing
    }

    override fun onSelected(position: Int, kelurahan: String) {
        adapterKelurahan.setSelectKelurahan(position)

        if (position == 0) filterKelurahan = "" else filterKelurahan = kelurahan

        searchIt(filterQuery, filterKelurahan)
    }

}