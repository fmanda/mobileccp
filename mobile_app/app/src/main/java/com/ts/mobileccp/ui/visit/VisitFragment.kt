package com.ts.mobileccp.ui.visit

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.ts.mobileccp.databinding.FragmentVisitBinding

class VisitFragment : Fragment() {

    private var _binding : FragmentVisitBinding? = null
    private val binding get() = _binding!!
    private lateinit var visitViewModel: VisitViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentVisitBinding.inflate(inflater, container, false)
        val factory = VisitViewModelFactory(requireActivity().application)
        visitViewModel = ViewModelProvider(this, factory).get(VisitViewModel::class.java)



        return binding.root
    }
}