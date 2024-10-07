package com.ts.mobileccp.ui.visit

import android.R
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.ts.mobileccp.databinding.FragmentVisitBinding
import kotlin.math.log

class VisitFragment : Fragment() {

    private var _binding : FragmentVisitBinding? = null
    private val binding get() = _binding!!
    private lateinit var visitViewModel: VisitViewModel
    private lateinit var markAdapter: ArrayAdapter<String>
    private lateinit var schAdapter: ArrayAdapter<String>


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentVisitBinding.inflate(inflater, container, false)
        val factory = VisitViewModelFactory(requireActivity().application)
        visitViewModel = ViewModelProvider(this, factory).get(VisitViewModel::class.java)

        initData()

        return binding.root
    }

    private fun initData(){
        markAdapter = ArrayAdapter(requireContext(), R.layout.simple_spinner_item, mutableListOf())
        markAdapter.setDropDownViewResource(R.layout.simple_spinner_item)
        binding.spMark.adapter = markAdapter

        schAdapter = ArrayAdapter(requireContext(), R.layout.simple_spinner_item, mutableListOf())
        schAdapter.setDropDownViewResource(R.layout.simple_spinner_item)
        binding.spSCH.adapter = schAdapter


        visitViewModel.listCCPMark.observe(viewLifecycleOwner){ objs->
            markAdapter.clear()
            markAdapter.addAll(objs.map{ obj->
                obj.markname
            })
            markAdapter.notifyDataSetChanged()
        }

        visitViewModel.listCCPSch.observe(viewLifecycleOwner){ objs ->
            schAdapter.clear()
            schAdapter.addAll(objs.map { obj->
                obj.ccpschname
            })
            schAdapter.notifyDataSetChanged()
        }
    }
}
