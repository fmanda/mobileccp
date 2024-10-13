package com.ts.mobileccp.ui.visit

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
import com.ts.mobileccp.adapter.ListVisitAdapter
import com.ts.mobileccp.adapter.ListVisitListener
import com.ts.mobileccp.databinding.FragmentBrowseSalesorderBinding
import com.ts.mobileccp.db.entity.LastActivityQuery
import com.ts.mobileccp.db.entity.LastVisit


class BrowseVisitFragment : Fragment(), ListVisitListener {
    private var _binding: FragmentBrowseSalesorderBinding? = null

    private val binding get() = _binding!!
    private lateinit var visitViewModel: VisitViewModel;
    val adapter = ListVisitAdapter(emptyList(), this)


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val factory = VisitViewModelFactory(requireActivity().application)
        visitViewModel = ViewModelProvider(this, factory).get(VisitViewModel::class.java)


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
            visitViewModel.listLatestVisits.observe(viewLifecycleOwner, Observer { data ->
                data?.let { adapter.updateData(it) }
            })
        }else{
            visitViewModel.searchLatestVisits(query).observe(viewLifecycleOwner, Observer { items ->
                adapter.updateData(items)
            })
        }

    }

    override fun onClick(visit: LastVisit, position: Int) {
        visit.isexpanded = !visit.isexpanded
        adapter.notifyItemChanged(position)
    }

    override fun onEdit(visit: LastVisit, position: Int) {
        val args = Bundle().apply {
            putString("visitID", visit.id.toString())
        }
        findNavController().navigate(R.id.action_nav_browse_visit_to_nav_visit, args)
    }

    override fun onUpload(visit: LastVisit, position: Int) {
        visitViewModel.syncData(visit.id)
    }

}