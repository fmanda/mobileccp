package com.ts.mobileccp.ui.login

//import Customer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.ts.mobileccp.databinding.FragmentDialogLoginsettingBinding
import com.ts.mobileccp.ui.SharedViewModel
import com.ts.mobileccp.ui.home.HomeViewModel


class DialogLoginAdvSetting(val current_api: String, val listener: DialogLoginSettingListener) : BottomSheetDialogFragment() {
    private var _binding: FragmentDialogLoginsettingBinding? = null

    private val binding get() = _binding!!
    private lateinit var loginViewModel: LoginViewModel


    // Use this interface to communicate with the host
//    private var listener: DialogLoginSettingListener? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDialogLoginsettingBinding.inflate(inflater, container, false)
        loginViewModel = ViewModelProvider(requireActivity()).get(LoginViewModel::class.java)


        binding.txtAPI.setText(current_api)

        binding.btnTest.setOnClickListener{
            listener?.onTestAPI(binding.txtAPI.text.toString())
        }

        binding.btnUpdate.setOnClickListener{
            listener?.onUpdateAPI(binding.txtAPI.text.toString())
        }

        val root: View = binding.root
        return root
    }

    override fun onStart() {
        super.onStart()
//        dialog?.window?.setLayout(
//            ViewGroup.LayoutParams.MATCH_PARENT,
//            ViewGroup.LayoutParams.MATCH_PARENT
//        )
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): BottomSheetDialog {
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog

        dialog.setOnShowListener { dialogInterface ->
            val bottomSheetDialog = dialogInterface as BottomSheetDialog
            val bottomSheet = bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as View
//            val behavior = BottomSheetBehavior.from(bottomSheet)
//            behavior.state = BottomSheetBehavior.STATE_EXPANDED

            // Set the BottomSheet to full height
//            bottomSheet.layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
        }

        return dialog
    }


    interface DialogLoginSettingListener {
        fun onTestAPI(api: String)
        fun onUpdateAPI(api: String)
    }

}
