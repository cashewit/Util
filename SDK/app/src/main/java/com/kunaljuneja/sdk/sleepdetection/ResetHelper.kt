package com.kunaljuneja.sdk.sleepdetection

import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.IBinder
import com.kunaljuneja.sdk.TelUtilSDKConstants

internal class ResetBroadcast : BroadcastReceiver() {

    override fun onReceive(context: Context?, p0: Intent?) {
        val sharedPref = context?.getSharedPreferences(TelUtilSDKConstants.sharedPrefName, Context.MODE_PRIVATE)
        with(sharedPref?.edit()) {
            this?.putBoolean(TelUtilSDKConstants.isAsleep, false)
            this?.apply()
        }
    }

    companion object {
        internal fun createPendingIntent(context: Context): PendingIntent {
            val intent = Intent(context, ResetBroadcast::class.java)

            return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT)
        }
    }
}