package com.ts.mobileccp.ui.sync

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
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

        binding.lnUpload.setOnClickListener(){
            uploadData()
        }

        sharedViewModel.last_download.observe(viewLifecycleOwner) { data ->
            data?.let {
                binding.txtLastDownload.text = data.toString()
            }
        }

        sharedViewModel.last_upload.observe(viewLifecycleOwner) { data ->
            data?.let {
                binding.txtLastUpload.text = data.toString()
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

        sharedViewModel.visittoupload.observe(viewLifecycleOwner){ data ->
            data?.let {
                binding.txtVisitToUpload.text = "$data Records"
            }
        }

        sharedViewModel.salestoupload.observe(viewLifecycleOwner){ data ->
            data?.let {
                binding.txtSalesToUpload.text = "$data Records"
            }
        }

        binding.backBtn.setOnClickListener{
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        sharedViewModel.isDownloadProcessing.observe(viewLifecycleOwner) { data ->
            data?.let { setSyncStateDownload(it) }
        }

        sharedViewModel.isUploadProcessing.observe(viewLifecycleOwner) { data ->
            data?.let { setSyncStateUpload(it) }
        }

        //set toolbar
        val root: View = binding.root
        return root
    }

    private fun syncData(){
        setSyncStateDownload(true)
        sharedViewModel.syncData()
    }

    private fun uploadData(){
        setSyncStateUpload(false)
        sharedViewModel.syncUploadData()
    }

    private fun setSyncStateDownload(processing: Boolean){
        if (processing){
            binding.imgSyncDownload.visibility = View.GONE
            binding.pgDownload.visibility = View.VISIBLE
        }else{
            binding.imgSyncDownload.visibility = View.VISIBLE
            binding.pgDownload.visibility = View.GONE
        }
    }

    private fun setSyncStateUpload(processing: Boolean){
        if (processing){
            binding.imgSyncUpload.visibility = View.GONE
            binding.pgUpload.visibility = View.VISIBLE
        }else{
            binding.imgSyncUpload.visibility = View.VISIBLE
            binding.pgUpload.visibility = View.GONE
        }
    }



}