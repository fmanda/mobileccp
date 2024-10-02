package com.ts.mobileccp.ui.home

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.ts.mobileccp.adapter.ListSalesOrderAdapter
import com.ts.mobileccp.adapter.ListSalesOrderListener
import com.ts.mobileccp.databinding.FragmentHomeBinding
import com.ts.mobileccp.db.entity.LastActivityQuery
import com.ts.mobileccp.global.AppVariable
import java.text.NumberFormat

class HomeFragment : Fragment(), ListSalesOrderListener {

    private var _binding: FragmentHomeBinding? = null
    private lateinit var homeViewModel: HomeViewModel

//    val adapter = LastActivityAdapter(emptyList())
    val adapter = ListSalesOrderAdapter(emptyList(), this, true)
    private val binding get() = _binding!!
    val format = NumberFormat.getNumberInstance()


    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val factory = HomeViewModelFactory(requireActivity().application)
        homeViewModel = ViewModelProvider(this, factory).get(HomeViewModel::class.java)

        format.maximumFractionDigits = 0
        format.minimumFractionDigits = 0

        _binding = FragmentHomeBinding.inflate(inflater, container, false)


        binding.rvActivities.layoutManager = LinearLayoutManager(requireContext())
        binding.rvActivities.adapter = adapter

        homeViewModel.isRestProcessing.observe(viewLifecycleOwner,Observer { data ->
            data?.let { setSyncState(it) }
        })

        homeViewModel.listLatestActivities.observe(viewLifecycleOwner, Observer { data ->
            data?.let { adapter.updateData(it) }
        })

        homeViewModel.todaySales.observe(viewLifecycleOwner, Observer { data ->
            data?.let {
                binding.txtTodaySales.text = format.format(data.sumSO)
                binding.txtTodayCount.text = format.format(data.countSO) + " Orders"
            }
        })

        homeViewModel.weeklySales.observe(viewLifecycleOwner, Observer { data ->
            data?.let {
                binding.txtWeeklySales.text = format.format(data.sumSO)
                binding.txtWeeklyCount.text = format.format(data.countSO) + " Orders"
            }
        })

        homeViewModel.monthlySales.observe(viewLifecycleOwner, Observer { data ->
            data?.let {
                binding.txtMonthlySales.text = format.format(data.sumSO)
                binding.txtMonthlyCount.text = format.format(data.countSO) + " Orders"
            }
        })

        homeViewModel.ordersToUpload.observe(viewLifecycleOwner){ data ->
            data?.let {
//                binding.txtInfoUpload.text = "$data Order(s) to Upload"
            }
        }

        binding.lnSync.setOnClickListener{
            syncData()
        }

        homeViewModel.lastUpdate.observe(viewLifecycleOwner) { data ->
            data?.let {
//                binding.txtInfoLastUpdate.text = data.toString()
            }
        }

        val welcome = "Hi, " + AppVariable.loginInfo.salesman
        binding.txtSalesName.text = welcome

        val desc = AppVariable.loginInfo.kode.toString() + " | Kode Cabang " + AppVariable.loginInfo.project_code
//        binding.txtIDSales.text = desc

        val root: View = binding.root
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun syncData(){
        setSyncState(true)
        homeViewModel.syncData()
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

    override fun onClick(activity: LastActivityQuery, position: Int) {
        //
    }

    override fun onEdit(activity: LastActivityQuery, position: Int) {
        //
    }

    override fun onUpload(activity: LastActivityQuery, position: Int) {
        //
    }
}