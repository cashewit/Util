package com.kunaljuneja.sdk

import android.hardware.Sensor

class TelUtilSDKConstants {

    companion object {
        internal const val sharedPrefName = "STATUS"
        internal const val isAsleep = "USER_STATUS"

        var repeatDurationMillis: Long = 1800000L
        var sleepDurationMillis : Long = 28800000L

        var sensorType : Int = Sensor.TYPE_HEART_RATE
        var sensorRange : ClosedFloatingPointRange<Double> = 40.0..60.0

    }
}