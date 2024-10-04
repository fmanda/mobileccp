package com.ts.mobileccp.ui.inventory


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.ts.mobileccp.adapter.InventoryAdapter
import com.ts.mobileccp.adapter.ListMerkAdapter
import com.ts.mobileccp.adapter.ListSalesOrderAdapter
import com.ts.mobileccp.adapter.SelectMerkListener
import com.ts.mobileccp.databinding.FragmentInventoryBinding


class InventoryFragment : Fragment(), SelectMerkListener {

    private var _binding: FragmentInventoryBinding? = null
    private val binding get() = _binding!!
    private lateinit var inventoryViewModel: InventoryViewModel
    val adapter = InventoryAdapter(emptyList(), emptyList())
    private val merkAdapter = ListMerkAdapter(emptyList(), 0, this)
    private var filterQuery: String = ""
    private var filterMerk: String = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentInventoryBinding.inflate(inflater, container, false)
        val factory = InventoryViewModelFactory(requireActivity().application)
        inventoryViewModel = ViewModelProvider(this, factory).get(InventoryViewModel::class.java)

        binding.rvInventory.layoutManager = LinearLayoutManager(requireContext())
        binding.rvInventory.adapter = adapter

        binding.rvCategory.adapter = merkAdapter
        binding.rvCategory.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)


        inventoryViewModel.priceLevels.observe(viewLifecycleOwner, Observer { items ->
            adapter.updateDataPriceLevel(items)
        })

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    filterQuery = it
                    searchIt(filterQuery, filterMerk)

                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let {
                    filterQuery = it
                    searchIt(filterQuery, filterMerk)
                }
                return false
            }
        })

        loadMerk();
        searchIt();

        return binding.root
    }

    private fun searchIt(query: String="", category:String="") {
        inventoryViewModel.lookupInventory( query, category).observe(viewLifecycleOwner, Observer { items ->
            adapter.updateData(items)
        })

    }

    override fun onSelectedMerk(position: Int, merk: String) {
        merkAdapter.setSelectedMerk(position)

        if (position == 0) filterMerk = "" else filterMerk = merk

        searchIt(filterQuery, filterMerk)

    }

    private fun loadMerk() {
        inventoryViewModel.loadMerk().observe(viewLifecycleOwner) { items ->
            merkAdapter.updateData(items)
        }
    }
}