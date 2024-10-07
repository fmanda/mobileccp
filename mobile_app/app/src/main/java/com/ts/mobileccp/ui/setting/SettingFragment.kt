package com.ts.mobileccp.ui.setting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.ts.mobileccp.databinding.FragmentSettingBinding

/**
 * A simple [Fragment] subclass.
 * Use the [SettingFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SettingFragment : Fragment() {
    private var _binding: FragmentSettingBinding? = null


    private val binding get() = _binding!!
    private lateinit var settingViewModel: SettingViewModel;


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val factory = SettingViewModelFactory(requireActivity().application)
        settingViewModel = ViewModelProvider(this, factory).get(SettingViewModel::class.java)
        _binding = FragmentSettingBinding.inflate(inflater, container, false)

        binding.btnLogOut.setOnClickListener(){
            logOut()
        }

        //set toolbar
        val root: View = binding.root
        return root
    }

    private fun logOut(){
        val builder = AlertDialog.Builder(requireContext())
        builder.setMessage("Anda Yakin akan Log Out?")
            .setCancelable(false)
            .setPositiveButton("Yes") { _, _ ->
                this.doLogout()
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
        val alert = builder.create()
        alert.show()
    }

    private fun doLogout(){
        settingViewModel.logOut(requireContext())
    }

}