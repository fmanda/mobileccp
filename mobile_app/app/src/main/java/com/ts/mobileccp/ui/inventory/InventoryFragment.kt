package com.ts.mobileccp.ui.inventory


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.ts.mobileccp.databinding.FragmentInventoryBinding


class InventoryFragment : Fragment() {

    private var binding: FragmentInventoryBinding? = null
    private lateinit var inventoryViewModel: InventoryViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentInventoryBinding.inflate(inflater, container, false)
        val factory = InventoryViewModelFactory(requireActivity().application)
        inventoryViewModel = ViewModelProvider(this, factory).get(InventoryViewModel::class.java)


        return binding!!.root
    }
}