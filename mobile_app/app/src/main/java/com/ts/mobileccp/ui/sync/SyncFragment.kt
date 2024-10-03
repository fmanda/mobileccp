package com.ts.mobileccp.ui.sync

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
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

        binding.btnDownload.setOnClickListener(){
            syncData()
        }

        syncViewModel.lastUpdate.observe(viewLifecycleOwner) { data ->
            data?.let {
//                binding.txtInfoLastUpdate.text = data.toString()
            }
        }

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
//            binding.imgSync.visibility = View.GONE
            binding.pgDownload.visibility = View.VISIBLE
        }else{
//            binding.imgSync.visibility = View.VISIBLE
            binding.pgDownload.visibility = View.GONE
        }
    }



}