package com.ts.mobileccp.ui.visit

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import com.ts.mobileccp.databinding.FragmentVisitBinding
import com.ts.mobileccp.db.entity.CustomerDelivery
import com.ts.mobileccp.ui.customer.DialogCustomerFragment
import androidx.core.app.ActivityCompat
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.ts.mobileccp.R
import com.ts.mobileccp.adapter.ListARInvAdapter
import com.ts.mobileccp.db.entity.Visit
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

import java.util.UUID

class VisitFragment : Fragment(), DialogCustomerFragment.DialogCustomerListener {

    private var _binding : FragmentVisitBinding? = null
    private val binding get() = _binding!!
    private lateinit var visitViewModel: VisitViewModel
    private lateinit var markAdapter: ArrayAdapter<String>
    private lateinit var schAdapter: ArrayAdapter<String>
    private var isEdit: Boolean = false
    private var isCustomerByID: Boolean = false
    private var latitude : Double?=null
    private var longitude  : Double?=null

    private var selectedCust : CustomerDelivery? = null
    private var selectedVisitMark: Int = 0
    private var selectedPlanMark: Int = 0

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>
    private lateinit var currentPhotoUri: Uri
    private var uuid : UUID? = null
    private var img_uri : String? = null

    private val adapter = ListARInvAdapter(emptyList())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val factory = VisitViewModelFactory(requireActivity().application)

        visitViewModel = ViewModelProvider(this, factory).get(VisitViewModel::class.java)

        visitViewModel.dialogSaveState.value = false
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                setLastLocation()
            } else {
                Toast.makeText(requireContext(), "Location permission denied", Toast.LENGTH_SHORT).show()
            }
        }
        checkLocationPermissions()

        val strID =  arguments?.getString("visitID")
        if (!strID.isNullOrEmpty()){  //edit
            isEdit = true
            val visitID =  UUID.fromString(strID)
            editData(visitID)
        }

        val custID =  arguments?.getInt("customerID")

        if ((custID ?: 0) > 0) {
            isCustomerByID = true
            visitByCustomerID(custID!!)
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentVisitBinding.inflate(inflater, container, false)


        initData()


        return binding.root
    }

    private fun editData(aID:UUID){
        visitViewModel.loadVisit(aID)
    }

    private fun visitByCustomerID(customerID:Int){
        visitViewModel.visitByCustomer(customerID)
    }

    private fun initData(){
        markAdapter = ArrayAdapter(requireContext(), R.layout.spinner_selected_item, mutableListOf())
        markAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
        binding.spMark.adapter = markAdapter

        schAdapter = ArrayAdapter(requireContext(), R.layout.spinner_selected_item, mutableListOf())
        schAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
        binding.spSCH.adapter = schAdapter


        visitViewModel.listVisitMark.observe(viewLifecycleOwner){ objs->
            markAdapter.clear()
            markAdapter.addAll(objs.map{ obj->
                obj.markname
            })
            markAdapter.notifyDataSetChanged()

            updateComboBox()
        }

        visitViewModel.listPlanMark.observe(viewLifecycleOwner){ objs ->
            schAdapter.clear()
            schAdapter.addAll(objs.map { obj->
                obj.markname
            })
            schAdapter.notifyDataSetChanged()

            updateComboBox()
        }

        binding.backBtn.setOnClickListener{
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        binding.btnCancel.setOnClickListener{
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }



        if (isEdit){
            visitViewModel.visit.observe(viewLifecycleOwner) { obj ->
                if (obj == null) return@observe
                setVisit(obj)
            }
            visitViewModel.customerDelivery.observe(viewLifecycleOwner) { cust ->
                if (cust == null) return@observe
                this.selectedCust = cust
                setCustomer()
            }
        }else if (isCustomerByID) {
            visitViewModel.customerDelivery.observe(viewLifecycleOwner) { cust ->
                if (cust == null) return@observe
                this.selectedCust = cust
                setCustomer()
            }
        }else{
            if (selectedCust == null) {
                showCustomerDialog()
            }
        }

        binding.txtShipName.setOnClickListener(){
            showCustomerDialog()
        }

        binding.imgView.setOnClickListener(){
            openCamera()
        }

        binding.btnSave.setOnClickListener(){
            saveData()
        }

        visitViewModel.dialogSaveState.observe(viewLifecycleOwner){ result ->
            if (result == true) {
                Toast.makeText(requireContext(), "Data berhasil disimpan", Toast.LENGTH_LONG).show()
                findNavController().navigate(R.id.action_nav_visit_to_nav_home)
            }
        }

        binding.rvARInv.layoutManager = LinearLayoutManager(requireContext())
        binding.rvARInv.adapter = adapter
    }

    private fun showCustomerDialog() {
        val dialog = DialogCustomerFragment()
        dialog.setOnDialogCustomerSelect(this)
        dialog.show(parentFragmentManager, "DialogCustomerFragment")
    }

    private fun setCustomer(){
        binding.txtShipName.text = selectedCust?.shipname
        binding.txtCustomerName.text = selectedCust?.partnername
        binding.txtShipAddress.text = selectedCust?.shipaddress
        binding.txtPhone.text = selectedCust?.shipphone

        visitViewModel.getARInvByCustomer(selectedCust?.partnerid?:0).observe(viewLifecycleOwner){ data ->
            data?.let{
                adapter.updateData(data)
            }
        }
    }

    fun setVisit(visit: Visit){
        uuid = visit.id
        selectedPlanMark = 0; //visit.planmark_id get from plan
        selectedVisitMark = visit.visitmark_id
        img_uri = visit.img_uri


        updateComboBox()
        visit.img_uri?.let{
            loadPhotosFromUri(visit.img_uri)
        }
    }

    fun updateComboBox(){
        val idxSCH = visitViewModel.listPlanMark.value?.indexOfFirst { it.id == selectedPlanMark }
        binding.spSCH.setSelection(idxSCH?:0)

        val idxMark = visitViewModel.listVisitMark.value?.indexOfFirst { it.id == selectedVisitMark }
        binding.spMark.setSelection(idxMark?:0)
    }

    override fun onSelectDialogCustomer(cust: CustomerDelivery) {
        this.selectedCust = cust
        setCustomer()
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
                val latlng = latitude.toString() + "," +longitude.toString()

                binding.txtLatLng.text = latlng
            } else {
                Toast.makeText(requireContext(), "Unable to get location", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getBitmapFromUri(uri: Uri): Bitmap? {
        return try {
            requireContext().contentResolver.openInputStream(uri).use { inputStream ->
                BitmapFactory.decodeStream(inputStream)
            }
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    private val capturePhotoResult = registerForActivityResult(ActivityResultContracts.TakePicture()) { success: Boolean ->
        if (success) {
            try {
                // Get the captured image as a bitmap
                val bitmap = getBitmapFromUri(currentPhotoUri)


                if (bitmap != null) {
                    // Resize the image to 720p (1280x720)
                    val originalWidth = bitmap.width
                    val originalHeight = bitmap.height
                    val aspectRatio = originalWidth.toFloat() / originalHeight.toFloat()
                    val targetHeight = 720
                    val targetWidth = (targetHeight * aspectRatio).toInt()

                    val resizedBitmap = resizeBitmap(bitmap, targetWidth, targetHeight)

                    // Add watermark with geolocation
                    val watermarkedBitmap = addWatermark(
                        resizedBitmap,
                        (binding.txtLatLng.text ?: "Unknown location").toString()
                    )

                    // Save the watermarked image back to the original URI
                    saveImageToUri(watermarkedBitmap, currentPhotoUri)

                    // Display the watermarked image
                    binding.imgView.setImageBitmap(watermarkedBitmap)
                }

            } catch (e: IOException) {
                e.printStackTrace()
                Toast.makeText(requireContext(), "Failed to process the image", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(requireContext(), "Photo capture failed", Toast.LENGTH_SHORT).show()
        }
    }

    private fun openCamera() {
        when {
            ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED -> {
                val photoUri = createImageUri() ?: return
                currentPhotoUri = photoUri

                // Launch the camera using the ActivityResult API
                capturePhotoResult.launch(photoUri)
            }
            else -> {
                requestPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }


    private fun createImageUri(): Uri? {
        val fileName = "${getUUID().toString()}.jpg"// Change this if needed
        val projection = arrayOf(MediaStore.Images.Media._ID)
        val selection = "${MediaStore.Images.Media.DISPLAY_NAME} = ?"
        val selectionArgs = arrayOf(fileName)

        // Query to check if the image already exists
        val cursor = requireContext().contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            selectionArgs,
            null
        )

        return if (cursor != null && cursor.moveToFirst()) {
            // Image exists, retrieve its URI
            val id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID))
            Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id.toString())
        } else {
            // Image does not exist, create a new URI
            val contentValues = ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
                put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/CCPPhotos") // Ensure the folder exists
            }
            requireContext().contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
        }.also {
            cursor?.close()
        }
    }

    private fun getRealPathFromUri(uri: Uri): String? {
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = requireContext().contentResolver.query(uri, projection, null, null, null)

        cursor?.use {
            val columnIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            if (it.moveToFirst()) {
                return it.getString(columnIndex)
            }
        }
        return null
    }


    fun loadPhotosFromUri(imguri:String) {
        val uri = Uri.parse(imguri)
        try {
            val bitmap = requireActivity().contentResolver.openInputStream(uri)?.use { inputStream ->
                BitmapFactory.decodeStream(inputStream)
            }
            binding.imgView.setImageBitmap(bitmap)
        } catch (e: Exception) {

        }
    }

    private fun getUUID():UUID{
        if (uuid == null){
            uuid = UUID.randomUUID()
        }
        return uuid!!
    }

    private fun addWatermark(originalBitmap: Bitmap, watermarkText: String): Bitmap {
        // Create a mutable copy of the original bitmap
        val mutableBitmap = originalBitmap.copy(Bitmap.Config.ARGB_8888, true)

        // Prepare the canvas to draw on the bitmap
        val canvas = Canvas(mutableBitmap)
        val paint = Paint()

        // Set the watermark text style (color, size, etc.)
        paint.color = Color.RED
        paint.textSize = 30f
        paint.isAntiAlias = true
        paint.alpha = 200 // Slight transparency for the watermark

        // Draw the watermark on the bottom-right corner of the image
        canvas.drawText(watermarkText, 20f, mutableBitmap.height - 20f, paint)

        return mutableBitmap
    }

    private fun resizeBitmap(originalBitmap: Bitmap, targetWidth: Int, targetHeight: Int): Bitmap {
        return Bitmap.createScaledBitmap(originalBitmap, targetWidth, targetHeight, true)
    }


    private fun saveImageToUri(bitmap: Bitmap, uri: Uri) {
        val originalFilePath = getRealPathFromUri(uri) ?: return
        val originalFile = File(originalFilePath)

        FileOutputStream(originalFile).use { outputStream ->
            val success = bitmap.compress(Bitmap.CompressFormat.JPEG, 50, outputStream)
            if (success) {
                println("Image compressed successfully and saved at: $originalFilePath")
                img_uri = uri.toString()
            } else {
                println("Failed to compress image.")
            }
        }
    }

    private fun buildVisitObj():Visit{
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val markidx = binding.spMark.selectedItemPosition?:0
        val visitMarkID = visitViewModel.listVisitMark.value?.get(markidx)?.id?:0

        val planMarkID = binding.spSCH.selectedItemPosition?:0
        val sch = visitViewModel.listPlanMark.value?.get(planMarkID)?.id?:0

        val visitno = "generate"
        val visitplan = "tbd_later"
        val notes = binding.txtNotes.text.toString()

        return Visit(
            getUUID(),
            selectedCust?.shipid?:0,
            visitno,
            dateFormat.format(Date()),
            visitMarkID,
            visitplan,
            notes,
            latitude,
            longitude,
            0,
            img_uri
        )
    }

    private fun saveData(){
        val visit = buildVisitObj()
        visitViewModel.saveVisit(visit)
    }
}
