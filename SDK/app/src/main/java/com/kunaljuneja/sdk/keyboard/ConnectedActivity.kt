package com.kunaljuneja.sdk.keyboard

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import androidx.fragment.app.FragmentActivity
import androidx.wear.ambient.AmbientModeSupport
import com.kunaljuneja.sdk.R
import java.lang.Exception

internal class ConnectedActivity : FragmentActivity(), AmbientModeSupport.AmbientCallbackProvider {

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action.equals(BluetoothController.BT_DISCONNECTED)) {
                finish()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.keyboard_activity_connected)

        registerReceiver(receiver, IntentFilter(BluetoothController.BT_DISCONNECTED))

        val editText = findViewById<EditText>(R.id.inputEditText)
        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                return
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (count < before) {
                    Log.d(TAG, "Backspace Detected")
                    BluetoothController.keyboardSender.sendKey(KeyboardSender.Key.BACKSPACE)
                } else {
                    try {
                        val key = s?.get(s.length - 1)
                        Log.d(TAG, "Character : $key")
                        if (key != null) {
                            BluetoothController.keyboardSender.sendChar(key)
                        }
                    } catch (e: Exception) {

                    }
                }
            }

            override fun afterTextChanged(p0: Editable?) {
                return
            }

        })
        editText.setOnEditorActionListener{ _, actionId: Int, _ ->
            when(actionId){
                EditorInfo.IME_ACTION_DONE -> {
                    BluetoothController.keyboardSender.sendKey(KeyboardSender.Key.ENTER)
                    true
                }
                 else -> false
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(receiver)
        BluetoothController.disconnectDevice()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        Log.d(TAG, "Event: $event")
        when (keyCode) {
            KeyEvent.KEYCODE_DEL -> BluetoothController.keyboardSender.sendKey(KeyboardSender.Key.BACKSPACE)
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun getAmbientCallback(): AmbientModeSupport.AmbientCallback {
        return object:AmbientModeSupport.AmbientCallback() {}
    }

    companion object {
        private const val TAG = "ConnectedActivity"
    }
}