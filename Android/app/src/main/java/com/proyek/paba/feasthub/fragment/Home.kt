package com.proyek.paba.feasthub.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.slider.RangeSlider
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.proyek.paba.feasthub.CategoriesActivity
import com.proyek.paba.feasthub.DetailsActivity
import com.proyek.paba.feasthub.R
import com.proyek.paba.feasthub.SearchActivity
import com.proyek.paba.feasthub.ShoppingCartActivity
import com.proyek.paba.feasthub.adapter.HomeAdapter
import com.proyek.paba.feasthub.api.URL
import com.proyek.paba.feasthub.api.details.DetailCart
import com.proyek.paba.feasthub.api.home.APIService
import com.proyek.paba.feasthub.api.home.ItemsArray
import com.proyek.paba.feasthub.api.home.ResponseGet
import com.proyek.paba.feasthub.data_class.FilterDataClass
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.NumberFormat
import java.util.Locale

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [Home.newInstance] factory method to
 * create an instance of this fragment.
 */
class Home : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sp = activity?.getSharedPreferences("user_login_detail", Context.MODE_PRIVATE)
        val cekLogin = sp!!.contains("detail")

        val totalShopCart: TextView = view.findViewById(R.id.totalShopCart)
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


        val shoppingCart: ImageView = view.findViewById(R.id.imgCartShop)
        shoppingCart.setOnClickListener {

            if (!cekLogin) {
                Toast.makeText(
                    view.context,
                    "Please log in to your account first.",
                    Toast.LENGTH_LONG
                ).show()
            }
            else {
                startActivity(Intent(view.context, ShoppingCartActivity::class.java))
            }
        }

        val checkboxFilter: ArrayList<String> = arrayListOf()

        var minHargaFilter = 20000
        var maxHargaFilter = 85000

        val _searchBar : EditText = view.findViewById(R.id.inputName)
        _searchBar.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {

                val filterDataClass = FilterDataClass(
                    _searchBar.text.toString(),
                    "",
                    "",
                    arrayListOf()
                )

                val gson = Gson()
                val arrayToJsonFilter = gson.toJson(filterDataClass)

                if (_searchBar.text.toString() != "") {
                    val intent = Intent(requireContext(), SearchActivity::class.java)
                    intent.putExtra("search_content", arrayToJsonFilter)
                    startActivity(intent)
                }

                return@setOnKeyListener true
            }
            return@setOnKeyListener false
        }

        val _filterImg : ImageView = view.findViewById(R.id.imgFilterClick)
        _filterImg.setOnClickListener {

            val alertCustomDialog: View = LayoutInflater.from(view.context).inflate(R.layout.filter_popup, null)
            val alertDialog = AlertDialog.Builder(view.context)
                .setView(alertCustomDialog)
                .create()

            alertDialog.window!!.attributes.windowAnimations = R.style.animPopUp

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

                val filterDataClass = FilterDataClass(
                    _searchBar.text.toString(),
                    minHargaFilter.toString(),
                    maxHargaFilter.toString(),
                    checkboxFilter
                )

                val gson = Gson()
                val arrayToJsonFilter = gson.toJson(filterDataClass)

                val intent = Intent(requireContext(), SearchActivity::class.java)
                intent.putExtra("search_content", arrayToJsonFilter)
                startActivity(intent)
            }

            _btnClose.setOnClickListener {
                alertDialog.dismiss()
            }

            alertDialog.show()
        }

        // Button categories
        val _btnDrinks : ImageButton = view.findViewById(R.id.imgBtnDrink)
        val _btnFood : ImageButton = view.findViewById(R.id.imgBtnFood)
        val _btnCake : ImageButton = view.findViewById(R.id.imgBtnCake)
        val _btnIceCream : ImageButton = view.findViewById(R.id.imgBtnIceCream)
        val _btnSnacks : ImageButton = view.findViewById(R.id.imgBtnSnacks)
        val _btnVege : ImageButton = view.findViewById(R.id.imgBtnVegetable)
        val _btnMeal : ImageButton = view.findViewById(R.id.imgBtnMeal)
        val _btnAll : ImageButton = view.findViewById(R.id.imgBtnAll)

        _btnDrinks.setOnClickListener {
            val intent = Intent(requireContext(), CategoriesActivity::class.java)
            intent.putExtra("category", "drinks")
            startActivity(intent)
        }

        _btnFood.setOnClickListener {
            val intent = Intent(requireContext(), CategoriesActivity::class.java)
            intent.putExtra("category", "food")
            startActivity(intent)
        }

        _btnCake.setOnClickListener {
            val intent = Intent(requireContext(), CategoriesActivity::class.java)
            intent.putExtra("category", "cake")
            startActivity(intent)
        }

        _btnIceCream.setOnClickListener {
            val intent = Intent(requireContext(), CategoriesActivity::class.java)
            intent.putExtra("category", "ice_cream")
            startActivity(intent)
        }

        _btnSnacks.setOnClickListener {
            val intent = Intent(requireContext(), CategoriesActivity::class.java)
            intent.putExtra("category", "snacks")
            startActivity(intent)
        }

        _btnVege.setOnClickListener {
            val intent = Intent(requireContext(), CategoriesActivity::class.java)
            intent.putExtra("category", "vegetable")
            startActivity(intent)
        }

        _btnMeal.setOnClickListener {
            val intent = Intent(requireContext(), CategoriesActivity::class.java)
            intent.putExtra("category", "meal")
            startActivity(intent)
        }

        _btnAll.setOnClickListener {
            val intent = Intent(requireContext(), CategoriesActivity::class.java)
            intent.putExtra("category", "all")
            startActivity(intent)
        }



        // Load API Best Items
        val _progressBar: ProgressBar = view.findViewById(R.id.progressBar)
        val _itemEmpty: TextView = view.findViewById(R.id.tvNoBestItems)
        val _rcItem: RecyclerView = view.findViewById(R.id.recyclerViewItems)

        val retrofit = Retrofit.Builder()
            .baseUrl(URL().urlApi)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(APIService::class.java)
        val call = apiService.create()

        call.enqueue(object : Callback<ResponseGet> {
            override fun onResponse(call: Call<ResponseGet>, response: Response<ResponseGet>) {
                if (response.isSuccessful) {
                    val post = response.body()

                    if (post != null) {
                        if (post.success) {
                            if (post.data.size == 0) {
                                _itemEmpty.isVisible = true
                            }
                            else {
                                _itemEmpty.isGone = true
                                _rcItem.isVisible = true

                                val homeAdapter = HomeAdapter(post.data)
                                homeAdapter.setOnItemClickCallback(object: HomeAdapter.OnItemClickCallback {
                                    override fun onItemClicked(data: ItemsArray) {
                                        val intent = Intent(requireActivity(), DetailsActivity::class.java)
                                        intent.putExtra("item_id", data.id)
                                        startActivity(intent)
                                    }
                                })

                                _rcItem.layoutManager = LinearLayoutManager(view.context)
                                _rcItem.adapter = homeAdapter
                            }
                        }
                    }
                    else {
                        _itemEmpty.isVisible = true
                    }

                    _progressBar.isGone = true
                }
                else {
                    Toast.makeText(requireContext(), "Upss! Error: ${response.code()}", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<ResponseGet>, t: Throwable) {
                Toast.makeText(requireContext(), "${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment Home.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Home().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}