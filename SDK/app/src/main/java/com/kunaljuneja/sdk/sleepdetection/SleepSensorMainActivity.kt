package com.kunaljuneja.sdk.sleepdetection

import android.Manifest
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Switch
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.wear.ambient.AmbientModeSupport
import com.kunaljuneja.sdk.R
import com.kunaljuneja.sdk.TelUtilSDKConstants
import android.Manifest.permission
import android.app.Application
import android.widget.Toast
import androidx.core.content.ContextCompat.startForegroundService

internal class SleepSensorMainActivity constructor(private val mContext: Application) {

    companion object {
        private const val TAG = "SleepSensorMainActivity"
        private const val CODE_BODY_SENSOR = 1234
    }

    class SleepSensorPermissionCheck : FragmentActivity() {

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)

            checkPermission()
        }

        private fun checkPermission() {
            if(permissionDenied()) {
                ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.BODY_SENSORS), CODE_BODY_SENSOR)
            } else {
                Log.d(TAG, "Permission Already Granted!")
                finish()
            }
        }

        override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<out String>,
            grantResults: IntArray
        ) {
            if(requestCode == CODE_BODY_SENSOR) {
                if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "Body Sensor Permission Granted!")
                    finish()
                } else {
                    Log.d(TAG, "Body Sensor Permission Denied!")
                }
            } else {
                super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            }
        }

        private fun permissionDenied() : Boolean {
            return ContextCompat.checkSelfPermission(this,
                Manifest.permission.BODY_SENSORS)== PackageManager.PERMISSION_DENIED
        }
    }

    init {
        val intent = Intent(mContext, SleepSensorPermissionCheck::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        mContext.startActivity(intent)
    }

    internal fun startSleepSensorListener() {
        val serviceIntent = Intent(mContext, SleepSensorForegroundService::class.java)
        startForegroundService(mContext, serviceIntent)

        val sharedPref = mContext.getSharedPreferences(TelUtilSDKConstants.sharedPrefName, Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putBoolean(TelUtilSDKConstants.isAsleep, false)
            apply()
        }

        Log.d(TAG, "Starting Sensor Foreground Service")
    }

    internal fun removeSleepSensorListener() {
        val serviceIntent = Intent(mContext, SleepSensorForegroundService::class.java)
        mContext.stopService(serviceIntent)
    }

}