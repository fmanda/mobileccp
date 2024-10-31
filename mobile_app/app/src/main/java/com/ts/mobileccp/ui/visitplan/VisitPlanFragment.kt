package com.ts.mobileccp.ui.visitplan

import android.annotation.SuppressLint
import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.ts.mobileccp.R
import com.ts.mobileccp.adapter.VisitPlanAdapter
import com.ts.mobileccp.adapter.VisitPlanListener
import com.ts.mobileccp.databinding.FragmentVisitBinding
import com.ts.mobileccp.databinding.FragmentVisitPlanBinding
import com.ts.mobileccp.db.entity.LastVisitPlan

class VisitPlanFragment : Fragment(), VisitPlanListener {

    private lateinit var _binding : FragmentVisitPlanBinding
    private lateinit var visitPlanViewModel: VisitPlanViewModel
    val binding get() = _binding
    var filterQuery: String = ""

    val adapter: VisitPlanAdapter = VisitPlanAdapter(emptyList(), this)


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentVisitPlanBinding.inflate(inflater, container, false)
        val factory = VisitPlanViewModelFactory(requireActivity().application)
        visitPlanViewModel = ViewModelProvider(this, factory).get(VisitPlanViewModel::class.java)

        binding.rvVisitPlan.layoutManager = LinearLayoutManager(requireContext())
        binding.rvVisitPlan.adapter = adapter

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    filterQuery = it
                    searchIt(filterQuery)
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let {
                    filterQuery = it
                    searchIt(filterQuery)
                }
                return false
            }
        })

        searchIt("")
        return binding.root
    }

    fun searchIt(query:String){
        visitPlanViewModel.getVisitPlans(query).observe(viewLifecycleOwner) { visits ->
            adapter.updateData(visits)
        }
    }

    override fun onClickListener(visitplan: LastVisitPlan, position: Int) {
        val args = Bundle().apply {
            putInt("customerID", visitplan.partnerid)
        }
        findNavController().navigate(R.id.action_nav_visitplan_to_nav_visit, args)
    }
}