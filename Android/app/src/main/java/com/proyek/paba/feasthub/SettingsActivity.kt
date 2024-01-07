package com.proyek.paba.feasthub

import android.content.SharedPreferences
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar

class SettingsActivity : AppCompatActivity() {

    private lateinit var edAddressHome: EditText
    private lateinit var checkBoxPushNotif: CheckBox
    private lateinit var checkBoxEmail: CheckBox
    private lateinit var checkBoxSms: CheckBox
    private lateinit var checkBoxLocation: CheckBox
    private lateinit var checkBoxRecom: CheckBox

    private val PREFS_FILENAME = "user_settings"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        edAddressHome = findViewById(R.id.edAddressHome)
        checkBoxPushNotif = findViewById(R.id.checkBoxPushNotif)
        checkBoxEmail = findViewById(R.id.checkBoxEmail)
        checkBoxSms = findViewById(R.id.checkBoxSms)
        checkBoxLocation = findViewById(R.id.checkBoxLocation)
        checkBoxRecom = findViewById(R.id.checkBoxRecom)

        val saveButton: Button = findViewById(R.id.btnSubmit)

        saveButton.setOnClickListener {
            saveData()
            popup()
        }
        displaySavedData()
    }

    private fun saveData() {
        val sharedPreferences: SharedPreferences = getSharedPreferences(PREFS_FILENAME, 0)
        val editor: SharedPreferences.Editor = sharedPreferences.edit()

        val addressHome = edAddressHome.text.toString()
        editor.putString("addressHome", addressHome)

        editor.putBoolean("pushNotif", checkBoxPushNotif.isChecked)
        editor.putBoolean("email", checkBoxEmail.isChecked)
        editor.putBoolean("sms", checkBoxSms.isChecked)
        editor.putBoolean("location", checkBoxLocation.isChecked)
        editor.putBoolean("recom", checkBoxRecom.isChecked)

        editor.apply()

//        Toast.makeText(this, "Data Saved", Toast.LENGTH_SHORT).show()
    }

    private fun displaySavedData() {
        val sharedPreferences: SharedPreferences = getSharedPreferences(PREFS_FILENAME, 0)

        val savedAddress = sharedPreferences.getString("addressHome", "")
        edAddressHome.setText(savedAddress)

        checkBoxPushNotif.isChecked = sharedPreferences.getBoolean("pushNotif", false)
        checkBoxEmail.isChecked = sharedPreferences.getBoolean("email", false)
        checkBoxSms.isChecked = sharedPreferences.getBoolean("sms", false)
        checkBoxLocation.isChecked = sharedPreferences.getBoolean("location", false)
        checkBoxRecom.isChecked = sharedPreferences.getBoolean("recom", false)
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