package com.ts.mobileccp.ui.sales

//import Customer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.ts.mobileccp.R
import com.ts.mobileccp.adapter.ListSalesOrderAdapter
import com.ts.mobileccp.adapter.ListSalesOrderListener
import com.ts.mobileccp.databinding.FragmentBrowseSalesorderBinding
import com.ts.mobileccp.db.entity.LastActivityQuery


class BrowseSalesFragment : Fragment(), ListSalesOrderListener {
    private var _binding: FragmentBrowseSalesorderBinding? = null

    private val binding get() = _binding!!
    private lateinit var salesViewModel: SalesViewModel;
    val adapter = ListSalesOrderAdapter(emptyList(), this)


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val factory = SalesViewModelFactory(requireActivity().application)
        salesViewModel = ViewModelProvider(this, factory).get(SalesViewModel::class.java)


        _binding = FragmentBrowseSalesorderBinding.inflate(inflater, container, false)

//
        binding.rvListOrder.layoutManager = LinearLayoutManager(requireContext())

        binding.rvListOrder.adapter = adapter


        searchIt();


        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    searchIt(it)
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let {
                    searchIt(it)
                }
                return false
            }
        })

        val root: View = binding.root
        return root
    }

    private fun searchIt(query: String="") {
        if (query.isEmpty()){
            salesViewModel.listLatestActivities.observe(viewLifecycleOwner, Observer { data ->
                data?.let { adapter.updateData(it) }
            })
        }else{
            salesViewModel.searchLatestOrder(query).observe(viewLifecycleOwner, Observer { items ->
                adapter.updateData(items)
            })
        }

    }

    override fun onClick(activity: LastActivityQuery, position: Int) {
        activity.isexpanded = !activity.isexpanded
        adapter.notifyItemChanged(position)
    }

    override fun onEdit(activity: LastActivityQuery, position: Int) {
        val args = Bundle().apply {
            putString("salesOrderID", activity.id.toString())
        }
        findNavController().navigate(R.id.action_nav_browse_sales_to_nav_sales, args)
    }

    override fun onUpload(activity: LastActivityQuery, position: Int) {
        salesViewModel.syncData(activity.id)
    }

}