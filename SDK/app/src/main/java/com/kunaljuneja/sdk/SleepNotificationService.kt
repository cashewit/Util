package com.kunaljuneja.sdk

import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.kunaljuneja.sdk.sleepdetection.ResetBroadcast

internal class SleepNotificationService : Service() {

    private lateinit var delayPendingIntent: PendingIntent
    private lateinit var alarmManager: AlarmManager

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(inten: Intent?, flags: Int, startId: Int): Int {

        alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
        delayPendingIntent = ResetBroadcast.createPendingIntent(this)

        val value = inten?.extras?.getString("value")
        Log.d("NotificationService", "Notification Service Starting!")

        val manager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Sleep Notification",
            NotificationManager.IMPORTANCE_DEFAULT
        )

        manager.createNotificationChannel(channel)

        val intent = Intent()
        val asleepPendingIntent: PendingIntent = PendingIntent.getBroadcast(
            this,
            0,
            intent.apply { action = ASLEEP_ACTION },
            PendingIntent.FLAG_CANCEL_CURRENT
        )
        val awakePendingIntent: PendingIntent = PendingIntent.getBroadcast(
            this,
            1,
            intent.apply { action = AWAKE_ACTION },
            PendingIntent.FLAG_CANCEL_CURRENT
        )

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("Sleep Detected")
            .setContentText("Value: $value. Turning off TV in 30 sec. Tap to stop.")
            .setContentIntent(awakePendingIntent)
            .setTimeoutAfter(30000L)
            .setOngoing(true)
            .setDeleteIntent(asleepPendingIntent)
            .build()

        manager.notify(2, notification)

        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                when (intent?.action) {
                    AWAKE_ACTION -> {
                        Log.d("SleepNotificationService", "User Awake")
                        manager.cancelAll()

                        createAlarmDelay(TelUtilSDKConstants.repeatDurationMillis)

                    }
                    ASLEEP_ACTION -> {
                        Log.d("MyReceiver", "User Asleep")
                        TelUtilSDK.sleepDetected()

                        createAlarmDelay(TelUtilSDKConstants.sleepDurationMillis)
                    }

                }
                Log.d("SleepNotificationService", "Receiver Unregistered")
                unregisterReceiver(this)
                this@SleepNotificationService.stopSelf()
            }
        }

        registerReceiver(receiver, IntentFilter("delete"))
        registerReceiver(receiver, IntentFilter("accept"))


        return START_NOT_STICKY
    }

    private fun createAlarmDelay(delay : Long) {
        alarmManager.set(
            AlarmManager.RTC,
            System.currentTimeMillis() + delay,
            delayPendingIntent
        )
    }

    companion object {
        private const val CHANNEL_ID: String = "Main Notification Channel"
        private const val ASLEEP_ACTION = "delete"
        private const val AWAKE_ACTION = "accept"

    }
}