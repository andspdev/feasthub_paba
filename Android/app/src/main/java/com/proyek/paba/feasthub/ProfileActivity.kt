package com.proyek.paba.feasthub

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import com.google.gson.Gson
import com.proyek.paba.feasthub.api.signin.DetailUser

class ProfileActivity : AppCompatActivity() {
    private lateinit var _idUser: TextView
    private lateinit var _namaUserHead: TextView
    private lateinit var _namaUser: TextView
    private lateinit var _emailUser: TextView
    private lateinit var _tglBergabungUser: TextView
    private lateinit var sp : SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        sp = getSharedPreferences("user_login_detail", Context.MODE_PRIVATE)

        _idUser = findViewById(R.id.tvIdPengguna)
        _namaUserHead = findViewById(R.id.tvNameHead)
        _namaUser = findViewById(R.id.tvNamaPengguna)
        _emailUser  = findViewById(R.id.tvEmail)
        _tglBergabungUser = findViewById(R.id.tvBergabung)

        if (sp.contains("detail"))
            this.loadDataUser()

        val _btnLogout : Button = findViewById(R.id.btnLogout)
        _btnLogout.setOnClickListener {

            AlertDialog.Builder(this@ProfileActivity)
                .setTitle("Sign Out")
                .setMessage("Are you sure you want to sign out of your account?")
                .setPositiveButton("Yes") { _, _ ->
                    val spEdit = sp.edit()
                    spEdit.remove("detail")
                    spEdit.remove("shopping_cart")
                    spEdit.apply()

                    startActivity(Intent(this@ProfileActivity, MainActivity::class.java))
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
                    finish()
                }
                .setNegativeButton("No") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }

        val _btnSetting : Button  = findViewById(R.id.btnSetting)
        _btnSetting.setOnClickListener {
            startActivity(Intent(this@ProfileActivity, SettingsActivity::class.java))
        }

        val _editProfile: TextView = findViewById(R.id.tvEditProfile)
        _editProfile.setOnClickListener {
            startActivity(Intent(this@ProfileActivity, EditProfileActivity::class.java))
        }

        val _btnAddItemToHome: Button = findViewById(R.id.btnAddItemToHome)
        _btnAddItemToHome.setOnClickListener {
            startActivity(Intent(this@ProfileActivity, AddItemsActivity::class.java))
        }
    }

    private fun loadDataUser() {
        val gson = Gson()
        val getJson= sp.getString("detail", null)
        val jsonToString = gson.fromJson(getJson, DetailUser::class.java)

        _idUser.text = jsonToString.id
        _namaUserHead.text = jsonToString.name
        _namaUser.text = jsonToString.name
        _emailUser.text = jsonToString.email
        _tglBergabungUser.text = jsonToString.tanggal_bergabung
    }

    override fun onResume() {
        super.onResume()
        this.loadDataUser()
    }
}