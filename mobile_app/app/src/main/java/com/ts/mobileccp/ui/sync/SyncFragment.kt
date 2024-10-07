package com.ts.mobileccp.ui.sync

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.ts.mobileccp.databinding.FragmentSyncBinding
import com.ts.mobileccp.ui.SharedViewModel

/**
 * A simple [Fragment] subclass.
 * Use the [SyncFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SyncFragment : Fragment() {
    private var _binding: FragmentSyncBinding? = null


    private val binding get() = _binding!!
    private lateinit var sharedViewModel: SharedViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        sharedViewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)

        _binding = FragmentSyncBinding.inflate(inflater, container, false)

        binding.lnDownload.setOnClickListener(){
            syncData()
        }

        sharedViewModel.last_download.observe(viewLifecycleOwner) { data ->
            data?.let {
                binding.txtLastDownload.text = data.toString()
            }
        }

        sharedViewModel.customerCount.observe(viewLifecycleOwner){ data ->
            data?.let {
                binding.txtCustRecords.text = "$data Records"
            }
        }

        sharedViewModel.inventoryCount.observe(viewLifecycleOwner){ data ->
            data?.let {
                binding.txtInventoryRecrods.text = "$data Records"
            }
        }

        binding.backBtn.setOnClickListener{
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        sharedViewModel.isRestProcessing.observe(viewLifecycleOwner) { data ->
            data?.let { setSyncState(it) }
        }

        //set toolbar
        val root: View = binding.root
        return root
    }

    private fun syncData(){
        setSyncState(true)
        sharedViewModel.syncData()
    }
    fun setSyncState(downloading: Boolean){
        if (downloading){
            binding.imgSync.visibility = View.GONE
            binding.pgDownload.visibility = View.VISIBLE
        }else{
            binding.imgSync.visibility = View.VISIBLE
            binding.pgDownload.visibility = View.GONE
        }
    }



}