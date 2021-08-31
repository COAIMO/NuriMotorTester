package com.jeongmin.nurimotortester

 interface ISerialProcess{
    /// <summary>
    /// 시작
    /// </summary>
    fun Start()

    /// <summary>
    /// 중지
    /// </summary>
    fun Stop()

    /// <summary>
    /// 작업 추가
    /// </summary>
    /// <param name="badata">전송 데이터</param>
    fun AddTaskqueue(badata: ByteArray?)

    /// <summary>
    /// 작업 제거
    /// </summary>
    fun ClearTaskqueue()
}

