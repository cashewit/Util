package com.kunaljuneja.sdk

import android.app.Application
import android.content.Context
import android.content.Intent
import android.util.Log
import com.kunaljuneja.sdk.keyboard.KeyboardMainActivity
import com.kunaljuneja.sdk.muteoncall.MuteOnCallActivity
import com.kunaljuneja.sdk.sleepapi.SleepAPIMainActivity
import com.kunaljuneja.sdk.sleepdetection.SleepSensorMainActivity


object TelUtilSDK {

    private lateinit var sleepSensorActivity: SleepSensorMainActivity
    private lateinit var sleepApiActivity: SleepAPIMainActivity

    private lateinit var applicationContext: Application
    private lateinit var mOnMuteCallback: () -> Unit
    private lateinit var mOnUnMuteCallback: () -> Unit
    private lateinit var mOnSleepDetectedCallback: () -> Unit

    fun init(applicationContext: Context) {
        this.applicationContext = applicationContext as Application
        setMutePermissions()
        setSleepSensorPermissions()
        setSleepAPIPermissions()
    }

    fun startBluetoothKeyboard() {
        val intent = Intent(this.applicationContext, KeyboardMainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }

        this.applicationContext.startActivity(intent)
    }

    fun setOnMuteListener(onMuteCallback: () -> Unit): TelUtilSDK {
        mOnMuteCallback = onMuteCallback
        return this
    }

    fun setOnUnMuteListener(onUnMuteCallback: () -> Unit): TelUtilSDK {
        mOnUnMuteCallback = onUnMuteCallback
        return this
    }

    fun setOnSleepDetected(onSleepDetectedCallback: () -> Unit) {
        mOnSleepDetectedCallback = onSleepDetectedCallback
    }

    fun stopSleepDetectionSensor() {
        sleepSensorActivity.removeSleepSensorListener()
    }

    fun stopSleepDetectionSleepAPI() {
        sleepApiActivity.unregisterSleepActivityListener(this.applicationContext)
    }

    private fun setMutePermissions(): TelUtilSDK {
        val intent = Intent(applicationContext, MuteOnCallActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        this.applicationContext.startActivity(intent)

        return this
    }

    private fun setSleepAPIPermissions() {
        sleepApiActivity = SleepAPIMainActivity(this.applicationContext)
        sleepApiActivity.startSleepActivityListener(this.applicationContext)
    }

    private fun setSleepSensorPermissions() {
        sleepSensorActivity = SleepSensorMainActivity(this.applicationContext)
        sleepSensorActivity.startSleepSensorListener()
    }

    internal enum class State { MUTE, UNMUTE }

    internal var state = State.UNMUTE

    internal fun sleepDetected() {
        mOnSleepDetectedCallback()
    }


    internal fun onMute() {
        mOnMuteCallback()
        state = State.MUTE
    }

    internal fun onUnMute() {
        mOnUnMuteCallback()
        state = State.UNMUTE
    }
}
