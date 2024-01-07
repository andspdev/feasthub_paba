package com.proyek.paba.feasthub.permission

import android.app.Activity
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class ManagePermissions(
    private val activity: Activity,
    private val list: List<String>,
    private val code: Int
) {
    fun checkPermissions() {
        if (isPermissionsGranted() != PackageManager.PERMISSION_GRANTED) {
            requestPermissions()
        } else {
            Toast.makeText(activity, "Permissions already granted.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun isPermissionsGranted(): Int {
        var counter = 0;

        for (permission in list) {
            counter += ContextCompat.checkSelfPermission(activity, permission)
        }
        return counter
    }

    private fun deniedPermission(): String {
        for (permission in list) {
            if (ContextCompat.checkSelfPermission(activity, permission)
                == PackageManager.PERMISSION_DENIED)
                return permission
        }

        return ""
    }

    private fun requestPermissions() {
        val permission = deniedPermission()

        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
            Toast.makeText(activity, "Should show an explanation.", Toast.LENGTH_SHORT).show()
        } else {
            ActivityCompat.requestPermissions(activity, list.toTypedArray(), code)
        }
    }
}