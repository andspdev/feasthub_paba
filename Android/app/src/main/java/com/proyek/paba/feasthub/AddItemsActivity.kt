package com.proyek.paba.feasthub

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import androidx.core.view.isVisible
import com.proyek.paba.feasthub.api.URL
import com.proyek.paba.feasthub.api.add_items.APIService
import com.proyek.paba.feasthub.api.add_items.ResponseAdd
import com.proyek.paba.feasthub.permission.ManagePermissions
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Callback
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File

class AddItemsActivity : AppCompatActivity() {
    private val uploadGallery = 1000
    private val PermissionsRequestCode = 1001
    private lateinit var managePermissions: ManagePermissions

    private lateinit var _imageUpload: ImageView
    private lateinit var _linearThumbnail: LinearLayout
    private lateinit var _tvDeleteThumb: TextView
    private lateinit var _btnSelectedImage: ImageButton
    private var selectedImageBody: MultipartBody.Part? = null


    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_items)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        val _inputName: EditText = findViewById(R.id.inputName)
        val _inputHarga: EditText = findViewById(R.id.inputHarga)
        val _inputUkuran: EditText = findViewById(R.id.inputUkuran)
        val _inputDesc: EditText = findViewById(R.id.inputDesc)
        val _inputSatuan: EditText = findViewById(R.id.inputSatuan)
        val _inputKategori: Spinner = findViewById(R.id.inputKategori)
        val _btnSubmit: Button = findViewById(R.id.btnSubmit)

        _imageUpload = findViewById(R.id.imageUpload)
        _linearThumbnail = findViewById(R.id.linearThumbnail)
        _tvDeleteThumb = findViewById(R.id.tvDeleteThumb)

        _btnSelectedImage = findViewById(R.id.btnSelectedImage)

        val listPermission = listOf(
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.READ_MEDIA_IMAGES,
        )

        managePermissions = ManagePermissions(this@AddItemsActivity, listPermission, PermissionsRequestCode)

        _btnSelectedImage.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED
            ) {
                managePermissions.checkPermissions()
            }
            else {
                this.pickImageFromGallery()
            }
        }


        _tvDeleteThumb.setOnClickListener {
            AlertDialog.Builder(this@AddItemsActivity)
                .setTitle("Delete Thumbnail?")
                .setMessage("Are you sure you want to delete the thumbnail?")
                .setPositiveButton("Yes") { dialog, _ ->
                    dialog.dismiss()

                    _imageUpload.setImageURI(null)

                    _linearThumbnail.isGone = true
                    _tvDeleteThumb.isVisible = false
                    _btnSelectedImage.isGone = false

                    selectedImageBody = null
                }
                .setNegativeButton("Cancel") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }

        var kategoriSelected = "Pilih kategori: "
        val itemsKategori = arrayOf(kategoriSelected, "Drinks", "Food", "Cake", "Ice Cream", "Snacks", "Vegetable", "Meal")

        val adapterKategori = ArrayAdapter(this@AddItemsActivity, android.R.layout.simple_spinner_item, itemsKategori)
        adapterKategori.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        _inputKategori.adapter = adapterKategori


        _inputKategori.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parentView: AdapterView<*>?, selectedItemView: android.view.View?, position: Int, id: Long) {
                var selectedValue = itemsKategori[position]

                if (selectedValue == "Ice Cream") {
                    selectedValue = "ice_cream"
                }

                kategoriSelected = selectedValue.lowercase()
            }

            override fun onNothingSelected(parentView: AdapterView<*>?) {
            }
        }


        _btnSubmit.setOnClickListener {
            var error = ""
            if (_inputName.text.toString().trim() == "") {
                error = "Nama items belum di input."
            }
            else if (_inputHarga.text.toString().trim() == "") {
                error = "Harga items belum di input."
            }
            else if (_inputUkuran.text.toString().trim() == "") {
                error = "Ukuran items belum di input."
            }
            else if (_inputSatuan.text.toString().trim() == "") {
                error = "Satuan items belum di input."
            }
            else if (_inputDesc.text.toString().trim() == "") {
                error = "Description items belum di input."
            }
            else if (kategoriSelected == "Pilih kategori: ") {
                error = "Silakan pilih kategori item."
            }
            else if (selectedImageBody == null) {
                error = "Silakan pilih thumbnail item."
            }

            if (error != "") {
                Toast.makeText(this@AddItemsActivity, error, Toast.LENGTH_LONG).show()
            }
            else {
                _btnSubmit.isEnabled = false

                val retrofit = Retrofit.Builder()
                    .baseUrl(URL().urlApi)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()

                val apiService = retrofit.create(APIService::class.java)

                val namaItem = RequestBody.create(MediaType.parse("text/plain"), _inputName.text.toString())
                val kategori = RequestBody.create(MediaType.parse("text/plain"), kategoriSelected)
                val deskripsi = RequestBody.create(MediaType.parse("text/plain"), _inputDesc.text.toString())
                val harga = RequestBody.create(MediaType.parse("text/plain"), _inputHarga.text.toString())
                val ukuran = RequestBody.create(MediaType.parse("text/plain"), _inputUkuran.text.toString())
                val satuan = RequestBody.create(MediaType.parse("text/plain"), _inputSatuan.text.toString())

                val call = apiService.createPost(
                    namaItem,
                    kategori,
                    deskripsi,
                    harga,
                    ukuran,
                    satuan,
                    selectedImageBody!!
                )

                call.enqueue(object : Callback<ResponseAdd> {
                    override fun onResponse(call: retrofit2.Call<ResponseAdd>, response: retrofit2.Response<ResponseAdd>) {
                        if (response.isSuccessful) {
                            val createdData = response.body()

                            if (createdData != null) {
                                if (!createdData.success && createdData.error != "") {
                                    Toast.makeText(
                                        this@AddItemsActivity,
                                        createdData.error,
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                                else {
                                    _inputName.setText("")

                                    kategoriSelected = "Pilih kategori: "
                                    _inputKategori.setSelection(0)

                                    _inputDesc.setText("")
                                    _inputHarga.setText("")
                                    _inputUkuran.setText("")
                                    _inputSatuan.setText("")

                                    _imageUpload.setImageURI(null)
                                    selectedImageBody = null

                                    _linearThumbnail.isGone = true
                                    _tvDeleteThumb.isVisible = false
                                    _btnSelectedImage.isGone = false


                                    val popupCart: View = LayoutInflater.from(this@AddItemsActivity).inflate(R.layout.popup, null)
                                    val setPopUpCart = androidx.appcompat.app.AlertDialog.Builder(this@AddItemsActivity)
                                        .setView(popupCart)
                                        .create()

                                    setPopUpCart.window!!.attributes.windowAnimations = R.style.animPopUp
                                    val _tvPopUp : TextView = popupCart.findViewById(R.id.isiTextPopUp)
                                    _tvPopUp.text = "Successfully added product items."

                                    setPopUpCart.show()

                                    val durationInMillis: Long = 2500

                                    val countDownTimer: CountDownTimer = object : CountDownTimer(durationInMillis, 1000) {
                                        override fun onTick(millisUntilFinished: Long) {}

                                        override fun onFinish() {
                                            setPopUpCart.dismiss()
                                        }
                                    }

                                    countDownTimer.start()
                                }
                            }
                        }

                        else {
                            Toast.makeText(this@AddItemsActivity, "Upss! Error: ${response.code()}", Toast.LENGTH_LONG).show()
                        }

                        _btnSubmit.isEnabled = true
                    }


                    override fun onFailure(call: retrofit2.Call<ResponseAdd>, t: Throwable) {
                        Toast.makeText(this@AddItemsActivity, "${t.message}", Toast.LENGTH_LONG).show()
                        Log.d("UPLOAD_FILE", t.message.toString())
                        _btnSubmit.isEnabled = true
                    }
                })
            }
        }
    }

    private fun getRealPathFromUri(uri: Uri): String {
        val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = contentResolver.query(uri, filePathColumn, null, null, null)

        if (cursor != null) {
            cursor.moveToFirst()
            val columnIndex = cursor.getColumnIndex(filePathColumn[0])
            val picturePath = cursor.getString(columnIndex)
            cursor.close()
            return picturePath
        }

        return ""
    }

    private fun isImageSquare(uri: Uri): Boolean {
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true

        try {
            BitmapFactory.decodeStream(contentResolver.openInputStream(uri), null, options)
            val imageWidth = options.outWidth
            val imageHeight = options.outHeight

            return imageWidth == imageHeight
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return false
    }

    private fun getFileSize(uri: Uri): Long {
        val cursor = contentResolver.query(uri, null, null, null, null)
        val sizeIndex = cursor?.getColumnIndex(OpenableColumns.SIZE) ?: 0
        cursor?.moveToFirst()
        val size = cursor?.getLong(sizeIndex) ?: 0
        cursor?.close()
        return size
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == uploadGallery && resultCode == Activity.RESULT_OK && data != null) {
            val chooseImage: Uri? = data.data

            if (chooseImage != null) {
                val fileSizeInBytes = getFileSize(chooseImage)
                val fileSizeInMB = fileSizeInBytes / (1024 * 1024).toFloat()

                if (isImageSquare(chooseImage)) {
                    if (fileSizeInMB <= 2) {
                        val file = File(getRealPathFromUri(chooseImage))

                        val requestFile =
                            RequestBody.create(MediaType.parse("multipart/form-data"), file)
                        val body =
                            MultipartBody.Part.createFormData("thumbnail", file.name, requestFile)

                        _linearThumbnail.isGone = false
                        _tvDeleteThumb.isVisible = true
                        _btnSelectedImage.isGone = true
                        selectedImageBody = body

                        _imageUpload.setImageURI(chooseImage)
                    }
                    else {
                        Toast.makeText(this@AddItemsActivity, "The maximum thumbnail file size is 2 MB", Toast.LENGTH_LONG).show()
                    }
                }
                else {
                    Toast.makeText(this@AddItemsActivity, "The ratio of uploaded images must be 1:1", Toast.LENGTH_LONG).show()
                }
            }
            else {
                Toast.makeText(this@AddItemsActivity, "Failed to select image, please try again.", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun pickImageFromGallery() {
        val iGallery =
            Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(iGallery, uploadGallery)
    }
}