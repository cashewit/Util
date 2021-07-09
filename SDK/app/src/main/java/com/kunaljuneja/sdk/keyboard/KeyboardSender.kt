package com.kunaljuneja.sdk.keyboard

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothHidDevice
import android.util.Log

internal class KeyboardSender(
    private val bluetoothHidDevice: BluetoothHidDevice?,
    private val host: BluetoothDevice?
) {

    private val keyboardReport = KeyboardReport()

    object Modifier {
        const val NONE = 0;
        const val LEFT_CTRL = (1 shl 0);
        const val LEFT_SHIFT = (1 shl 1);
        const val LEFT_ALT = (1 shl 2);
        const val LEFT_GUI = (1 shl 3);
        const val RIGHT_CTRL = (1 shl 4);
        const val RIGHT_SHIFT = (1 shl 5);
        const val RIGHT_ALT = (1 shl 6);
        const val RIGHT_GUI = (1 shl 7);
    }

    object Key {
        const val ENTER = 40
        const val ESCAPE = 41
        const val BACKSPACE = 42
        const val TAB = 43
        const val SPACE = 44
        const val RIGHT = 79
        const val LEFT = 80
        const val DOWN = 81
        const val UP = 82
    }

    private val keyMap = mapOf<Char, Int>(
        'a' to 0x04,
        'b' to 0x05,
        'c' to 0x06,
        'd' to 0x07,
        'e' to 0x08,
        'f' to 0x09,
        'g' to 0x0A,
        'h' to 0x0B,
        'i' to 0x0C,
        'j' to 0x0D,
        'k' to 0x0E,
        'l' to 0x0F,
        'm' to 0x10,
        'n' to 0x11,
        'o' to 0x12,
        'p' to 0x13,
        'q' to 0x14,
        'r' to 0x15,
        's' to 0x16,
        't' to 0x17,
        'u' to 0x18,
        'v' to 0x19,
        'w' to 0x1A,
        'x' to 0x1B,
        'y' to 0x1C,
        'z' to 0x1D,
        '1' to 0x1E,
        '2' to 0x1F,
        '3' to 0x20,
        '4' to 0x21,
        '5' to 0x22,
        '6' to 0x23,
        '7' to 0x24,
        '8' to 0x25,
        '9' to 0x26,
        '0' to 0x27,
        ' ' to 0x2C,
        '-' to 0x2D,
        '=' to 0x2E,
        '[' to 0x2F,
        ']' to 0x30,
        '\\' to 0x31,
        ';' to 0x33,
        '\'' to 0x34,
        '`' to 0x35,
        ',' to 0x36,
        '.' to 0x37,
        '/' to 0x38
    )
    private val shiftKeyMap = mapOf<Char, Int>(
        'A' to 0x04,
        'B' to 0x05,
        'C' to 0x06,
        'D' to 0x07,
        'E' to 0x08,
        'F' to 0x09,
        'G' to 0x0A,
        'H' to 0x0B,
        'I' to 0x0C,
        'J' to 0x0D,
        'K' to 0x0E,
        'L' to 0x0F,
        'M' to 0x10,
        'N' to 0x11,
        'O' to 0x12,
        'P' to 0x13,
        'Q' to 0x14,
        'R' to 0x15,
        'S' to 0x16,
        'T' to 0x17,
        'U' to 0x18,
        'V' to 0x19,
        'W' to 0x1A,
        'X' to 0x1B,
        'Y' to 0x1C,
        'Z' to 0x1D,
        '!' to 0x1E,
        '@' to 0x1F,
        '#' to 0x20,
        '$' to 0x21,
        '%' to 0x22,
        '^' to 0x23,
        '&' to 0x24,
        '*' to 0x25,
        '(' to 0x26,
        ',' to 0x27,
        '_' to 0x2D,
        '+' to 0x2E,
        '{' to 0x2F,
        '}' to 0x30,
        '|' to 0x31,
        ':' to 0x33,
        '"' to 0x34,
        '~' to 0x35,
        '<' to 0x36,
        '>' to 0x37,
        '?' to 0x38
    )

    fun sendChar(key: Char) {
        var shift = false
        var code = keyMap.get(key)
        if(code == null) {
            shift = true
            code = shiftKeyMap.get(key)
            if(code == null){
                return
            }
        }

        sendKeysDown(if(shift) Modifier.LEFT_SHIFT else Modifier.NONE, code)
        sendKeysDown(Modifier.NONE)
    }

    fun sendKey(key: Int) {
        sendKeysDown(Modifier.NONE, key)
        sendKeysDown(Modifier.NONE)
    }

    private fun sendKeysDown(modifier: Int, key1: Int = 0, key2: Int = 0, key3: Int = 0, key4: Int = 0, key5: Int = 0, key6: Int = 0) {
        val report = keyboardReport.setValue(modifier, key1, key2, key3, key4, key5, key6)
        if(bluetoothHidDevice != null && host != null) {
            val reportSent = bluetoothHidDevice.sendReport(host, DescriptorCollection.ID_KEYBOARD.toInt(), report)
            Log.d(TAG, "Report Status: $reportSent")
            if(!reportSent) {
                Log.e(TAG, "Error in sending report")
            }
        } else {
            Log.e(TAG, "Device Not Found")
        }
    }

    companion object {
        private const val TAG = "KeyboardSender"
    }
}