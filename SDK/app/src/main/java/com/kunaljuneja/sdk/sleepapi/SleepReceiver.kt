package com.kunaljuneja.sdk.sleepapi

import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.google.android.gms.location.SleepClassifyEvent
import com.kunaljuneja.sdk.SleepNotificationService
import com.kunaljuneja.sdk.TelUtilSDKConstants

internal class SleepReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d(TAG, "BroadcastReceiver Started!")

        val asleep = context?.getSharedPreferences(TelUtilSDKConstants.sharedPrefName, Context.MODE_PRIVATE)
            ?.getBoolean(TelUtilSDKConstants.isAsleep, true)

        if (SleepClassifyEvent.hasEvents(intent) && !(asleep!!)) {
            val sleepClassifyEvent: List<SleepClassifyEvent> =
                SleepClassifyEvent.extractEvents(intent!!)

            Log.d(TAG, "SleepClassifyEvents : ${sleepClassifyEvent[0].confidence}")

            val bundle = Bundle()
            bundle.putString("value", "${sleepClassifyEvent[0].confidence}")

            context.startService(Intent(context, SleepNotificationService::class.java).putExtras(bundle))
        }
    }

    companion object {
        private const val TAG = "SleepReceiver"

        fun createPendingIntent(context: Context): PendingIntent {
            val intent = Intent(context, SleepReceiver::class.java)

            return PendingIntent.getBroadcast(
                context, 0,
                intent, PendingIntent.FLAG_CANCEL_CURRENT
            )
        }
    }
}

