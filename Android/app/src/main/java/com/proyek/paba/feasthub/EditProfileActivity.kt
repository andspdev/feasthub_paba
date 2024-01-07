package com.proyek.paba.feasthub

import android.content.Context
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.gson.Gson
import com.proyek.paba.feasthub.api.URL
import com.proyek.paba.feasthub.api.edit_user.APIService
import com.proyek.paba.feasthub.api.edit_user.EditUserRequest
import com.proyek.paba.feasthub.api.edit_user.ResponsePost
import com.proyek.paba.feasthub.api.signin.DetailUser
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class EditProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }


        val sp = getSharedPreferences("user_login_detail", Context.MODE_PRIVATE)

        val _edEmail : EditText = findViewById(R.id.edEmail)
        val _edFullName : EditText = findViewById(R.id.edFullName)
        val _edPass : EditText = findViewById(R.id.edPassword)

        if (sp.contains("detail"))
        {
            val gson = Gson()
            val getJson= sp.getString("detail", null)
            val jsonToString = gson.fromJson(getJson, DetailUser::class.java)

            val idUser = jsonToString.id

            _edEmail.setText(jsonToString.email)
            _edFullName.setText(jsonToString.name)

            val _btnSave: Button = findViewById(R.id.btnSimpann)
            _btnSave.setOnClickListener {
                var error = ""
                if (!Patterns.EMAIL_ADDRESS.matcher(_edEmail.text.toString()).matches())
                {
                    error = "Please use format email correctly."
                }
                else if (_edFullName.text.toString() == "") {
                    error = "The long name is still empty."
                }
                else if (_edPass.text.toString() != "" &&
                (_edPass.text.length < 6 || _edPass.text.length > 12)) {
                    error = "Characters password minimum 6 and maximum 12."
                }

                if (error != "")
                {
                    Toast.makeText(this@EditProfileActivity, error, Toast.LENGTH_LONG).show()
                }
                else {
                    _btnSave.isEnabled = false

                    val retrofit = Retrofit.Builder()
                        .baseUrl(URL().urlApi)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build()

                    val apiService = retrofit.create(APIService::class.java)
                    val createPostRequest = EditUserRequest(idUser, _edEmail.text.toString(), _edFullName.text.toString(), _edPass.text.toString())
                    val call = apiService.createPost(createPostRequest)

                    call.enqueue(object : Callback<ResponsePost> {
                        override fun onResponse(call: Call<ResponsePost>, response: Response<ResponsePost>) {
                            if (response.isSuccessful) {
                                val createdPost = response.body()

                                if (createdPost != null) {
                                    if (!createdPost.success)
                                        Toast.makeText(
                                            this@EditProfileActivity,
                                            createdPost.error,
                                            Toast.LENGTH_LONG
                                        ).show()
                                    else
                                    {
                                        popup()

                                        val closeKeyboard = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                                        closeKeyboard.hideSoftInputFromWindow(currentFocus?.windowToken, 0)

//                                        Toast.makeText(this@EditProfileActivity, "Successfully saved your profile.", Toast.LENGTH_LONG).show()

                                        val saveDetail = DetailUser(idUser, _edFullName.text.toString(), _edEmail.text.toString(), jsonToString.tanggal_bergabung)
                                        val saveJson = gson.toJson(saveDetail)

                                        val spEditor = sp.edit();
                                        spEditor.putString("detail", saveJson)
                                        spEditor.apply()
                                    }
                                }
                            } else {
                                Toast.makeText(this@EditProfileActivity, "Upss! Error: ${response.code()}", Toast.LENGTH_LONG).show()

                            }

                            _btnSave.isEnabled = true
                        }

                        override fun onFailure(call: Call<ResponsePost>, t: Throwable) {
                            Toast.makeText(this@EditProfileActivity, "${t.message}", Toast.LENGTH_LONG).show()
                            _btnSave.isEnabled = true
                        }
                    })
                }
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
        _tvPopUp.text = "Data Changes Have Been Saved"

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