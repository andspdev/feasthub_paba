package com.proyek.paba.feasthub

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.proyek.paba.feasthub.adapter.ShoppingCartAdapter
import com.proyek.paba.feasthub.api.URL
import com.proyek.paba.feasthub.api.details.DetailCart
import com.proyek.paba.feasthub.api.shopping_cart.APIService
import com.proyek.paba.feasthub.api.shopping_cart.BodyRequest
import com.proyek.paba.feasthub.api.shopping_cart.ResponsePost
import com.proyek.paba.feasthub.api.signin.DetailUser
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.NumberFormat
import java.util.Locale

class ShoppingCartActivity : AppCompatActivity() {
    private lateinit var sp: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shopping_cart)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        sp = getSharedPreferences("user_login_detail", Context.MODE_PRIVATE)
        val cekLogin = sp.contains("detail")

        if (cekLogin) {
            val linearEmpty: LinearLayout = findViewById(R.id.linearEmpty)
            val linearData: LinearLayout = findViewById(R.id.linearData)
            val linearFooter: LinearLayout = findViewById(R.id.linearFooter)
            val btnPayNow: Button = findViewById(R.id.btnPayNow)
            val _rcItems: RecyclerView = findViewById(R.id.rcItems)

            val _linearAlamatRumah: LinearLayout = findViewById(R.id.linearAlamatRumah)


            // Cek alamat rumah
            val getAlamatRumah = getSharedPreferences("user_settings", Context.MODE_PRIVATE)
            val containsAlamatRumah = getAlamatRumah.contains("addressHome")

            if (containsAlamatRumah) {
                val getValueAlamatRumah = getAlamatRumah.getString("addressHome", null)

                if (getValueAlamatRumah != null && getValueAlamatRumah != "") {
                    _linearAlamatRumah.isGone = false

                    val _tvAlamatRumah: TextView = findViewById(R.id.tvAlamatRumahTeks)
                    _tvAlamatRumah.text = getValueAlamatRumah

                    val _tvChangeAddress: TextView = findViewById(R.id.tvChangeAddress)
                    _tvChangeAddress.setOnClickListener {
                        startActivity(Intent(this@ShoppingCartActivity, SettingsActivity::class.java))
                    }
                }
            }

            val gson = Gson()
            val idShoppingCart = "shopping_cart"
            val checkDataShoppingCart = sp.contains(idShoppingCart)
            val ambilDataCart = sp.getString(idShoppingCart, null)

            if (checkDataShoppingCart && ambilDataCart != null) {
                val typeArrShopping =
                    object : TypeToken<ArrayList<DetailCart>>() {}.type

                val getArrayShopping: ArrayList<DetailCart> =
                    gson.fromJson(ambilDataCart, typeArrShopping)

                if (getArrayShopping.size == 0) {
                    linearEmpty.isVisible = true
                    linearData.isVisible = false
                    linearFooter.isVisible = false
                }
                else {
                    linearEmpty.isVisible = false
                    linearFooter.isVisible = true
                    linearData.isVisible = true

                    val adapterCart = ShoppingCartAdapter(getArrayShopping)

                    adapterCart.setOnItemClickCallback(object: ShoppingCartAdapter.OnItemClickCallback {
                        override fun onItemClicked(data: DetailCart) {
                            val intent = Intent(this@ShoppingCartActivity, DetailsActivity::class.java)
                            intent.putExtra("item_id", data.id.toString())
                            startActivity(intent)
                        }

                        override fun btnMinClicked(data: DetailCart) {
                            var edtValue = data.total_cart

                            if (edtValue > 1) {
                                edtValue--

                                val cariId = getArrayShopping.find { it.id == data.id }

                                if (cariId != null) {
                                    data.total_cart = edtValue
                                }

                                adapterCart.notifyDataSetChanged()

                                val jsonBaru = gson.toJson(getArrayShopping)
                                val editor = sp.edit()
                                editor.putString("shopping_cart", jsonBaru)
                                editor.apply()

                                totalPrice()
                            }
                            else {
                                deleteItem(
                                    getArrayShopping,
                                    data,
                                    adapterCart,
                                    linearEmpty,
                                    linearData,
                                    linearFooter
                                )
                            }
                        }

                        override fun btnMaxClicked(data: DetailCart) {
                            var edtValue = data.total_cart

                            if (edtValue < 100) {
                                edtValue++

                                val cariId = getArrayShopping.find { it.id == data.id }

                                if (cariId != null) {
                                    data.total_cart = edtValue
                                }
                            }

                            adapterCart.notifyDataSetChanged()

                            val jsonBaru = gson.toJson(getArrayShopping)
                            val editor = sp.edit()
                            editor.putString("shopping_cart", jsonBaru)
                            editor.apply()

                            totalPrice()
                        }

                        override fun deleteItem(data: DetailCart) {
                            deleteItem(
                                getArrayShopping,
                                data,
                                adapterCart,
                                linearEmpty,
                                linearData,
                                linearFooter
                            )
                        }
                    })

                    _rcItems.layoutManager = LinearLayoutManager(this@ShoppingCartActivity)
                    _rcItems.adapter = adapterCart

                    this.totalPrice()

                    btnPayNow.setOnClickListener {
                        val userData = sp.getString("detail", null)
                        val jsonToString = gson.fromJson(userData, DetailUser::class.java)


                        val userAddressSp = getSharedPreferences("user_settings", MODE_PRIVATE)
                        var alreadyAddress = false

                        if (userAddressSp.contains("addressHome")) {
                            if (userAddressSp.getString("addressHome", "") != "") {
                                alreadyAddress = true
                            }
                        }

                        if (!alreadyAddress) {
                            Toast.makeText(this@ShoppingCartActivity, "You haven't set a home address, please set it first in settings.", Toast.LENGTH_LONG).show()
                        }
                        else {
                            btnPayNow.isEnabled = false

                            var totalHarga = 0
                            var totalItem = 0
                            for (element in getArrayShopping) {
                                totalHarga += element.total_cart * element.harga
                                totalItem += element.total_cart
                            }


                            val bodyRequestCart = BodyRequest(
                                jsonToString.id,
                                userAddressSp.getString("addressHome", "-").toString(),
                                totalHarga.toString(),
                                totalItem.toString(),
                                jsonToString.name,
                                getArrayShopping,
                            )

                            val retrofit = Retrofit.Builder()
                                .baseUrl(URL().urlApi)
                                .addConverterFactory(GsonConverterFactory.create())
                                .build()

                            val apiService = retrofit.create(APIService::class.java)
                            val callPayNow = apiService.insert(bodyRequestCart)

                            callPayNow.enqueue(object : Callback<ResponsePost> {
                                override fun onResponse(
                                    call: Call<ResponsePost>,
                                    response: Response<ResponsePost>
                                ) {
                                    if (response.isSuccessful) {
                                        val data = response.body()

                                        if (data != null) {
                                            if (data.success && data.last_id != "") {

                                                popup()

                                                val durationInMillis: Long = 2000

                                                val countDownTimer: CountDownTimer = object : CountDownTimer(durationInMillis, 1000) {
                                                    override fun onTick(millisUntilFinished: Long) { }

                                                    override fun onFinish() {
                                                        val intent = Intent(this@ShoppingCartActivity, DetailTransactionActivity::class.java)
                                                        intent.putExtra("item_id", data.last_id)
                                                        startActivity(intent)
                                                        finish()

                                                        sp.edit().remove("shopping_cart").apply()
                                                    }
                                                }
                                                countDownTimer.start()

                                            }
                                            else {
                                                Toast.makeText(
                                                    this@ShoppingCartActivity,
                                                    "Can't get transaction ID.",
                                                    Toast.LENGTH_LONG
                                                ).show()
                                            }
                                        }
                                        else {
                                            Toast.makeText(
                                                this@ShoppingCartActivity,
                                                "Can't get your transaction.",
                                                Toast.LENGTH_LONG
                                            ).show()
                                        }

                                    } else {
                                        Toast.makeText(
                                            this@ShoppingCartActivity,
                                            "Upss! Error: ${response.code()}",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }
                                }

                                override fun onFailure(call: Call<ResponsePost>, t: Throwable) {
                                    Toast.makeText(
                                        this@ShoppingCartActivity,
                                        "${t.message}",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }

                            })
                        }
                    }
                }
            }
            else {
                linearEmpty.isVisible = true
                linearData.isVisible = false
                linearFooter.isVisible = false
            }
        }
    }

    private fun popup() {
        val popupCart: View = LayoutInflater.from(this).inflate(R.layout.popup,null)
        val setPopUpCart = AlertDialog.Builder(this)
            .setView(popupCart)
            .create()

        setPopUpCart.window!!.attributes.windowAnimations = R.style.animPopUp

        val _tvPopUp : TextView = popupCart.findViewById(R.id.isiTextPopUp)
        val _tvStatus : TextView = popupCart.findViewById(R.id.tvStatus)
        val _imgGif : ImageView = popupCart.findViewById(R.id.imgSuccess)

        _tvPopUp.text = "Payment Is Still Pending"
        _tvStatus.text = "Pending"
        _tvStatus.setTextColor(Color.parseColor("#FEC50E"))
        _imgGif.setImageResource(R.drawable.pending)

        setPopUpCart.show()

        val durationInMillis: Long = 2500

        val countDownTimer: CountDownTimer = object : CountDownTimer(durationInMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) { }

            override fun onFinish() {
                setPopUpCart.dismiss()
            }
        }
        countDownTimer.start()
    }


    private fun totalPrice() {
        val gson = Gson()
        val idShoppingCart = "shopping_cart"
        val checkDataShoppingCart = sp.contains(idShoppingCart)
        val ambilDataCart = sp.getString("shopping_cart", null)


        var updateData = "Rp0"
        if (checkDataShoppingCart) {
            val typeArrShopping =
                object : TypeToken<ArrayList<DetailCart>>() {}.type

            val getArrayShopping: ArrayList<DetailCart> =
                gson.fromJson(ambilDataCart, typeArrShopping)

            var totalHarga = 0
            for (element in getArrayShopping) {
                totalHarga += element.total_cart * element.harga
            }

            val currencyFormat = NumberFormat.getCurrencyInstance(
                Locale("in", "ID")
            )

            updateData = currencyFormat.format(totalHarga)
        }

        findViewById<TextView>(R.id.tvHargaFooter).text = updateData
    }


    private fun deleteItem(
        getArrayShopping: ArrayList<DetailCart>,
        data: DetailCart,
        adapterCart: ShoppingCartAdapter,
        linearEmpty: LinearLayout,
        linearData: LinearLayout,
        linearFooter: LinearLayout
    ) {
        val gson = Gson()

        AlertDialog.Builder(this@ShoppingCartActivity)
            .setTitle("Delete Item?")
            .setMessage("Are you sure you want to delete this item?")
            .setPositiveButton("Delete") { dialog, _ ->
                dialog.dismiss()
                getArrayShopping.removeIf { it.id == data.id }

                adapterCart.notifyDataSetChanged()

                if (getArrayShopping.size == 0) {
                    linearEmpty.isVisible = true
                    linearData.isVisible = false
                    linearFooter.isVisible = false

                    sp.edit().remove("shopping_cart").apply()
                }
                else {
                    val jsonBaru = gson.toJson(getArrayShopping)
                    val editor = sp.edit()
                    editor.putString("shopping_cart", jsonBaru)
                    editor.apply()

                    totalPrice()
                }
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
}