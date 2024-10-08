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
import com.ts.mobileccp.db.entity.Customer
import com.ts.mobileccp.ui.customer.DialogCustomerFragment
import android.R as R1
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

import java.util.UUID

class VisitFragment : Fragment(), DialogCustomerFragment.DialogCustomerListener {

    private var _binding : FragmentVisitBinding? = null
    private val binding get() = _binding!!
    private lateinit var visitViewModel: VisitViewModel
    private lateinit var markAdapter: ArrayAdapter<String>
    private lateinit var schAdapter: ArrayAdapter<String>
    private var isEdit: Boolean = false
    private var latitude : Double?=null
    private var longitude  : Double?=null

    private var selectedCust : Customer? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>
    private lateinit var currentPhotoUri: Uri
    private var uuid : UUID? = null
    private val CAMERA_REQUEST_CODE = 1001



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
        _binding = FragmentVisitBinding.inflate(inflater, container, false)
        val factory = VisitViewModelFactory(requireActivity().application)
        visitViewModel = ViewModelProvider(this, factory).get(VisitViewModel::class.java)

        initData()


        return binding.root
    }

    private fun initData(){
        markAdapter = ArrayAdapter(requireContext(), R1.layout.simple_spinner_item, mutableListOf())
        markAdapter.setDropDownViewResource(R1.layout.simple_spinner_item)
        binding.spMark.adapter = markAdapter

        schAdapter = ArrayAdapter(requireContext(), R1.layout.simple_spinner_item, mutableListOf())
        schAdapter.setDropDownViewResource(R1.layout.simple_spinner_item)
        binding.spSCH.adapter = schAdapter


        visitViewModel.listCCPMark.observe(viewLifecycleOwner){ objs->
            markAdapter.clear()
            markAdapter.addAll(objs.map{ obj->
                obj.markname
            })
            markAdapter.notifyDataSetChanged()
        }

        visitViewModel.listCCPSch.observe(viewLifecycleOwner){ objs ->
            schAdapter.clear()
            schAdapter.addAll(objs.map { obj->
                obj.ccpschname
            })
            schAdapter.notifyDataSetChanged()
        }

        binding.backBtn.setOnClickListener{
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        binding.btnCancel.setOnClickListener{
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        if (!isEdit) {
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
    }

    private fun showCustomerDialog() {
        val dialog = DialogCustomerFragment()
        dialog.setOnDialogCustomerSelect(this)
        dialog.show(parentFragmentManager, "DialogCustomerFragment")
    }

    fun setCustomer(){
//        salesOrder.customer = cust
        binding.txtShipName.text = selectedCust?.shipname
        binding.txtCustomerName.text = selectedCust?.partnername
        binding.txtShipAddress.text = selectedCust?.shipaddress
        binding.txtPhone.text = selectedCust?.shipphone

    }

    override fun onSelectDialogCustomer(cust: Customer) {
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
        val contentValues = ContentValues().apply {
//            put(MediaStore.Images.Media.DISPLAY_NAME, "${System.currentTimeMillis()}.jpg")
            put(MediaStore.Images.Media.DISPLAY_NAME, "${getUUID().toString()}.jpg")
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/WatermarkedPhotos")
        }

        return requireContext().contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
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

        // Get the original file name and extension
//        val fileName = originalFile.nameWithoutExtension
//        val fileExtension = originalFile.extension
//        val editedFileName = "${fileName}_edited.$fileExtension"
//        val outputPath = File(originalFile.parent, editedFileName).absolutePath

        FileOutputStream(originalFile).use { outputStream ->
            // Compress the bitmap and write to the output stream
            val success = bitmap.compress(Bitmap.CompressFormat.JPEG, 50, outputStream)

            // Check if compression was successful
            if (success) {
                println("Image compressed successfully and saved at: $originalFilePath")
            } else {
                println("Failed to compress image.")
            }
        }
    }

}
