package com.iraimjanov.alarm.utils

import android.Manifest
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentActivity

class PermissionsService {
    fun checkPermission(activity: FragmentActivity):Boolean{
        return ActivityCompat.checkSelfPermission(activity , Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
    }
}