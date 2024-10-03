package com.ts.mobileccp.ui.customer

//import Customer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
//import com.fma.mobility.adapter.ListCustomerAdapter
import com.ts.mobileccp.databinding.FragmentCustomerUpdateBinding
import com.ts.mobileccp.db.entity.Customer
import java.util.UUID


class CustomerUpdateFragment : Fragment() {
    private var _binding: FragmentCustomerUpdateBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var custViewModel: CustomerUpdateViewModel;


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val factory = CustomerUpdateViewModelFactory(requireActivity().application)
        custViewModel = ViewModelProvider(this, factory).get(CustomerUpdateViewModel::class.java)


        _binding = FragmentCustomerUpdateBinding.inflate(inflater, container, false)


        binding.backBtn.setOnClickListener{
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }


        val root: View = binding.root
        return root
    }
}