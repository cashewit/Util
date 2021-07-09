package com.kunaljuneja.sdk.sleepapi

import android.Manifest
import android.annotation.SuppressLint
import android.app.Application
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Switch
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.wear.ambient.AmbientModeSupport
import com.google.android.gms.location.ActivityRecognition
import com.google.android.gms.location.SleepSegmentRequest
import com.kunaljuneja.sdk.R
import com.kunaljuneja.sdk.sleepdetection.SleepSensorMainActivity

internal class SleepAPIMainActivity constructor(private val mContext: Application) {

    private var sleepIntent: PendingIntent

    companion object {
        private const val TAG = "Sleep_API_MainActivity"
        private const val ACTIVITY_REQUEST_CODE = 1101
    }

    class SleepAPIPermissionCheck : FragmentActivity() {
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)

            val permissions = arrayOf(
                Manifest.permission.ACTIVITY_RECOGNITION,
                Manifest.permission.FOREGROUND_SERVICE
            )

            if (!hasPermissions(this, permissions)) {
                ActivityCompat.requestPermissions(
                    this,
                    permissions,
                    ACTIVITY_REQUEST_CODE
                )
            } else {
                Log.d(TAG, "Permission Already Granted")
            }

            Log.d(TAG, "checkPermission sensor")
            this.finish()
        }

        private fun hasPermissions(context: Context, permissions: Array<String>): Boolean {
            return permissions.all {
                ActivityCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
            }
        }

        override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<out String>,
            grantResults: IntArray
        ) {
            if (requestCode == ACTIVITY_REQUEST_CODE) {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "Permission Granted")
                    finish()
                } else {
                    Log.d(TAG, "Permission Denied")
                    finish()
                }
            } else {
                super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            }
        }
    }

    init {
        val intent = Intent(mContext, SleepAPIPermissionCheck::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        mContext.startActivity(intent)

        sleepIntent = SleepReceiver.createPendingIntent(mContext)
    }

    @SuppressLint("MissingPermission")
    internal fun unregisterSleepActivityListener(context: Context) {
        val task = ActivityRecognition.getClient(context).removeSleepSegmentUpdates(sleepIntent)

        task.addOnSuccessListener {
            Log.d(TAG, "Successfully Removed Listener!")
        }

        task.addOnFailureListener {
            Log.d(TAG, "Exception in listener: $it")
        }
    }

    @SuppressLint("MissingPermission")
    internal fun startSleepActivityListener(context: Context) {
        Log.d(TAG, "Staring Sleep Activity Listener!")

        val task = ActivityRecognition.getClient(context).requestSleepSegmentUpdates(
            sleepIntent,
            SleepSegmentRequest.getDefaultSleepSegmentRequest()
        )

        task.addOnSuccessListener {
            Log.d(TAG, "Successfully Started Listener!")
        }

        task.addOnFailureListener {
            Log.d(TAG, "Exception in listener: $it")
        }
    }
}