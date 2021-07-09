package com.kunaljuneja.sdk.keyboard

import android.bluetooth.BluetoothDevice
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.kunaljuneja.sdk.R

internal class DeviceAdapter(private val context: Context, private val deviceList : List<BluetoothDevice>) : BaseAdapter() {

    override fun getCount(): Int {
        return deviceList.size
    }

    override fun getItem(position: Int): Any {
        return deviceList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, view: View?, parent: ViewGroup?): View {
        val myView = LayoutInflater.from(context).inflate(R.layout.keyboard_list_element_device, null, true)

        myView.findViewById<TextView>(R.id.device_name).text = deviceList[position].name
        myView.findViewById<TextView>(R.id.device_address).text = deviceList[position].address

        return myView
    }
}