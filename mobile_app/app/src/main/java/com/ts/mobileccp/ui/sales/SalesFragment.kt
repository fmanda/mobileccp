package com.ts.mobileccp.ui.sales

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.ts.mobileccp.R
import com.ts.mobileccp.adapter.ListMerkAdapter
import com.ts.mobileccp.adapter.InventoryPickAdapter
import com.ts.mobileccp.adapter.ProductPickListener
import com.ts.mobileccp.adapter.SelectMerkListener
import com.ts.mobileccp.databinding.FragmentSalesBinding
import com.ts.mobileccp.db.entity.Customer
import com.ts.mobileccp.db.entity.InventoryLookup
import com.ts.mobileccp.db.entity.TmpSalesOrder
import com.ts.mobileccp.db.entity.TmpSalesOrderItem
import com.ts.mobileccp.ui.customer.DialogCustomerFragment
import java.text.NumberFormat
import java.util.UUID

/**
 * A simple [Fragment] subclass.
 * Use the [SalesFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SalesFragment : Fragment(), ProductPickListener,
    DialogCustomerFragment.DialogCustomerListener, DialogSalesListener, SelectMerkListener {
    private var _binding: FragmentSalesBinding? = null

    private val binding get() = _binding!!
    private lateinit var salesViewModel: SalesViewModel;

    private var soItemList: MutableList<TmpSalesOrderItem> = mutableListOf()
    private var salesOrder = TmpSalesOrder()

    val adapter = InventoryPickAdapter(emptyList(), soItemList, this)

    private val merkAdapter = ListMerkAdapter(emptyList(), 0, this)
    private var filterQuery: String = ""
    private var filterMerk: String = ""

    private var isTradUOM: Boolean = false
    private var isEdit: Boolean = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val factory = SalesViewModelFactory(requireActivity().application)
        salesViewModel = ViewModelProvider(this, factory).get(SalesViewModel::class.java)

        val strID =  arguments?.getString("salesOrderID")
        if (!strID.isNullOrEmpty()){  //edit
            isEdit = true
            val salesOrderID =  UUID.fromString(strID)
            editData(salesOrderID)
        }
    }

    fun editData(aID:UUID){
        salesViewModel.loadTmpSalesOrder(aID)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSalesBinding.inflate(inflater, container, false)

        binding.rvOrderPick.layoutManager = LinearLayoutManager(requireContext())
        binding.rvOrderPick.adapter = adapter


        binding.rvMerk.adapter = merkAdapter
        binding.rvMerk.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)


        loadMerk();
        searchIt(getPriceLevel());

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    filterQuery = it
                    searchIt(getPriceLevel(), filterQuery, filterMerk)

                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let {
                    filterQuery = it
                    searchIt(getPriceLevel(), filterQuery, filterMerk)
                }
                return false
            }
        })

//        binding.searchView.setOnClickListener {
//            binding.searchView.onActionViewExpanded()
//        }

        binding.btnAddCustomer.setOnClickListener{
            showCustomerDialog()
        }

        binding.btnNext.setOnClickListener{
            showSalesOrderDialog()
        }

        val root: View = binding.root

        if (!isEdit) {
            if (salesOrder.customer == null) {
                showCustomerDialog()
            }
        }else{
            salesViewModel.editedSO.observe(viewLifecycleOwner) { so ->
                this.salesOrder = so
                setCustomer()
            }

            salesViewModel.editedSOItems.observe(viewLifecycleOwner){ items ->
                soItemList.clear()
                soItemList.addAll(items)
                adapter.notifyDataSetChanged()
            }
        }

        return root
    }

    //calculate qty
    private fun calculateData() {
        var dpp : Double = 0.0
        val format = NumberFormat.getNumberInstance()
        format.maximumFractionDigits = 0
        format.minimumFractionDigits = 0
        soItemList.forEach { item ->
            dpp += (item.qty * item.price)
        }

        val ppn = 0.11 * dpp
        val total = 1.11 * dpp

        val dppTxt = "Rp " + format.format(dpp)
        val ppnTxt = "Rp " + format.format(ppn)
        val totalTxt = "Rp " + format.format(total)

        binding.txtDPP.text = dppTxt
        binding.txtPPN.text = ppnTxt
        binding.txtTotal.text = totalTxt
    }

    override fun onUpdateQty(prod: InventoryLookup, position: Int, qtyIndex: Int, increment:Int) {

        val soItem = getOrAdd(prod, qtyIndex) ?: return

        soItem.qty += increment

        if (soItem.qty <=0) soItemList.remove(soItem)

        calculateData()
        adapter.notifyItemChanged(position)
    }

    private fun getOrAdd(prod: InventoryLookup, uomIdx:Int):TmpSalesOrderItem?{

        val price: Double? = prod.price

        for(tmp in soItemList){
            if (tmp.partno.equals(prod.partno)){
                return tmp
            }
        }


        val tmp = TmpSalesOrderItem(
            prod.partno,
            prod.invname,
            0,
            price ?: 0.0,
            0.0,
            0.0,
            0.0,
            0.0
        )

        soItemList.add(tmp)
        return tmp
    }

    private fun searchIt(pricelevel: Int, query: String="", merk: String ="") {
        salesViewModel.lookupProducts(pricelevel, query, merk).observe(viewLifecycleOwner, Observer { items ->
            adapter.updateData(items)
        })
    }

    private fun loadMerk() {
        salesViewModel.loadMerk().observe(viewLifecycleOwner) { items ->
            merkAdapter.updateData(items)
        }
    }

    private fun showCustomerDialog() {
        val dialog = DialogCustomerFragment()
        dialog.setOnDialogCustomerSelect(this)
        dialog.show(parentFragmentManager, "DialogCustomerFragment")
    }

    private fun showSalesOrderDialog() {
        if (salesOrder.customer==null){
            showWarning("Customer Belum dipilih, Silahkan Pilih Customer terlebih dahulu")
            return
        }


        if (soItemList.size==0){
            showWarning("Data Item masih kosong")
            return
//            showConfrimationVisit { confirmed ->
//                if (confirmed){
//                    DialogSalesFragment(this.salesOrder, this.soItemList, this )
//                        .show(parentFragmentManager, "DialogCustomerFragment")
//                }
//            }
        }else{
            DialogSalesFragment(this.salesOrder, this.soItemList, this )
                .show(parentFragmentManager, "DialogCustomerFragment")
        }

    }


    override fun onSelectDialogCustomer(cust: Customer) {
        salesOrder.customer = cust
        setCustomer()
        searchIt(getPriceLevel());
    }

    fun setCustomer(){
//        salesOrder.customer = cust
        binding.txtCustName.text = salesOrder.customer?.shipname
        binding.txtAddress1.text = salesOrder.customer?.shipaddress

    }

    fun getPriceLevel():Int{
        return salesOrder.customer?.pricelevel?:0
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onCloseDialog(soItemList: MutableList<TmpSalesOrderItem>) {
        adapter.notifyDataSetChanged()
    }

    override fun onSaveSuccess() {
//        findNavController().popBackStack(R.id.nav_sales, false)
        if (isVisit()){
            findNavController().navigate(R.id.action_nav_sales_to_nav_home)
        }else {
            findNavController().navigate(R.id.action_nav_sales_to_nav_browse_sales)
        }
    }

    override fun onSelectedMerk(position: Int, merk: String){
        merkAdapter.setSelectedMerk(position)

        if (position == 0) filterMerk = "" else filterMerk = merk

        searchIt(getPriceLevel(),filterQuery, filterMerk)

    }

    private fun showWarning(msg: String) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Warning")
        builder.setMessage(msg)
        builder.setPositiveButton("OK") { dialog, _ ->
            dialog.dismiss()
        }

        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

//    private fun showConfrimationVisit(callback: (Boolean) -> Unit) {
//        val builder = AlertDialog.Builder(requireContext())
//        builder.setTitle("Konfirmasi")
//        builder.setMessage("Tidak Item yg diorder, Apakah mau menyimpan Kunjungan saja?")
//        builder.setPositiveButton("Ya, Hanya Kunjungan") { dialog, _ ->
//            callback(true)  // Return true when user clicks "Yes"
//            dialog.dismiss()
//        }
//        builder.setNegativeButton("Tidak") { dialog, _ ->
//            callback(false) // Return false when user clicks "No"
//            dialog.dismiss()
//        }
//        builder.create().show()
//    }

    private fun isVisit():Boolean{
        return soItemList.size==0
    }

}

