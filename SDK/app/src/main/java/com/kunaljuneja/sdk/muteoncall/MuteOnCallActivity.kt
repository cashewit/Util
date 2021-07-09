package com.kunaljuneja.sdk.muteoncall

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.wear.ambient.AmbientModeSupport


internal class MuteOnCallActivity : FragmentActivity(), AmbientModeSupport.AmbientCallbackProvider {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!Settings.canDrawOverlays(this)) {
            val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:$packageName"))

            startActivityForResult(intent, CODE_DRAW_OVERLAY)
        } else {
            Log.d(TAG, "Permission Granted to Draw overlay!")
        }

        checkPermission()
    }

    private fun checkPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_PHONE_STATE), CODE_CALL_PERMISSION)
        } else {
            Log.d(TAG, "Permission Already Granted")
            finish()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<out String>,
                                            grantResults: IntArray)
    {
        if (requestCode == CODE_CALL_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "Permission Granted")
            } else {
                Log.d(TAG, "Permission Denied")
            }
            finish()
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == CODE_DRAW_OVERLAY) {
            if (resultCode == RESULT_OK) {
                Log.d(TAG, "Granting Permission to Draw now!")
            } else {
                Log.e(TAG, "Permission cannot be granted!")
            }
            finish()
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun getAmbientCallback(): AmbientModeSupport.AmbientCallback {
        return object:AmbientModeSupport.AmbientCallback() {}
    }

    companion object {
        private const val TAG = "MuteOnCallActivity"
        private const val CODE_DRAW_OVERLAY = 110
        private const val CODE_CALL_PERMISSION = 1101
    }
}