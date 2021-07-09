package com.kunaljuneja.sdk.muteoncall

import android.app.Service
import android.content.Intent
import android.graphics.PixelFormat
import android.os.IBinder
import android.util.Log
import android.view.*
import android.widget.ImageView
import com.kunaljuneja.sdk.R
import com.kunaljuneja.sdk.TelUtilSDK

internal class FloatingViewService() : Service() {
    private lateinit var windowManager: WindowManager
    private lateinit var floatingView : View

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        floatingView = LayoutInflater.from(this).inflate(R.layout.muteoncall_layout_floating_view, null)

        val params: WindowManager.LayoutParams = WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT
        )

        params.gravity = Gravity.TOP or Gravity.START
        params.x = 20
        params.y = 100

        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        windowManager.addView(floatingView, params)

        val muteButton = floatingView.findViewById<ImageView>(R.id.mute_unmute)

        floatingView.setOnTouchListener (object: View.OnTouchListener {
            var initX = params.x
            var initY = params.y
            var initTX = params.x
            var initTY = params.y

            override fun onTouch(view:View, event:MotionEvent): Boolean {
                return when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        initX = params.x
                        initY = params.y

                        initTX = event.rawX.toInt()
                        initTY = event.rawY.toInt()
                        true
                    }

                    MotionEvent.ACTION_UP -> {
                        val diffX = (event.rawX - initTX).toInt()
                        val diffY = (event.rawY - initTY).toInt()

                        if(diffX < 10 && diffY < 10) {
                            if (TelUtilSDK.state == TelUtilSDK.State.UNMUTE) {
                                TelUtilSDK.onMute()
                                muteButton.setImageResource(R.drawable.ic_baseline_music_off_24)
                            } else {
                                TelUtilSDK.onUnMute()
                                muteButton.setImageResource(R.drawable.ic_baseline_music_note_24)
                            }
                        }
                        true
                    }

                    MotionEvent.ACTION_MOVE -> {
                        params.x = initX + (event.rawX - initTX).toInt()
                        params.y = initY + (event.rawY - initTY).toInt()
                        windowManager.updateViewLayout(floatingView, params)
                        true
                    }
                    else -> false
                }
            }
        })

    }

    override fun onDestroy() {
        super.onDestroy()
        if (floatingView != null) {
            windowManager.removeView(floatingView)
        }
    }

    companion object {
        private const val TAG = "FloatingService"
    }
}
