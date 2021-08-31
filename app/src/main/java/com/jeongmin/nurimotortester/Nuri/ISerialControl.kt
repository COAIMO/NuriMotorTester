package com.jeongmin.nurimotortester.Nuri


import android.database.Observable
import android.util.Log
import androidx.databinding.ObservableField
import kotlin.properties.Delegates

class ISerialControl {

    /// <summary>
    /// 에러 발생
    /// </summary>
    /// <value>발생 에러</value>
//    IObservable<Exception> ObsErrorReceived { get; }
//    var ObsErrorReceived: Exception by Delegates.observable(NumberFormatException)

    /// <summary>
    /// 포트 열림
    /// </summary>
    /// <value>포트 열림 여부</value>
//    IObservable<bool> ObsIsOpenObservable { get; }
    var ObsIsOpenObservable: Boolean by Delegates.observable(false) { property, oldValue, newValue ->
        Log.d("TAG", "oldValue:$oldValue")
        Log.d("TAG", "newValue:$newValue")

    }

    //    bool IsOpen { get; }
    var IsOpen: Boolean = false


    /// <summary>
    /// 시리얼 연결 초기화
    /// </summary>
    /// <param name="serialPortSetting"></param>
//    void Init(SerialPortSetting serialPortSetting);


    /// <summary>
    /// 시리얼 연결
    /// </summary>
    /// <returns>작업</returns>
//    Task Connect();
    val Connenct: Thread = Thread()


    /// <summary>
    /// 시리얼 연결 해제
    /// </summary>
//    void Disconnect();
    fun Disconnect() {}

    /// <summary>
    /// 데이터 전송
    /// </summary>
    /// <param name="baData">전달 데이터</param>
    /// <param name="iStart">데이터 시작위치</param>
    /// <param name="iLength">데이터 크기</param>
//    void Send(byte[] baData, int iStart = 0, int iLength = -1);
    fun Send(baData: ByteArray, iStart: Int = 0, iLength: Int = -1) {}


    //    IObservable<byte[]> ObsProtocolReceived { get; }
    var ObsProtocolReceived: ByteArray by Delegates.observable("123".toByteArray()) { property, oldValue, newValue ->
        Log.d("TAG", "oldValue:$oldValue")
        Log.d("TAG", "newValue:$newValue")
    }

}


