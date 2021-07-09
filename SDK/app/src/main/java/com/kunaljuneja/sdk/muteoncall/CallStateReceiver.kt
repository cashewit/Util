package com.kunaljuneja.sdk.muteoncall

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.TelephonyManager
import android.util.Log

internal class CallStateReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val state : String? = intent?.getStringExtra(TelephonyManager.EXTRA_STATE)

        Log.d("Receiver Tag", "State : $state")

        if  (state == TelephonyManager.EXTRA_STATE_OFFHOOK) {
            context?.startService(Intent(context, FloatingViewService::class.java))
        } else if (state == TelephonyManager.EXTRA_STATE_IDLE) {
            context?.stopService(Intent(context, FloatingViewService::class.java))
        }
    }
}