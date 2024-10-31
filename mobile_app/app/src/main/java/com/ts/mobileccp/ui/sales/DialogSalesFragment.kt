package com.ts.mobileccp.ui.sales

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.ts.mobileccp.adapter.OrderFinishAdapter
import com.ts.mobileccp.adapter.OrderFinishListener
import com.ts.mobileccp.databinding.FragmentDialogSalesorderSaveBinding
import com.ts.mobileccp.db.entity.SalesOrder
import com.ts.mobileccp.db.entity.SalesOrderItem
import com.ts.mobileccp.db.entity.TmpSalesOrder
import com.ts.mobileccp.db.entity.TmpSalesOrderItem
import com.ts.mobileccp.global.AppVariable
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

class DialogSalesFragment(
    private val salesOrder: TmpSalesOrder,
    private val soItemList: MutableList<TmpSalesOrderItem>,
    private val dialogListener: DialogSalesListener
) : BottomSheetDialogFragment(), OrderFinishListener {
    private var _binding: FragmentDialogSalesorderSaveBinding? = null
    private lateinit var objSO : SalesOrder
    private lateinit var objSOItems : List<SalesOrderItem>

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>

    private var dpp = 0.0
    private var ppn = 0.0
    private var total = 0.0

    private var latitude : Double?=null
    private var longitude  : Double?=null
    private val isVisit : Boolean = soItemList.size==0


    private val binding get() = _binding!!
    private lateinit var salesViewModel: SalesViewModel;
    val adapter = OrderFinishAdapter(soItemList, this)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                setLastLocation()
            } else {
                Toast.makeText(requireContext(), "Location permission denied", Toast.LENGTH_SHORT).show()
            }
        }
        checkLocationPermissions()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val factory = SalesViewModelFactory(requireActivity().application)
        salesViewModel = ViewModelProvider(this, factory).get(SalesViewModel::class.java)
        salesViewModel.dialogSaveState.value = false

        salesViewModel.dialogSaveState.observe(viewLifecycleOwner){ result->
            if (result == true){
                dialogListener.onSaveSuccess()
                this.dismiss()
            }
        }

        _binding = FragmentDialogSalesorderSaveBinding.inflate(inflater, container, false)
        binding.rvDetailOrder.layoutManager = LinearLayoutManager(requireContext())
        binding.rvDetailOrder.adapter = adapter

        initData()

        binding.btnCancel.setOnClickListener{
            this.dismiss()
        }

        binding.btnSave.setOnClickListener{
            this.saveData()
        }

        val root: View = binding.root
        return root
    }


    override fun onCreateDialog(savedInstanceState: Bundle?): BottomSheetDialog {
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog

        dialog.setOnShowListener { dialogInterface ->
            val bottomSheetDialog = dialogInterface as BottomSheetDialog
            val bottomSheet = bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as View
            val behavior = BottomSheetBehavior.from(bottomSheet)
            behavior.state = BottomSheetBehavior.STATE_EXPANDED

            // Set the BottomSheet to full height
            bottomSheet.layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
        }

        return dialog
    }

    override fun onDismiss(dialog: DialogInterface) {
        dialogListener.onCloseDialog(soItemList)
        super.onDismiss(dialog)
    }

    fun initData(){
        if (salesOrder.customerDelivery != null){
            binding.txtCustName.text = salesOrder.customerDelivery?.shipname
            binding.txtAddress1.text = salesOrder.customerDelivery?.shipaddress
        }

        if (isVisit){
            binding.btnSave.text = "Simpan Kunjungan"
            binding.lnSummary.visibility = View.GONE
            binding.txtTittle.text = "Detail Kunjungan"
        }

        calculateData()
    }

    override fun onUpdateQty(soItem: TmpSalesOrderItem, position: Int, increment: Int) {
        soItem.qty = soItem.qty + increment
        if (soItem.qty<0) soItem.qty = 0

        calculateData()

        adapter.notifyItemChanged(position)
    }

    fun calculateData() {
        if (isVisit) return

        dpp = 0.0
        val format = NumberFormat.getNumberInstance()
        format.maximumFractionDigits = 0
        format.minimumFractionDigits = 0
        soItemList.forEach { item ->
            dpp += (item.qty * item.price)
        }

        ppn = 0.11 * dpp
        total = 1.11 * dpp

        val dppTxt = "Rp " + format.format(dpp)
        val ppnTxt = "Rp " + format.format(ppn)
        val totalTxt = "Rp " + format.format(total)

        binding.txtDPP.text = dppTxt
        binding.txtPPN.text = ppnTxt
        binding.txtTotal.text = totalTxt
    }

    fun saveData(){

        //hanya order
        if (!isVisit) {
            this.saveToDB()
//            val builder = AlertDialog.Builder(requireContext())
//            builder.setMessage("Are you sure you want to Save?")
//                .setCancelable(false)
//                .setPositiveButton("Yes") { _, _ ->
//                    this.saveToDB()
//                }
//                .setNegativeButton("No") { dialog, _ ->
//                    dialog.dismiss()
//                }
//            val alert = builder.create()
//            alert.show()
        }else{
            showWarning("Data Item masih kosong")
//            this.saveVisitToDB()
        }
    }

    private fun saveToDB(){
        buildData()
        salesViewModel.saveOrder(objSO, objSOItems)
    }

//    private fun saveVisitToDB(){
//        val objVisit: Visit = buildDataVisit()
//
//        salesViewModel.saveVisit(objVisit)
//        dialogListener.onSaveSuccess()
//        this.dismiss()
//    }

    private fun buildData(){
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val dtFormatOrder = SimpleDateFormat("yyMMddHHmmss", Locale.getDefault())
        val loginInfo = AppVariable.loginInfo

        val orderNO : String = loginInfo.salid?.trim() + "." + dtFormatOrder.format(Date());

        val id = salesOrder.id ?: UUID.randomUUID()

        objSO = SalesOrder(
            id,
            orderNO,
            dateFormat.format(Date()),
            this.salesOrder.customerDelivery?.shipid?:0,
            loginInfo.salid?:"",
            loginInfo.areano,
            dpp,
            ppn,
            total,
            latitude,
            longitude
        )

        objSOItems = soItemList.map { obj -> SalesOrderItem(
            salesorder_id = objSO.id,
            id = 0,
            partno = obj.partno,
            qty = obj.qty,
            price = obj.price,
            discount = 0.0,
            dpp = obj.calcDPP(),
            ppn = obj.calcPPN(),
            amt = obj.calcPPN()
        ) }
    }


    private fun checkLocationPermissions() {
        when {
            ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED -> {
                setLastLocation()
            }
            else -> {
                requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }
    }

    @SuppressLint("MissingPermission")  //only called after permission granted
    private fun setLastLocation() {
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                latitude = location.latitude
                longitude = location.longitude

                binding.txtLocation.text = latitude.toString() + "," +longitude.toString()
            } else {
                Toast.makeText(requireContext(), "Unable to get location", Toast.LENGTH_SHORT).show()
            }
        }
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


}

interface DialogSalesListener{
    fun onCloseDialog(soItemList: MutableList<TmpSalesOrderItem>)
    fun onSaveSuccess()
}
