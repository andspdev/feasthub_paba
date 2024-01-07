package com.proyek.paba.feasthub.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.proyek.paba.feasthub.DetailTransactionActivity
import com.proyek.paba.feasthub.R
import com.proyek.paba.feasthub.adapter.TransactionAdapter
import com.proyek.paba.feasthub.api.URL
import com.proyek.paba.feasthub.api.transaction.ItemsArray
import com.proyek.paba.feasthub.api.signin.DetailUser
import com.proyek.paba.feasthub.api.transaction.APIService
import com.proyek.paba.feasthub.api.transaction.ResponseGet
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [Transaction.newInstance] factory method to
 * create an instance of this fragment.
 */
class Transaction : Fragment() {
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sp = activity?.getSharedPreferences("user_login_detail", Context.MODE_PRIVATE)
        val cekMasuk: Boolean = sp!!.contains("detail")

        if (cekMasuk) {
            val _progressBar: ProgressBar = view.findViewById(R.id.progressBarTransaction)
            val _itemEmpty: LinearLayout = view.findViewById(R.id.linearEmpty)
            val _itemData: LinearLayout = view.findViewById(R.id.linearData)
            val _rcItem: RecyclerView = view.findViewById(R.id.rcItems)

            val gson = Gson()
            val getJson= sp.getString("detail", null)
            val jsonToString = gson.fromJson(getJson, DetailUser::class.java)


            val retrofit = Retrofit.Builder()
                .baseUrl(URL().urlApi)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            val apiService = retrofit.create(APIService::class.java)
            val call = apiService.getData(jsonToString.id)

            call.enqueue(object: Callback<ResponseGet> {
                override fun onResponse(call: Call<ResponseGet>, response: Response<ResponseGet>) {
                    if (response.isSuccessful) {
                        val post = response.body()

                        if (post != null) {
                            if (post.success) {
                                if (post.data.size == 0) {
                                    _itemEmpty.isVisible = true
                                } else {
                                    _itemData.isVisible = true

                                    val dataArrayAdapter: ArrayList<ItemsArray> = post.data
                                    val transactionAdapter = TransactionAdapter(dataArrayAdapter)

                                    transactionAdapter.setOnItemClickCallback(object: TransactionAdapter.OnItemClickCallback {
                                        override fun onItemClicked(data: ItemsArray) {
                                            val intent = Intent(requireContext(), DetailTransactionActivity::class.java)
                                            intent.putExtra("item_id", data.id)
                                            startActivity(intent)
                                        }
                                    })

                                    _rcItem.layoutManager = LinearLayoutManager(view.context)
                                    _rcItem.adapter = transactionAdapter
                                }
                            }
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
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_transaction, container, false)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment Transaction.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Transaction().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}