package com.jeongmin.nurimotortester

import android.util.Log
import com.jeongmin.nurimotortester.Nuri.AppState
import java.util.*


class DeviceSearch {
    var rates = arrayOf<String>(
        "1000000",
        "500000",
        "250000",
        "230400",
        "115200",
        "76800",
        "57600",
        "38400",
        "28800",
        "19200",
        "14400",
        "9600",
        "4800",
        "2400",
        "1200",
        "600",
        "300" //,"110"
    )
    val TAG = "태그"
    init {
        var state = AppState()
        Log.d(TAG, "Device find Start =======================")
        Log.d(TAG, "Comport : {0}\", state.Comport")

        val addv = rates.indexOf(state.Baudrate)
        for (i in 0..rates.size){
            var item = rates[i % rates.size]

        }
        var item = rates
    }
}