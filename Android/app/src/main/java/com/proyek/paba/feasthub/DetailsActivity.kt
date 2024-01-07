package com.proyek.paba.feasthub

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.core.view.isGone
import androidx.core.view.isVisible
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.proyek.paba.feasthub.api.URL
import com.proyek.paba.feasthub.api.details.APIService
import com.proyek.paba.feasthub.api.wishlist.APIService as Wishlist
import com.proyek.paba.feasthub.api.details.ResponseGetDetails
import com.proyek.paba.feasthub.api.details.DetailCart
import com.proyek.paba.feasthub.api.signin.DetailUser
import com.proyek.paba.feasthub.api.wishlist.DeleteResponse
import com.proyek.paba.feasthub.api.wishlist.InsertResponse
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class DetailsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)

        val _totalPrice : TextView = findViewById(R.id.totalPrice)
        val _total : TextView = findViewById(R.id.total)
        val _btnTambah : Button = findViewById(R.id.btnPlus)
        val _btnMinus : Button = findViewById(R.id.btnMinus)
        val _imgBack : ImageView = findViewById(R.id.imgBack)
        val _wishlist_kosong : ImageView = findViewById(R.id.wishlist_kosong)
        val _progressBar: ProgressBar = findViewById(R.id.progressBarCategories)
        val _scrollView : ScrollView = findViewById(R.id.scrollView2)
        val _frameAddToCart : FrameLayout = findViewById(R.id.frameAddToCart)
        val _frameAdd : FrameLayout = findViewById(R.id.frameAdd)

        val _tvFoto : ImageView = findViewById(R.id.tvFoto)
        val _tvJudul : TextView = findViewById(R.id.tvJudul)
        val _tvBerat : TextView = findViewById(R.id.tvBerat)
        val _tvDesc : TextView = findViewById(R.id.tvDesc)
        val _tvCart : ImageView = findViewById(R.id.tvCart)

        var user_id = ""
        val dataIntent = intent.getStringExtra("item_id").toString()
        Log.d("CekDataIntent", dataIntent)

        val sp = this.getSharedPreferences("user_login_detail", Context.MODE_PRIVATE)
        val gson = Gson()
        val getJson= sp.getString("detail", null)
        val cekLogin = sp.contains("detail")

        if (cekLogin) {
            Log.d("CekDataGetJSON", getJson.toString())
            val jsonToString = gson.fromJson(getJson, DetailUser::class.java)
            Log.d("CekDataJSON", jsonToString.toString())

            user_id = jsonToString.id
        }

        val retrofit = Retrofit.Builder()
            .baseUrl(URL().urlApi)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiServiceDetail = retrofit.create(APIService::class.java)

        val callDetails = apiServiceDetail.getDetails(dataIntent, user_id)

        totalCart()

        _tvCart.setOnClickListener {
            if (!cekLogin) {
                Toast.makeText(
                    this@DetailsActivity,
                    "Please log in to your account first.",
                    Toast.LENGTH_LONG
                ).show()
            }
            else {
                startActivity(Intent(this@DetailsActivity, ShoppingCartActivity::class.java))
            }
        }

        callDetails.enqueue(object : Callback<ResponseGetDetails> {
            override fun onResponse(call: Call<ResponseGetDetails>, response: Response<ResponseGetDetails>) {
                if (response.isSuccessful) {
                    val data = response.body()
                    if (data != null) {
                        if (data.detail.id.toString() == "") {
                            _scrollView.isGone = false
                            _frameAdd.isGone = false
                            _frameAddToCart.isGone = false
                        } else {

                            _scrollView.isVisible = true
                            _frameAdd.isVisible = true
                            _frameAddToCart.isVisible = true

                            _totalPrice.text = "Rp${data.detail.harga}"

                            val imageRes = data.detail.thumbnail
                            Picasso.get().load(imageRes).into(_tvFoto)

                            _tvJudul.text = data.detail.nama_item
                            _tvBerat.text = data.detail.ukuran
                            _tvDesc.text = data.detail.deskripsi

                            var finalTotal: Int
                            var finalPrice = 0

                            _btnTambah.setOnClickListener {
                                if (Integer.parseInt(_total.text.toString()) < 100) {
                                    finalTotal = Integer.parseInt(_total.text.toString()) + 1
                                    finalPrice = finalTotal * data.detail.harga
                                    _totalPrice.text = "Rp$finalPrice"
                                    _total.text = finalTotal.toString()
                                }
                            }

                            _btnMinus.setOnClickListener {
                                if (Integer.parseInt(_total.text.toString()) != 1) {
                                    finalTotal = Integer.parseInt(_total.text.toString()) - 1
                                    finalPrice -= data.detail.harga
                                    _totalPrice.text = "Rp$finalPrice"
                                    _total.text = finalTotal.toString()
                                }
                            }

                            var wishlistAdd = true
                            if (data.detail.is_wishlist) {
                                wishlistAdd = false
                                _wishlist_kosong.setImageResource(R.drawable.wishlist)
                            }


                            // Wishlist
                            _wishlist_kosong.setOnClickListener {
                                val apiServiceUpdate = retrofit.create(Wishlist::class.java)

                                if (cekLogin) {
                                    if (wishlistAdd) {
                                        val callUpdate = apiServiceUpdate.insert(dataIntent, user_id)

                                        callUpdate.enqueue(object : Callback<InsertResponse> {
                                            override fun onResponse(
                                                call: Call<InsertResponse>,
                                                response: Response<InsertResponse>) {
                                                if (response.isSuccessful) {
                                                    val dataWishlist = response.body()

                                                    if (dataWishlist != null) {
                                                        if (dataWishlist.success) {
                                                            _wishlist_kosong.setImageResource(R.drawable.wishlist)
                                                            wishlistAdd = false
                                                            popup(0)

                                                            Log.d("cekWishlist", "MASUKKK")
                                                        }
                                                    } else {
                                                        Toast.makeText(
                                                            this@DetailsActivity,
                                                            "An error occurred while adding to wishlist.",
                                                            Toast.LENGTH_LONG
                                                        ).show()
                                                    }
                                                } else {
                                                    Toast.makeText(
                                                        this@DetailsActivity,
                                                        "Upss! Error: ${response.code()}",
                                                        Toast.LENGTH_LONG
                                                    ).show()
                                                }
                                            }
                                            override fun onFailure(
                                                call: Call<InsertResponse>,
                                                t: Throwable
                                            ) {
                                                Toast.makeText(
                                                    this@DetailsActivity,
                                                    "${t.message}",
                                                    Toast.LENGTH_LONG
                                                ).show()
                                            }
                                        })
                                    }
                                    else {
                                        val callDelete = apiServiceUpdate.delete(dataIntent, user_id)
                                        callDelete.enqueue(object : Callback<DeleteResponse> {
                                            override fun onResponse(
                                                call: Call<DeleteResponse>,
                                                response: Response<DeleteResponse>) {

                                                if (response.isSuccessful) {
                                                    val dataWishlist = response.body()

                                                    if (dataWishlist != null) {
                                                        if (dataWishlist.success) {
                                                            _wishlist_kosong.setImageResource(R.drawable.wishlist_blank)
                                                            wishlistAdd = true
                                                            popup(1)

                                                            Log.d("cekWishlist", "MASUKKK")
                                                        }
                                                    }
                                                    else {
                                                        Toast.makeText(
                                                            this@DetailsActivity,
                                                            "An error occurred while deleting the wishlist.",
                                                            Toast.LENGTH_LONG
                                                        ).show()
                                                    }
                                                }
                                                else {
                                                    Toast.makeText(
                                                        this@DetailsActivity,
                                                        "Upss! Error: ${response.code()}",
                                                        Toast.LENGTH_LONG
                                                    ).show()
                                                }
                                            }
                                            override fun onFailure(
                                                call: Call<DeleteResponse>,
                                                t: Throwable
                                            ) {
                                                Toast.makeText(
                                                    this@DetailsActivity,
                                                    "${t.message}",
                                                    Toast.LENGTH_LONG
                                                ).show()
                                            }
                                        })
                                    }
                                }
                                else {
                                    Toast.makeText(this@DetailsActivity, "Please log in to your account first.", Toast.LENGTH_LONG).show()
                                }
                            }


                            // Add to Cart
                            _frameAddToCart.setOnClickListener {
                                if (cekLogin) {
                                    val idShoppingCart = "shopping_cart"
                                    val checkDataShoppingCart = sp.contains(idShoppingCart)
                                    val editor = sp.edit()

                                    val arrDataDetails: ArrayList<DetailCart> = arrayListOf()

                                    val dataDetailsNew = DetailCart(
                                        data.detail.id,
                                        data.detail.nama_item,
                                        data.detail.harga,
                                        data.detail.harga_indo,
                                        data.detail.thumbnail,
                                        _total.text.toString().toInt()
                                    )

                                    // Cek kalau belum 100
                                    var addToCart = true
                                    val maxAdd = 100
                                    if (checkDataShoppingCart) {
                                        val ambilDataCart = sp.getString("shopping_cart", null)

                                        val typeArrShopping =
                                            object : TypeToken<ArrayList<DetailCart>>() {}.type

                                        val getArrayShopping: ArrayList<DetailCart> =
                                            gson.fromJson(ambilDataCart, typeArrShopping)

                                        val findId = getArrayShopping.find { it.id == data.detail.id }
                                        if (findId != null) {

                                            var defaultValue = 0
                                            for (element in getArrayShopping) {
                                                if (element.id == data.detail.id) {
                                                    defaultValue = element.total_cart
                                                }
                                            }

                                            if (defaultValue + (_total.text.toString().toInt()) > maxAdd) {
                                                addToCart = false
                                            }
                                        }
                                    }

                                    if (addToCart) {
                                        if (!checkDataShoppingCart) {
                                            arrDataDetails.add(dataDetailsNew)
                                        } else {
                                            val ambilDataCart = sp.getString("shopping_cart", null)

                                            if (ambilDataCart != null) {
                                                val typeArrShopping =
                                                    object :
                                                        TypeToken<ArrayList<DetailCart>>() {}.type

                                                val getArrayShopping: ArrayList<DetailCart> =
                                                    gson.fromJson(ambilDataCart, typeArrShopping)

                                                if (getArrayShopping.size > 0) {
                                                    for (element in getArrayShopping) {
                                                        var dataDetails = element

                                                        if (element.id == data.detail.id) {
                                                            dataDetails = DetailCart(
                                                                element.id,
                                                                element.nama_item,
                                                                element.harga,
                                                                element.harga_indo,
                                                                element.thumbnail,
                                                                element.total_cart + _total.text.toString()
                                                                    .toInt()
                                                            )
                                                        }

                                                        arrDataDetails.add(dataDetails)
                                                    }
                                                }

                                                val cariIdAda =
                                                    getArrayShopping.find { it.id == data.detail.id }

                                                if (cariIdAda == null) {
                                                    arrDataDetails.add(dataDetailsNew)
                                                }

                                            } else {
                                                Toast.makeText(
                                                    this@DetailsActivity,
                                                    "Error when entering shopping cart data.",
                                                    Toast.LENGTH_LONG
                                                ).show()
                                            }
                                        }

                                        _total.text = "1"
                                        _totalPrice.text = data.detail.harga_indo


                                        val dataJson = gson.toJson(arrDataDetails)
                                        editor.putString("shopping_cart", dataJson)
                                        editor.apply()

                                        totalCart()
                                        popup(2)
                                    }
                                    else {
                                        Toast.makeText(this@DetailsActivity, "You can only shop for 100 items. Please check the shopping cart.", Toast.LENGTH_LONG).show()
                                    }
                                }
                                else {
                                    Toast.makeText(this@DetailsActivity, "Please log in to your account first.", Toast.LENGTH_LONG).show()
                                }
                            }
                        }
                        _progressBar.isGone = true
                    }
                }
                else {
                    Toast.makeText(this@DetailsActivity, "Upss! Error: ${response.code()}", Toast.LENGTH_LONG).show()
                }
            }
            override fun onFailure(call: Call<ResponseGetDetails>, t: Throwable) {
                Toast.makeText(this@DetailsActivity, "${t.message}", Toast.LENGTH_LONG).show()
            }
        })

        _imgBack.setOnClickListener {
            onBackPressed()
        }
    }

    private fun popup(count: Int) {
        if (count == 0) {
            val popupCart: View = LayoutInflater.from(this).inflate(R.layout.popup, null)
            val setPopUpCart = AlertDialog.Builder(this)
                .setView(popupCart)
                .create()

            setPopUpCart.window!!.attributes.windowAnimations = R.style.animPopUp
            val _tvPopUp : TextView = popupCart.findViewById(R.id.isiTextPopUp)

            _tvPopUp.text = "Added To Wishlist"

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
        else if (count == 1) {
            val popupCart: View = LayoutInflater.from(this).inflate(R.layout.popup, null)
            val setPopUpCart = AlertDialog.Builder(this)
                .setView(popupCart)
                .create()

            setPopUpCart.window!!.attributes.windowAnimations = R.style.animPopUp
            val _tvPopUp : TextView = popupCart.findViewById(R.id.isiTextPopUp)

            _tvPopUp.text = "Removed From Wishlist"

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
        else if (count == 2) {
            val popupCart: View = LayoutInflater.from(this).inflate(R.layout.popup, null)
            val setPopUpCart = AlertDialog.Builder(this)
                .setView(popupCart)
                .create()

            setPopUpCart.window!!.attributes.windowAnimations = R.style.animPopUp

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



    private fun totalCart() {
        val sp = getSharedPreferences("user_login_detail", Context.MODE_PRIVATE)
        val cekLogin = sp!!.contains("detail")

        val totalShopCart: TextView = findViewById(R.id.totalShopCartDetail)
        if (cekLogin) {
            if (sp.contains("shopping_cart")) {
                val getShopCart = sp.getString("shopping_cart", null)

                if (getShopCart != null) {
                    val gson = Gson()

                    val typeArrShopping =
                        object : TypeToken<ArrayList<DetailCart>>() {}.type

                    val getArrayShopping: ArrayList<DetailCart> =
                        gson.fromJson(getShopCart, typeArrShopping)

                    var totalCart = 0
                    for (element in getArrayShopping) {
                        totalCart += element.total_cart
                    }

                    if (totalCart > 0) {
                        totalShopCart.isVisible = true
                        totalShopCart.text = totalCart.toString()
                    }
                }
            }
        }
    }
}