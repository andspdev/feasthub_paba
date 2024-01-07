package com.proyek.paba.feasthub.fragment

import android.os.Bundle
import android.util.Log
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.proyek.paba.feasthub.R
import com.proyek.paba.feasthub.api.URL
import com.proyek.paba.feasthub.api.signup.APIService
import com.proyek.paba.feasthub.api.signup.ResponsePost
import com.proyek.paba.feasthub.api.signup.SignUpRequest
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
 * Use the [SignUp.newInstance] factory method to
 * create an instance of this fragment.
 */
class SignUp : Fragment() {
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
        return inflater.inflate(R.layout.fragment_sign_up, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val _tvSignUp : TextView = view.findViewById(R.id.tvSignIn)
        _tvSignUp.setOnClickListener {
            val mFragmentManager = parentFragmentManager

            mFragmentManager.beginTransaction().apply {
                replace(R.id.frame_container, Login(), Login::class.java.simpleName)
                addToBackStack(null)
                commit()
            }
        }


        val _btnSignUp : Button = view.findViewById(R.id.btnSignUp)
        val _edEmail : EditText = view.findViewById(R.id.edEmail)
        val _edFullName : EditText = view.findViewById(R.id.edFullName)
        val _edPass : EditText = view.findViewById(R.id.edPassword)
        val _edConfirmPass : EditText = view.findViewById(R.id.edConfirmPassword)

        _btnSignUp.setOnClickListener {
            var error = ""
            if (!Patterns.EMAIL_ADDRESS.matcher(_edEmail.text.toString()).matches())
            {
                error = "Please use format email correctly."
            }
            else if (_edFullName.text.toString() == "") {
                error = "The long name is still empty."
            }
            else if (_edPass.text.length < 6 || _edPass.text.length > 12) {
                error = "Characters password minimum 6 and maximum 12."
            }
            else if (_edConfirmPass.text.toString() != _edPass.text.toString())
            {
                error = "Confirm password is not the same."
            }

            if (error != "")
            {
                Toast.makeText(requireContext(), error, Toast.LENGTH_LONG).show()
            }
            else
            {
                _btnSignUp.isEnabled = false

                val retrofit = Retrofit.Builder()
                    .baseUrl(URL().urlApi)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()

                val apiService = retrofit.create(APIService::class.java)
                val createPostRequest = SignUpRequest(_edEmail.text.toString(), _edFullName.text.toString(), _edPass.text.toString())
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
                                    val mFragmentManager = parentFragmentManager

                                    mFragmentManager.beginTransaction().apply {
                                        replace(R.id.frame_container, Login(), Login::class.java.simpleName)
                                        addToBackStack(null)
                                        commit()
                                    }

                                    Toast.makeText(requireContext(), "Registration is successful.", Toast.LENGTH_LONG).show()
                                }
                            }
                        } else {
                            Toast.makeText(requireContext(), "Upss! Error: ${response.code()}", Toast.LENGTH_LONG).show()

                        }

                        _btnSignUp.isEnabled = true
                    }

                    override fun onFailure(call: Call<ResponsePost>, t: Throwable) {
                        Toast.makeText(requireContext(), "${t.message}", Toast.LENGTH_LONG).show()
                        Log.d("SIGN_UP", "Permintaan gagal: ${t.message}")

                        _btnSignUp.isEnabled = true
                    }
                })
            }
        }


    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SignUpFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SignUp().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}