package com.jeongmin.nurimotortester

interface SerialReadCallback {
    fun onReceivedData(port_index: Int, data: ByteArray)
}