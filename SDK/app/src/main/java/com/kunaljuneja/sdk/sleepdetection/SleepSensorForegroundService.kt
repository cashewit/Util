package com.kunaljuneja.sdk.sleepdetection

import android.app.*
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.kunaljuneja.sdk.R
import com.kunaljuneja.sdk.SleepNotificationService
import com.kunaljuneja.sdk.TelUtilSDKConstants

internal class SleepSensorForegroundService : Service(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private var mHeartRate : Sensor? = null

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flag: Int, startId: Int): Int {
        Log.d(TAG, "Starting Foreground Sensor Service")
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        mHeartRate = sensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE)

        Log.d(TAG, "Sensor: $mHeartRate")

        val register = sensorManager.registerListener(this, mHeartRate, SensorManager.SENSOR_DELAY_FASTEST)
        Log.d(TAG, "registering Listener: $register")


        /* ---------------------------------
        Done with registering Sensor and Listener
        ------------------------------------*/


        createNotificationChannel()


        val notification : Notification =
            NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Sleep Detection")
                .setContentText("Detecting Sensor Values")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setOngoing(true)
                .build()

        startForeground(1, notification)

        return START_STICKY
    }

    private fun createNotificationChannel() {
        val serviceChannel = NotificationChannel(
                CHANNEL_ID,
                "Foreground Service Channel",
                NotificationManager.IMPORTANCE_MIN
            )

        val manager : NotificationManager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(serviceChannel)

    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy Called! Unregistering Sensor Listener")
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        Log.d("SensorEventListener", "Sensor Changed Event Called!")
        val values = event?.values!![0]
        Log.d("SensorEventListener", "Sensor Values: $values")

        val asleep = getSharedPreferences(TelUtilSDKConstants.sharedPrefName, Context.MODE_PRIVATE)
            .getBoolean(TelUtilSDKConstants.isAsleep, true)

        if(values in TelUtilSDKConstants.sensorRange && !asleep) {
            val sharedPref = getSharedPreferences(TelUtilSDKConstants.sharedPrefName, Context.MODE_PRIVATE)
            with(sharedPref.edit()) {
                putBoolean(TelUtilSDKConstants.isAsleep, true)
                apply()
            }

            val bundle = Bundle()
            bundle.putString("value", "$values")

            val notifyIntent = Intent(this, SleepNotificationService::class.java)
            notifyIntent.putExtras(bundle)
            startService(notifyIntent)
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        Log.d("SensorEventListener", "Sensor: $sensor\nAccuracy: $accuracy")
    }

    companion object {
        private const val TAG = "MyServiceSensor"
        const val CHANNEL_ID : String = "ForegroundServiceChannel"
        private const val NOTIFICATION_ID = 10
    }
}