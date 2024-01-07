package com.proyek.paba.feasthub

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.proyek.paba.feasthub.adapter.ProdukDetailAdapter
import com.proyek.paba.feasthub.api.URL
import com.proyek.paba.feasthub.api.signin.DetailUser
import com.proyek.paba.feasthub.api.detail_transaction.APIService
import com.proyek.paba.feasthub.api.detail_transaction.ProdukItems
import com.proyek.paba.feasthub.api.detail_transaction.ResponseGet
import com.proyek.paba.feasthub.api.detail_transaction.ResponseUpdate
import com.proyek.paba.feasthub.fragment.Transaction
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.NumberFormat
import java.util.Locale

class DetailTransactionActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_transaction)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        val item_id = intent.getStringExtra("item_id")
        val _progressBarDetailTransaction: ProgressBar = findViewById(R.id.progressBarDetailTransaction)
        val _linearFooter: LinearLayout = findViewById(R.id.linearFooter)
        val _scDetail:  ScrollView = findViewById(R.id.scOrderDetails)
        val _linearPaymentDetail: LinearLayout = findViewById(R.id.linearPaymentDetail)
        val _imgPembayaran: ImageView = findViewById(R.id.imgPembyaran)
        val _btnCekPayment: Button = findViewById(R.id.btnCekPayment)


        val _tvPurchaseStatus: TextView = findViewById(R.id.tvPurchaseStatus)
        val _tvTanggalPembelianOrder: TextView = findViewById(R.id.tvTanggalPembelianOrder)
        val _tvPaidOn: TextView = findViewById(R.id.tvPaidOn)
        val _rcItems: RecyclerView = findViewById(R.id.rcItems)
        val _tvNoProducts:  TextView = findViewById(R.id.tvNoProducts)
        val _tvNoResiOrder: TextView = findViewById(R.id.tvNoResiOrder)
        val _tvPenerimaOrder: TextView = findViewById(R.id.tvPenerimaOrder)
        val _tvAlamatRumah: TextView = findViewById(R.id.tvAlamatRumah)
        val _tvTotalItemOrder: TextView = findViewById(R.id.tvTotalItemOrder)
        val _tvTotalPrice: TextView = findViewById(R.id.tvTotalPrice)
        val _tvAdminFees: TextView = findViewById(R.id.tvAdminFees)
        val _tvTotalPriceOrder: TextView = findViewById(R.id.tvTotalPriceOrder)


        val sp = getSharedPreferences("user_login_detail", Context.MODE_PRIVATE)
        val cekMasuk: Boolean = sp!!.contains("detail")

        if (cekMasuk) {
            val gson = Gson()
            val getJson = sp.getString("detail", null)
            val jsonToString = gson.fromJson(getJson, DetailUser::class.java)


            val retrofit = Retrofit.Builder()
                .baseUrl(URL().urlApi)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            val apiService = retrofit.create(APIService::class.java)
            val call = apiService.get_data(item_id.toString(), jsonToString.id)

            call.enqueue(object: Callback<ResponseGet> {
                override fun onResponse(call: Call<ResponseGet>, response: Response<ResponseGet>) {
                    if (response.isSuccessful) {
                        val data = response.body()

                        if (data != null) {
                            val dataProduk = data.data

                            if (dataProduk != null) {
                                _progressBarDetailTransaction.isVisible = false
                                _scDetail.isVisible = true

                                if (dataProduk.status == "selesai") {
                                    _tvPurchaseStatus.text = "Paid"
                                    _tvPurchaseStatus.setBackgroundResource(R.drawable.bg_radius_list_paid)

                                    val marginBottomInPixels = 200
                                    val params = LinearLayout.LayoutParams(
                                        LinearLayout.LayoutParams.MATCH_PARENT,
                                        LinearLayout.LayoutParams.WRAP_CONTENT
                                    )

                                    params.setMargins(0, 0, 0, marginBottomInPixels)
                                    _linearPaymentDetail.layoutParams = params

                                }
                                else {
                                    _linearFooter.isVisible = true
                                    _tvPurchaseStatus.text = "Pending"
                                    _tvPurchaseStatus.setBackgroundResource(R.drawable.bg_radius_list_pending)
                                }

                                var paidOn = "-"
                                if (dataProduk.dibayar_pada != "")
                                    paidOn = dataProduk.dibayar_pada

                                _tvTanggalPembelianOrder.text = dataProduk.tanggal_pembelian
                                _tvPaidOn.text = paidOn

                                if (dataProduk.produk_items.size == 0) {
                                    _tvNoProducts.isVisible = true
                                    _rcItems.isVisible = false
                                }
                                else {
                                    _tvNoProducts.isVisible = false
                                    _rcItems.isVisible = true
                                }

                                val currencyFormat = NumberFormat.getCurrencyInstance(
                                    Locale("in", "ID"))

                                _tvNoResiOrder.text = dataProduk.no_resi
                                _tvPenerimaOrder.text = dataProduk.penerima
                                _tvAlamatRumah.text = dataProduk.alamat
                                _tvTotalItemOrder.text = dataProduk.total_item

                                _tvTotalPrice.text = currencyFormat.format(dataProduk.harga.toDouble())
                                _tvAdminFees.text = currencyFormat.format(dataProduk.harga_fee.toDouble())
                                _tvTotalPriceOrder.text = currencyFormat.format(dataProduk.total_harga.toDouble())


                                val adapterItems = ProdukDetailAdapter(dataProduk.produk_items)
                                adapterItems.setOnItemClickCallback(object: ProdukDetailAdapter.OnItemClickCallback {
                                    override fun onItemClicked(data: ProdukItems) {
                                        val intent = Intent(this@DetailTransactionActivity, DetailsActivity::class.java)
                                        intent.putExtra("item_id", data.item_id)
                                        startActivity(intent)
                                    }
                                })
                                _rcItems.layoutManager = LinearLayoutManager(this@DetailTransactionActivity)
                                _rcItems.adapter = adapterItems


                                _imgPembayaran.setOnClickListener {

                                    val alertCustomDialog: View = LayoutInflater.from(this@DetailTransactionActivity)
                                        .inflate(R.layout.va_popup, null)

                                    val noVa: TextView = alertCustomDialog.findViewById(R.id.tvVA)
                                    noVa.text = dataProduk.virtual_account

                                    val totalHargaDialog: TextView = alertCustomDialog.findViewById(R.id.tvTotalHarga)
                                    totalHargaDialog.text = currencyFormat.format(dataProduk.total_harga.toDouble())

                                    val imgCopyVa: ImageView = alertCustomDialog.findViewById(R.id.imgCopyVA)
                                    imgCopyVa.setOnClickListener {
                                        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                        val clip = ClipData.newPlainText("VA_Copy", dataProduk.virtual_account)
                                        clipboard.setPrimaryClip(clip)

                                        Toast.makeText(this@DetailTransactionActivity, "Copied to clipboard", Toast.LENGTH_SHORT).show()
                                    }

                                    val alertDialog = AlertDialog.Builder(this@DetailTransactionActivity)
                                        .setView(alertCustomDialog)
                                        .setNegativeButton("Close") {dialog, _, ->
                                            dialog.dismiss()
                                        }
                                        .create()

                                    alertDialog.show()
                                }

                                _btnCekPayment.setOnClickListener {
                                    val callUpdate = apiService.update_status(item_id.toString(), jsonToString.id)

                                    _btnCekPayment.isEnabled = false

                                    callUpdate.enqueue(object: Callback<ResponseUpdate> {
                                        override fun onResponse(
                                            call: Call<ResponseUpdate>,
                                            response: Response<ResponseUpdate>
                                        ) {
                                            if (response.isSuccessful) {
                                                val dataUpdate = response.body()

                                                if (dataUpdate != null) {
                                                    if (dataUpdate.success) {
                                                        _linearFooter.isVisible = false
                                                        _tvPaidOn.text = dataUpdate.paid_on

                                                        _tvPurchaseStatus.text = "Paid"
                                                        _tvPurchaseStatus.setBackgroundResource(R.drawable.bg_radius_list_paid)

                                                        val marginBottomInPixels = 200
                                                        val params = LinearLayout.LayoutParams(
                                                            LinearLayout.LayoutParams.MATCH_PARENT,
                                                            LinearLayout.LayoutParams.WRAP_CONTENT
                                                        )

                                                        params.setMargins(0, 0, 0, marginBottomInPixels)
                                                        _linearPaymentDetail.layoutParams = params

                                                        popup()

//                                                        Toast.makeText(this@DetailTransactionActivity, "Payment has been confirmed.", Toast.LENGTH_LONG).show()
                                                    }
                                                }
                                                else {
                                                    Toast.makeText(this@DetailTransactionActivity, "Can't find your transaction.", Toast.LENGTH_LONG).show()
                                                    _btnCekPayment.isEnabled = true
                                                }
                                            }
                                            else {
                                                Toast.makeText(this@DetailTransactionActivity, "Upss! Error: ${response.code()}", Toast.LENGTH_LONG).show()
                                                _btnCekPayment.isEnabled = true
                                            }
                                        }

                                        override fun onFailure(
                                            call: Call<ResponseUpdate>,
                                            t: Throwable
                                        ) {
                                            Toast.makeText(this@DetailTransactionActivity, "${t.message}", Toast.LENGTH_LONG).show()
                                            _btnCekPayment.isEnabled = true
                                        }

                                    })
                                }
                            }
                            else {
                                Toast.makeText(this@DetailTransactionActivity, "Can't find your transaction.", Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                    else {
                        Toast.makeText(this@DetailTransactionActivity, "Upss! Error: ${response.code()}", Toast.LENGTH_LONG).show()
                    }
                }

                override fun onFailure(call: Call<ResponseGet>, t: Throwable) {
                    Toast.makeText(this@DetailTransactionActivity, "${t.message}", Toast.LENGTH_LONG).show()
                }

            })
        }
    }

    private fun popup() {
        val popupCart: View = LayoutInflater.from(this).inflate(R.layout.popup,null)
        val setPopUpCart = AlertDialog.Builder(this)
            .setView(popupCart)
            .create()

        setPopUpCart.window!!.attributes.windowAnimations = R.style.animPopUp

        val _tvPopUp : TextView = popupCart.findViewById(R.id.isiTextPopUp)
        _tvPopUp.text = "Payment Has Been Confirmed"

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
}