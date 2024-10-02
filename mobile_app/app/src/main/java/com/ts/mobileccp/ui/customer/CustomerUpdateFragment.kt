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

        //init for debug
//        binding.txtNama.setText("Febrian M")
//        binding.txtNIK.setText("000-000-000-000")
//        binding.txtPhone.setText("0888-8888-8888")
//        binding.txtAlamat.setText("Jl Mendungan No 16B")
//        binding.txtKecamatan.setText("Kartasura")
//        binding.txtKelurahan.setText("Pabelan")


        binding.btnSave.setOnClickListener{
            try {
                val cust = Customer(
                    UUID.randomUUID(),
                    binding.txtNama.text.toString(),
                    binding.txtNIK.text.toString(),
                    binding.txtPhone.text.toString(),
                    binding.txtAlamat.text.toString(),
                    binding.txtKecamatan.text.toString(),
                    binding.txtKelurahan.text.toString(),
                    0
                )
                custViewModel.upsert(cust);
                requireActivity().onBackPressedDispatcher.onBackPressed()
            } catch(e : Exception){
                throw e
            }
        }

        val root: View = binding.root
        return root
    }
}