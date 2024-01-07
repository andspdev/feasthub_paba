package com.proyek.paba.feasthub

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.proyek.paba.feasthub.fragment.Home
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.proyek.paba.feasthub.fragment.Login
import com.proyek.paba.feasthub.fragment.NotLogin
import com.proyek.paba.feasthub.fragment.Transaction
import com.proyek.paba.feasthub.fragment.Wishlists


class MainActivity : AppCompatActivity() {
    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        ubahFragment(Home())

        //  Cek User Login
        val sp = getSharedPreferences("user_login_detail", Context.MODE_PRIVATE)
        bottomNavigationView = findViewById(R.id.bottomNavigationView)
        val cekMasuk: Boolean = sp.contains("detail")

        if (cekMasuk)
        {
            bottomNavigationView.menu.findItem(R.id.profile)
                .setIcon(R.drawable.circle_user_filled)
        }

        bottomNavigationView.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.home -> {
                    ubahFragment(Home())
                    true
                }

                R.id.wishlists -> {
                    if (cekMasuk) {
                        ubahFragment(Wishlists())
                    }
                    else {
                        ubahFragment(NotLogin())
                    }
                    true
                }

                R.id.transaction -> {
                    if (cekMasuk) {
                        ubahFragment(Transaction())
                    }
                    else {
                        ubahFragment(NotLogin())
                    }
                    true
                }

                R.id.profile -> {
                    if (cekMasuk) {
                        startActivity(Intent(this@MainActivity, ProfileActivity::class.java))
                    }
                    else {
                        ubahFragment(Login())
                    }
                    true
                }
                else -> false
            }
        }
    }

    override fun onResume() {
        super.onResume()

        bottomNavigationView.selectedItemId = R.id.home
    }

    private fun ubahFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager

        fragmentManager.beginTransaction()
            .replace(R.id.frame_container, fragment)
            .commit()
    }
}