package com.proyek.paba.feasthub

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.proyek.paba.feasthub.adapter.HomeAdapter
import com.proyek.paba.feasthub.api.URL
import com.proyek.paba.feasthub.api.category.APIService
import com.proyek.paba.feasthub.api.home.ItemsArray
import com.proyek.paba.feasthub.api.home.ResponseGet
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.Locale

class CategoriesActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_categories)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        val dataIntent = intent.getStringExtra("category").toString()
        toolbar.title = "Category: ${dataIntent.capitalize(Locale.ROOT)
            .replace("_", " ")}"

        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

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
            override fun onResponse(call: Call<ResponseGet>, response: retrofit2.Response<ResponseGet>) {
                if (response.isSuccessful) {
                    val data = response.body()

                    if (data != null) {
                        if (data.data.size == 0) {
                            _linearEmpty.isGone = false
                        }
                        else {
                            _linearData.isVisible = true

                            val homeAdapter = HomeAdapter(data.data)
                            homeAdapter.setOnItemClickCallback(object: HomeAdapter.OnItemClickCallback {
                                override fun onItemClicked(data: ItemsArray) {
                                    val intent = Intent(this@CategoriesActivity, DetailsActivity::class.java)
                                    intent.putExtra("item_id", data.id)
                                    startActivity(intent)
                                }
                            })

                            _rcItem.layoutManager = LinearLayoutManager(this@CategoriesActivity)
                            _rcItem.adapter = homeAdapter
                        }
                    }
                    else {
                        _linearEmpty.isGone = false
                    }

                    _progressBar.isGone = true
                } else {
                    Toast.makeText(this@CategoriesActivity, "Upss! Error: ${response.code()}", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<ResponseGet>, t: Throwable) {
                Toast.makeText(this@CategoriesActivity, "${t.message}", Toast.LENGTH_LONG).show()
            }
        })

    }
}