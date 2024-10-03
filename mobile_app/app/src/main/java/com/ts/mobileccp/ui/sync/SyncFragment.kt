package com.ts.mobileccp.ui.sync

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.ts.mobileccp.databinding.FragmentSyncBinding

/**
 * A simple [Fragment] subclass.
 * Use the [SyncFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SyncFragment : Fragment() {
    private var _binding: FragmentSyncBinding? = null


    private val binding get() = _binding!!
    private lateinit var syncViewModel: SyncViewModel;


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val factory = SettingViewModelFactory(requireActivity().application)
        syncViewModel = ViewModelProvider(this, factory).get(SyncViewModel::class.java)
        _binding = FragmentSyncBinding.inflate(inflater, container, false)

        binding.lnDownload.setOnClickListener(){
            syncData()
        }

        syncViewModel.last_download.observe(viewLifecycleOwner) { data ->
            data?.let {
                binding.txtLastDownload.text = data.toString()
            }
        }

        syncViewModel.customerCount.observe(viewLifecycleOwner){ data ->
            data?.let {
                binding.txtCustRecords.text = "$data Records"
            }
        }

        syncViewModel.inventoryCount.observe(viewLifecycleOwner){ data ->
            data?.let {
                binding.txtInventoryRecrods.text = "$data Records"
            }
        }

        binding.backBtn.setOnClickListener{
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        syncViewModel.isRestProcessing.observe(viewLifecycleOwner, Observer { data ->
            data?.let { setSyncState(it) }
        })

        //set toolbar
        val root: View = binding.root
        return root
    }

    private fun syncData(){
        setSyncState(true)
        syncViewModel.syncData()
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