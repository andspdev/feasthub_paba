package com.proyek.paba.feasthub.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.gson.Gson
import com.proyek.paba.feasthub.MainActivity
import com.proyek.paba.feasthub.R
import com.proyek.paba.feasthub.api.URL
import com.proyek.paba.feasthub.api.signin.APIService
import com.proyek.paba.feasthub.api.signin.DetailUser
import com.proyek.paba.feasthub.api.signin.SignInRequest
import com.proyek.paba.feasthub.api.signin.ResponsePost
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
 * Use the [Login.newInstance] factory method to
 * create an instance of this fragment.
 */
class Login : Fragment() {
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

        val _tvSignUp : TextView = view.findViewById(R.id.tvSignUp)
        _tvSignUp.setOnClickListener {
            val mFragmentManager = parentFragmentManager

            mFragmentManager.beginTransaction().apply {
                replace(R.id.frame_container, SignUp(), SignUp::class.java.simpleName)
                addToBackStack(null)
                commit()
            }
        }

        val sp = requireActivity().getSharedPreferences("user_login_detail", Context.MODE_PRIVATE)
        val _btnMasuk : Button = view.findViewById(R.id.btnMasuk)
        val _edEmail  : EditText = view.findViewById(R.id.edEmail)
        val _edPass : EditText = view.findViewById(R.id.edPassword)

        _btnMasuk.setOnClickListener {
            _btnMasuk.isEnabled = false

            val retrofit = Retrofit.Builder()
                .baseUrl(URL().urlApi)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            val apiService = retrofit.create(APIService::class.java)
            val createPostRequest = SignInRequest(_edEmail.text.toString(), _edPass.text.toString())
            val call = apiService.createPost(createPostRequest)

            call.enqueue(object : Callback<ResponsePost> {
                override fun onResponse(call: Call<ResponsePost>, response: Response<ResponsePost>) {
                    if (response.isSuccessful) {
                        val createdPost = response.body()

                        if (createdPost != null) {
                            if (!createdPost.success)
                                Toast.makeText(
                                    requireContext(),
                                    createdPost.error,
                                    Toast.LENGTH_LONG
                                ).show()
                            else
                            {
                                val gson = Gson()
                                val saveDetail = DetailUser(createdPost.detail.id, createdPost.detail.name, createdPost.detail.email, createdPost.detail.tanggal_bergabung)
                                val saveJson = gson.toJson(saveDetail)

                                val spEditor = sp.edit()
                                spEditor.putString("detail", saveJson)
                                spEditor.apply()

                                startActivity(Intent(requireContext(), MainActivity::class.java))
                                requireActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
                                requireActivity().finish()
                            }
                        }
                    } else {
                        Toast.makeText(requireContext(), "Upss! Error: ${response.code()}", Toast.LENGTH_LONG).show()
                    }

                    _btnMasuk.isEnabled = true
                }

                override fun onFailure(call: Call<ResponsePost>, t: Throwable) {
                    Toast.makeText(requireContext(), "${t.message}", Toast.LENGTH_LONG).show()
                    Log.d("SIGN_UP", "Permintaan gagal: ${t.message}")

                    _btnMasuk.isEnabled = true
                }
            })

        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment Login.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Login().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}