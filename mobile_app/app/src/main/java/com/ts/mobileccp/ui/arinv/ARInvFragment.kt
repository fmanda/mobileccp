package com.ts.mobileccp.ui.arinv

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.ts.mobileccp.R
import com.ts.mobileccp.adapter.ListARInvAdapter
import com.ts.mobileccp.databinding.FragmentArinvBinding

class ARInvFragment : Fragment() {
    private var _binding : FragmentArinvBinding? = null
    val binding get() = _binding!!

    private lateinit var arInvViewModel: ARInvViewModel

    private val adapter = ListARInvAdapter(emptyList())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentArinvBinding.inflate(inflater, container, false)
        val factory = ARInvViewModelFactory(requireActivity().application)
        arInvViewModel = ViewModelProvider(this,factory).get(ARInvViewModel::class.java)

        binding.rvARInv.layoutManager = LinearLayoutManager(requireContext())
        binding.rvARInv.adapter = adapter

        arInvViewModel.arInvList.observe(viewLifecycleOwner){ data ->
            data?.let{
                adapter.updateData(data)
            }
        }

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    searchIt(it)
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let {
                    searchIt(it)
                }
                return false
            }
        })

        return binding.root
    }

    private fun searchIt(query: String="") {
        if (query.isEmpty()){
            arInvViewModel.arInvList.observe(viewLifecycleOwner) { data ->
                data?.let { adapter.updateData(data) }
            }
        }else{
            arInvViewModel.searchARList(query).observe(viewLifecycleOwner) { data ->
                data?.let{
                    adapter.updateData(data)
                }
            }
        }

    }
}