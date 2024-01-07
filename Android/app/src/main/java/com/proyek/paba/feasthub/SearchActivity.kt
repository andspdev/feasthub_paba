package com.proyek.paba.feasthub

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.slider.RangeSlider
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.proyek.paba.feasthub.adapter.HomeAdapter
import com.proyek.paba.feasthub.api.URL
import com.proyek.paba.feasthub.api.home.ItemsArray
import com.proyek.paba.feasthub.api.home.ResponseGet
import com.proyek.paba.feasthub.api.search.APIService
import com.proyek.paba.feasthub.data_class.FilterDataClass
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.NumberFormat
import java.util.Locale

class SearchActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }


        val _progressBar: ProgressBar = findViewById(R.id.progressBarCategories)
        val _linearEmpty: LinearLayout = findViewById(R.id.linearEmpty)
        val _linearData: LinearLayout = findViewById(R.id.linearData)

        val checkboxFilter: ArrayList<String> = arrayListOf()
        var minHargaFilter = 20000
        var maxHargaFilter = 85000

        val dataIntent = intent.getStringExtra("search_content").toString()
        val filterJson = Gson().fromJson(dataIntent, FilterDataClass::class.java)
        val valueSearch = filterJson.search

        if (filterJson.minHarga != "")
            minHargaFilter = filterJson.minHarga.toInt()

        if (filterJson.maxHarga != "")
            maxHargaFilter = filterJson.maxHarga.toInt()

        val _tvContentSearch: TextView = findViewById(R.id.tvContentSearch)
        val listType = object : TypeToken<ArrayList<String>>() {}.type
        val kategoriFilter: ArrayList<String> = Gson().fromJson(Gson().toJson(filterJson.kategori), listType)


        var _valueTextSearch = "Search for items \"${valueSearch}\""
        if (filterJson.minHarga != "" || filterJson.maxHarga != "" || kategoriFilter.size > 0) {

            if (kategoriFilter.size > 0) {
                for (kategori in kategoriFilter)
                    checkboxFilter.add(kategori)
            }

            _valueTextSearch = "Search for items \"${valueSearch}\" and by filter."
        }

        _tvContentSearch.text = _valueTextSearch

        // Search
        val _edSearch: EditText = findViewById(R.id.edSearch)

        if (filterJson.search != "")
            _edSearch.setText(valueSearch)


        // Filter
        val _filterImg : ImageView = findViewById(R.id.imgFilterClick)
        _filterImg.setOnClickListener {

            val alertCustomDialog: View = LayoutInflater.from(this@SearchActivity).inflate(R.layout.filter_popup, null)
            val alertDialog = AlertDialog.Builder(this@SearchActivity)
                .setView(alertCustomDialog)
                .create()

            val _rangeSlider = alertCustomDialog.findViewById<RangeSlider>(R.id.rangeSlider)
            val _minText = alertCustomDialog.findViewById<TextView>(R.id.minText)
            val _maxText = alertCustomDialog.findViewById<TextView>(R.id.maxText)
            val _btnApply = alertCustomDialog.findViewById<Button>(R.id.btnFilterApply)
            val _btnClose = alertCustomDialog.findViewById<ImageView>(R.id.btnClose)


            // Range harga
            _rangeSlider?.addOnChangeListener { _, _, _ ->
                val minValue = _rangeSlider.values[0]
                val maxValue = _rangeSlider.values[1]

                minHargaFilter = minValue.toInt()
                maxHargaFilter = maxValue.toInt()

                val currencyFormat = NumberFormat.getCurrencyInstance(
                    Locale("in", "ID"))

                _minText.text = currencyFormat.format(minValue?.toDouble())
                _maxText.text = currencyFormat.format(maxValue?.toDouble())
            }

            val defaultValueHarga: List<Float> = listOf(minHargaFilter.toFloat(), maxHargaFilter.toFloat())
            _rangeSlider.values = defaultValueHarga


            // Kategori
            val _checkBoxDrinks: CheckBox = alertCustomDialog.findViewById(R.id.checkBoxDrinks)
            if (checkboxFilter.contains("drinks"))
                _checkBoxDrinks.isChecked = true

            _checkBoxDrinks.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    if (!checkboxFilter.contains("drinks")) {
                        checkboxFilter.add("drinks")
                    }
                } else {
                    checkboxFilter.remove("drinks")
                }
            }

            val _checkBoxVegetable: CheckBox = alertCustomDialog.findViewById(R.id.checkBoxVegetable)
            if (checkboxFilter.contains("vegetable"))
                _checkBoxVegetable.isChecked = true

            _checkBoxVegetable.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    if (!checkboxFilter.contains("vegetable")) {
                        checkboxFilter.add("vegetable")
                    }
                } else {
                    checkboxFilter.remove("vegetable")
                }
            }

            val _checkBoxCake: CheckBox = alertCustomDialog.findViewById(R.id.checkBoxCake)
            if (checkboxFilter.contains("cake"))
                _checkBoxCake.isChecked = true

            _checkBoxCake.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    if (!checkboxFilter.contains("cake")) {
                        checkboxFilter.add("cake")
                    }
                } else {
                    checkboxFilter.remove("cake")
                }
            }

            val _checkBoxIceCream: CheckBox = alertCustomDialog.findViewById(R.id.checkBoxIceCream)
            if (checkboxFilter.contains("ice_cream"))
                _checkBoxIceCream.isChecked = true

            _checkBoxIceCream.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    if (!checkboxFilter.contains("ice_cream")) {
                        checkboxFilter.add("ice_cream")
                    }
                } else {
                    checkboxFilter.remove("ice_cream")
                }
            }

            val _checkBoxSnacks: CheckBox = alertCustomDialog.findViewById(R.id.checkBoxSnacks)
            if (checkboxFilter.contains("snacks"))
                _checkBoxSnacks.isChecked = true

            _checkBoxSnacks.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    if (!checkboxFilter.contains("snacks")) {
                        checkboxFilter.add("snacks")
                    }
                } else {
                    checkboxFilter.remove("snacks")
                }
            }

            val _checkBoxFood: CheckBox = alertCustomDialog.findViewById(R.id.checkBoxFood)
            if (checkboxFilter.contains("food"))
                _checkBoxFood.isChecked = true

            _checkBoxFood.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    if (!checkboxFilter.contains("food")) {
                        checkboxFilter.add("food")
                    }
                } else {
                    checkboxFilter.remove("food")
                }
            }

            val _checkBoxMeal: CheckBox = alertCustomDialog.findViewById(R.id.checkBoxMeal)
            if (checkboxFilter.contains("meal"))
                _checkBoxMeal.isChecked = true

            _checkBoxMeal.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    if (!checkboxFilter.contains("meal")) {
                        checkboxFilter.add("meal")
                    }
                } else {
                    checkboxFilter.remove("meal")
                }
            }


            _btnApply.setOnClickListener {
                alertDialog.dismiss()

                _progressBar.isVisible = true
                _linearEmpty.isVisible = false
                _linearData.isVisible = false

                val filterDataClass = FilterDataClass(
                    _edSearch.text.toString(),
                    minHargaFilter.toString(),
                    maxHargaFilter.toString(),
                    checkboxFilter
                )

                val gson = Gson()
                val arrayToJson = gson.toJson(filterDataClass)

                this.getDataSearch(arrayToJson)

                _valueTextSearch = "Search for items \"${_edSearch.text}\" and by filter."
                _tvContentSearch.text = _valueTextSearch
            }

            _btnClose.setOnClickListener {
                alertDialog.dismiss()
            }


            alertDialog.show()
        }

        this.getDataSearch(dataIntent)

        _edSearch.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {

                if (_edSearch.text.toString().trim() != "") {
                    _progressBar.isVisible = true
                    _linearEmpty.isVisible = false
                    _linearData.isVisible = false

                    val filterDataClass = FilterDataClass(
                        _edSearch.text.toString(),
                        minHargaFilter.toString(),
                        maxHargaFilter.toString(),
                        checkboxFilter
                    )

                    val gson = Gson()
                    val arrayToJson = gson.toJson(filterDataClass)

                    this.getDataSearch(arrayToJson)

                    _valueTextSearch = "Search for items \"${_edSearch.text}\""
                    _tvContentSearch.text = _valueTextSearch
                }

                return@setOnKeyListener true
            }
            return@setOnKeyListener false
        }
    }


    private fun getDataSearch(dataIntent: String) {
        val _progressBar: ProgressBar = findViewById(R.id.progressBarCategories)
        val _linearEmpty: LinearLayout = findViewById(R.id.linearEmpty)
        val _linearData: LinearLayout = findViewById(R.id.linearData)
        val _rcItem: RecyclerView = findViewById(R.id.rcItems)

        val retrofit = Retrofit.Builder()
            .baseUrl(URL().urlApi)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(APIService::class.java)
        val call = apiService.create(dataIntent)

        call.enqueue(object : retrofit2.Callback<ResponseGet> {
            override fun onResponse(call: Call<ResponseGet>, response: Response<ResponseGet>) {
                if (response.isSuccessful) {
                    val data = response.body()

                    if (data != null) {
                        if (data.data.size == 0) {
                            _linearEmpty.isGone = false
                        } else {
                            _linearData.isVisible = true

                            val searchAdapter = HomeAdapter(data.data)
                            searchAdapter.setOnItemClickCallback(object: HomeAdapter.OnItemClickCallback {
                                override fun onItemClicked(data: ItemsArray) {
                                    val intent = Intent(this@SearchActivity, DetailsActivity::class.java)
                                    intent.putExtra("item_id", data.id)
                                    startActivity(intent)
                                }
                            })

                            _rcItem.layoutManager = LinearLayoutManager(this@SearchActivity)
                            _rcItem.adapter = searchAdapter
                        }
                    }
                    else {
                        _linearEmpty.isGone = false
                    }

                    _progressBar.isGone = true
                }
                else {
                    Toast.makeText(this@SearchActivity, "Upss! Error: ${response.code()}", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<ResponseGet>, t: Throwable) {
                Toast.makeText(this@SearchActivity, "${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }
}