package com.jeongmin.nurimotortester

import android.util.Log
import com.jeongmin.nurimotortester.Nuri.*
import java.lang.Math.round
import java.nio.ByteBuffer
import kotlin.experimental.inv


class NurirobotMC : ICommand {
    val TAG = "TAG"
    override var PacketName: String? = null
    override var Data: ByteArray? = null
    override var ID: Byte? = null

    override fun GetCheckSum(): Byte {
        if (Data == null) return 0
        else if (Data!!.size >= 0) {
            val sumval: UInt = Data!!.toUByteArray().sum() - Data!![0].toUByte() - Data!![1].toUByte() - Data!![4].toUByte()
            return sumval.toByte().inv()
        } else return 0
    }

    // <summary>
    // 프로토콜 작성
    // </summary>
    /// <param name="id">장비 id</param>
    // <param name="size">프로토콜 사이즈</param>
    // <param name="mode">프로토콜 모드</param>
    // <param name="data">프로토콜 데이터</param>
    // <param name="isSend">시리얼 포트 전달여부 기본 : 전송</param>
    fun BuildProtocol(id: Byte, size: Byte, mode: Byte, data: ByteArray, isSend: Boolean = true) {
        val protocolSize: Int = 6 + data.size
        Data = ByteArray(protocolSize)
        Data!![0] = 0xFF.toByte()
        Data!![1] = 0xFE.toByte()
        Data!![2] = id
        Data!![3] = size
        Data!![5] = mode

        data.copyInto(Data!!, 6, 0, data.size)
        Data!![4] = GetCheckSum()
    }

    /// <summary>
    /// 통신속도
    /// </summary>
    /// <param name="arg">Baudrate 열거형</param>
    /// <returns>실제 통신 bps</returns>
    fun GetBaudrate(arg: Byte): Int {
        when (BaudrateByte.codesMap[arg]) {
            BaudrateByte.BR_110 -> return 100
            BaudrateByte.BR_300 -> return 300
            BaudrateByte.BR_600 -> return 600
            BaudrateByte.BR_1200 -> return 1200
            BaudrateByte.BR_2400 -> return 2400
            BaudrateByte.BR_4800 -> return 4800
            BaudrateByte.BR_9600 -> return 9600
            BaudrateByte.BR_14400 -> return 14400
            BaudrateByte.BR_19200 -> return 19200
            BaudrateByte.BR_28800 -> return 28800
            BaudrateByte.BR_38400 -> return 38400
            BaudrateByte.BR_57600 -> return 57600
            BaudrateByte.BR_76800 -> return 76800
            BaudrateByte.BR_115200 -> return 115200
            BaudrateByte.BR_230400 -> return 230400
            BaudrateByte.BR_250000 -> return 250000
            BaudrateByte.BR_500000 -> return 500000
            BaudrateByte.BR_1000000 -> return 10000000
            else -> return 0
        }
    }

    fun GetBaudrateProtocol(arg: Int): Byte {
        when (arg) {
            110 -> return BaudrateByte.BR_110.byte
            300 -> return BaudrateByte.BR_300.byte
            600 -> return BaudrateByte.BR_600.byte
            1200 -> return BaudrateByte.BR_1200.byte
            2400 -> return BaudrateByte.BR_2400.byte
            4800 -> return BaudrateByte.BR_4800.byte
            9600 -> return BaudrateByte.BR_9600.byte
            14400 -> return BaudrateByte.BR_14400.byte
            19200 -> return BaudrateByte.BR_19200.byte
            28800 -> return BaudrateByte.BR_28800.byte
            38400 -> return BaudrateByte.BR_38400.byte
            57600 -> return BaudrateByte.BR_57600.byte
            76800 -> return BaudrateByte.BR_76800.byte
            115200 -> return BaudrateByte.BR_115200.byte
            230400 -> return BaudrateByte.BR_230400.byte
            250000 -> return BaudrateByte.BR_250000.byte
            500000 -> return BaudrateByte.BR_500000.byte
            1000000 -> return BaudrateByte.BR_1000000.byte
            else -> return 0
        }
    }

    fun littleEndianConversion(bytes: ByteArray): Int {
        var result = 0
        for (i in bytes.indices) {
            result = result or (bytes[i].toUByte().toInt() shl 8 * i)
        }
        return result
    }

    override fun GetDataStruct():Any {
        return when (ProtocolMode.codesMap[Data!![5]]) {
            ProtocolMode.CTRLPosSpeed -> {
                val nuripos = NuriPosSpeedAclCtrl()
                nuripos.Protocol = Data!![5]
                nuripos.ID = Data!![2]
//                nuripos.Direction = Data!![6] as Direction
                nuripos.Direction = Direction.codesMap[Data!![6]]
//                nuripos.Pos =
//                    ByteBuffer.wrap(Data!!.slice(6..7).reversed().toByteArray()).getFloat() * 0.01f
//                nuripos.Speed =
//                    ByteBuffer.wrap(Data!!.slice(8..9).reversed().toByteArray()).getFloat() * 0.1f
                nuripos.Pos =
                    ByteBuffer.wrap(Data!!.slice(7..8).reversed().toByteArray()).getFloat() * 0.01f
                nuripos.Speed =
                    ByteBuffer.wrap(Data!!.slice(9..10).reversed().toByteArray()).getFloat() * 0.1f
                nuripos
            }
            ProtocolMode.CTRLAccPos -> {
                val nuripos = NuriPosSpeedAclCtrl()
                nuripos.Protocol = Data!![5]
                nuripos.ID = Data!![2]
//                nuripos.Direction = Data!![6] as Direction
                nuripos.Direction = Direction.codesMap[Data!![6]]
//                nuripos.Pos =
//                    ByteBuffer.wrap(Data!!.slice(6..7).reversed().toByteArray()).getFloat() * 0.01f
                nuripos.Pos =
                    ByteBuffer.wrap(Data!!.slice(7..8).reversed().toByteArray()).getFloat() * 0.01f
                nuripos.Arrivetime = Data!![9] * 0.1f
                nuripos
            }
            ProtocolMode.CTRLAccSpeed -> {
                val nuripos = NuriPosSpeedAclCtrl()
                nuripos.Protocol = Data!![5]
                nuripos.ID = Data!![2]
                nuripos.Direction = Data!![6] as Direction
                nuripos.Speed =
                    ByteBuffer.wrap(Data!!.slice(7..9).reversed().toByteArray()).getFloat() * 0.1f
                nuripos.Arrivetime = Data!![9] * 0.1f
                nuripos
            }
            ProtocolMode.SETPosCtrl -> {
                val nuriPosSpdCtrl = NuriPosSpdCtrl()
                nuriPosSpdCtrl.Protocol = Data!![5]
                nuriPosSpdCtrl.ID = Data!![2]
                nuriPosSpdCtrl.Kp = Data!![6]
                nuriPosSpdCtrl.Ki = Data!![7]
                nuriPosSpdCtrl.Kd = Data!![8]
                nuriPosSpdCtrl.Current = (Data!![9] * 100).toShort()
                nuriPosSpdCtrl
            }
            ProtocolMode.SETSpeedCtrl -> {
                val nuriPosSpdCtrl = NuriPosSpdCtrl()
                nuriPosSpdCtrl.Protocol = Data!![5]
                nuriPosSpdCtrl.ID = Data!![2]
                nuriPosSpdCtrl.Kp = Data!![6]
                nuriPosSpdCtrl.Ki = Data!![7]
                nuriPosSpdCtrl.Kd = Data!![8]
                nuriPosSpdCtrl.Current = (Data!![9] * 100).toShort()
                nuriPosSpdCtrl
            }
            ProtocolMode.SETID -> {
                val nuriID = NuriID()
                nuriID.Protocol = Data!![5]
                nuriID.ID = Data!![2]
                nuriID.AfterID = Data!![6]
                nuriID
            }
            ProtocolMode.SETBaudrate -> {
                val nuriBuadrate = NuriBaudrate()
                nuriBuadrate.Protocol = Data!![5]
                nuriBuadrate.ID = Data!![2]
                nuriBuadrate.Baudrate = GetBaudrate(Data!![6])
                nuriBuadrate
            }
            ProtocolMode.SETResptime -> {
                val nuriResponsetime = NuriResponsetime()
                nuriResponsetime.Protocol = Data!![5]
                nuriResponsetime.ID = Data!![2]
//                nuriResponsetime.Responsetime = (Data!![6] * 100).toShort()
                nuriResponsetime.Responsetime = (Data!![6].toUByte() * 100u).toShort()
                nuriResponsetime
            }
            ProtocolMode.SETRatedSPD -> {
                val nuriRateSpeed = NuriRateSpeed()
                nuriRateSpeed.Protocol = Data!![5]
                nuriRateSpeed.ID = Data!![2]
//                nuriRateSpeed.Speed = Data!!.slice(5..6).reversed().toString().toUShort()
                nuriRateSpeed.Speed = littleEndianConversion(Data!!.slice(6..7).toByteArray()).toUShort()
                nuriRateSpeed
            }
            ProtocolMode.SETResolution -> {
                val nuriResolution = NuriResolution()
                nuriResolution.Protocol = Data!![5]
                nuriResolution.ID = Data!![2]
//                nuriResolution.Resolution = Data!!.slice(5..6).reversed().toString().toUShort()
                nuriResolution.Resolution = littleEndianConversion(Data!!.slice(6..7).toByteArray()).toUShort()
                nuriResolution
            }
            ProtocolMode.SETRatio -> {
                val nuriRatio = NuriRatio()
                nuriRatio.Protocol = Data!![5]
                nuriRatio.ID = Data!![2]
//                nuriRatio.Ratio = Data!!.slice(5..6).reversed().toString().toFloat() * 0.1f
                nuriRatio.Ratio = littleEndianConversion(Data!!.slice(6..7).toByteArray()) * 0.1f
                nuriRatio
            }
            ProtocolMode.SETCtrlOnOff -> {
                val nuriControlOnOff = NuriControlOnOff()
                nuriControlOnOff.Protocol = Data!![5]
                nuriControlOnOff.ID = Data!![2]
                nuriControlOnOff.IsCtrlOn = Data!![6].toInt() == 0
                nuriControlOnOff
            }
            ProtocolMode.SETPosCtrlMode -> {
                val nuriPositionCtrl = NuriPositionCtrl()
                nuriPositionCtrl.Protocol = Data!![5]
                nuriPositionCtrl.ID = Data!![2]
                nuriPositionCtrl.IsAbsolutePostionCtrl = Data!![6].toInt() == 0
                nuriPositionCtrl
            }
            ProtocolMode.SETCtrlDirt -> {
                val nuriCtrlDirection = NuriCtrlDirection()
                nuriCtrlDirection.Protocol = Data!![5]
                nuriCtrlDirection.ID = Data!![2]
//                nuriCtrlDirection.Direction = Data!![6] as Direction
                nuriCtrlDirection.Direction = Direction.codesMap[Data!![6]]
                nuriCtrlDirection
            }
            ProtocolMode.RESETPos -> {
                val nuriProtocol = NuriProtocol()
                nuriProtocol.ID = Data!![2]
                nuriProtocol.Protocol = Data!![5]
                nuriProtocol
            }
            ProtocolMode.RESETFactory -> {
                val nuriProtocol = NuriProtocol()
                nuriProtocol.ID = Data!![2]
                nuriProtocol.Protocol = Data!![5]
                nuriProtocol
            }
            ProtocolMode.REQPing -> {
                val nuriProtocol = NuriProtocol()
                nuriProtocol.ID = Data!![2]
                nuriProtocol.Protocol = Data!![5]
                nuriProtocol
            }
            ProtocolMode.REQPos -> {
                val nuriProtocol = NuriProtocol()
                nuriProtocol.ID = Data!![2]
                nuriProtocol.Protocol = Data!![5]
                nuriProtocol
            }
            ProtocolMode.REQSpeed -> {
                val nuriProtocol = NuriProtocol()
                nuriProtocol.ID = Data!![2]
                nuriProtocol.Protocol = Data!![5]
                nuriProtocol
            }
            ProtocolMode.REQSpdCtrl -> {
                val nuriProtocol = NuriProtocol()
                nuriProtocol.ID = Data!![2]
                nuriProtocol.Protocol = Data!![5]
                nuriProtocol
            }
            ProtocolMode.REQResptime -> {
                val nuriProtocol = NuriProtocol()
                nuriProtocol.ID = Data!![2]
                nuriProtocol.Protocol = Data!![5]
                nuriProtocol
            }
            ProtocolMode.REQRatedSPD -> {
                val nuriProtocol = NuriProtocol()
                nuriProtocol.ID = Data!![2]
                nuriProtocol.Protocol = Data!![5]
                nuriProtocol
            }
            ProtocolMode.REQResolution -> {
                val nuriProtocol = NuriProtocol()
                nuriProtocol.ID = Data!![2]
                nuriProtocol.Protocol = Data!![5]
                nuriProtocol
            }
            ProtocolMode.REQRatio -> {
                val nuriProtocol = NuriProtocol()
                nuriProtocol.ID = Data!![2]
                nuriProtocol.Protocol = Data!![5]
                nuriProtocol
            }
            ProtocolMode.REQCtrlOnOff -> {
                val nuriProtocol = NuriProtocol()
                nuriProtocol.ID = Data!![2]
                nuriProtocol.Protocol = Data!![5]
                nuriProtocol
            }
            ProtocolMode.REQPosCtrlMode -> {
                val nuriProtocol = NuriProtocol()
                nuriProtocol.ID = Data!![2]
                nuriProtocol.Protocol = Data!![5]
                nuriProtocol
            }
            ProtocolMode.REQCtrlDirt -> {
                val nuriProtocol = NuriProtocol()
                nuriProtocol.ID = Data!![2]
                nuriProtocol.Protocol = Data!![5]
                nuriProtocol
            }
            ProtocolMode.REQFirmware -> {
                val nuriProtocol = NuriProtocol()
                nuriProtocol.ID = Data!![2]
                nuriProtocol.Protocol = Data!![5]
                nuriProtocol
            }
            ProtocolMode.FEEDPing -> {
                val nuriProtocol = NuriProtocol()
                nuriProtocol.ID = Data!![2]
                nuriProtocol.Protocol = Data!![5]
                nuriProtocol
            }
            ProtocolMode.FEEDPos -> {
                val nuriPosSpeedAclCtrl = NuriPosSpeedAclCtrl()
                nuriPosSpeedAclCtrl.ID = Data!![2]
                nuriPosSpeedAclCtrl.Protocol = Data!![5]
//                nuriPosSpeedAclCtrl.Direction = Data!![6] as Direction
                nuriPosSpeedAclCtrl.Direction = Direction.codesMap[Data!![6]]
//                nuriPosSpeedAclCtrl.Pos =
//                    ByteBuffer.wrap(Data!!.slice(6..7).reversed().toByteArray()).getFloat() * 0.01f
//                nuriPosSpeedAclCtrl.Speed =
//                    ByteBuffer.wrap(Data!!.slice(8..9).reversed().toByteArray()).getFloat() * 0.1f
                nuriPosSpeedAclCtrl.Pos = littleEndianConversion(Data!!.slice(7..8).toByteArray()) * 0.01f
                nuriPosSpeedAclCtrl.Speed = littleEndianConversion(Data!!.slice(9..10).toByteArray()) * 0.1f
//                nuriPosSpeedAclCtrl.Current = (Data!![11] * 100).toShort()
                nuriPosSpeedAclCtrl.Current = (Data!![11].toUByte() * 100u).toShort()
                nuriPosSpeedAclCtrl
            }
            ProtocolMode.FEEDSpeed -> {
                val nuriPosSpeedAclCtrl = NuriPosSpeedAclCtrl()
                nuriPosSpeedAclCtrl.ID = Data!![2]
                nuriPosSpeedAclCtrl.Protocol = Data!![5]
//                nuriPosSpeedAclCtrl.Direction = Data!![6] as Direction
                nuriPosSpeedAclCtrl.Direction = Direction.codesMap[Data!![6]]
//                nuriPosSpeedAclCtrl.Pos =
//                    ByteBuffer.wrap(Data!!.slice(8..9).reversed().toByteArray()).getFloat() * 0.01f
//                nuriPosSpeedAclCtrl.Speed =
//                    ByteBuffer.wrap(Data!!.slice(6..7).reversed().toByteArray()).getFloat() * 0.1f
                nuriPosSpeedAclCtrl.Pos = littleEndianConversion(Data!!.slice(9..10).toByteArray()) * 0.01f
                nuriPosSpeedAclCtrl.Speed = littleEndianConversion(Data!!.slice(7..8).toByteArray()) * 0.1f
//                nuriPosSpeedAclCtrl.Current = (Data!![11] * 100).toShort()
                nuriPosSpeedAclCtrl.Current = (Data!![11].toUByte() * 100u).toShort()
                nuriPosSpeedAclCtrl
            }
            ProtocolMode.FEEDPosCtrl -> {
                val nuriPosSpdCtrl = NuriPosSpdCtrl()
                nuriPosSpdCtrl.ID = Data!![2]
                nuriPosSpdCtrl.Protocol = Data!![5]
                nuriPosSpdCtrl.Kp = Data!![6]
                nuriPosSpdCtrl.Ki = Data!![7]
                nuriPosSpdCtrl.Kd = Data!![8]
                nuriPosSpdCtrl.Current = (Data!![9] * 100).toShort()
                nuriPosSpdCtrl
            }
            ProtocolMode.FEEDSpdCtrl -> {
                val nuriPosSpdCtrl = NuriPosSpdCtrl()
                nuriPosSpdCtrl.ID = Data!![2]
                nuriPosSpdCtrl.Protocol = Data!![5]
                nuriPosSpdCtrl.Kp = Data!![6]
                nuriPosSpdCtrl.Ki = Data!![7]
                nuriPosSpdCtrl.Kd = Data!![8]
//                nuriPosSpdCtrl.Current = (Data!![9] * 100).toShort()
                nuriPosSpdCtrl.Current = (Data!![9].toUByte() * 100u).toShort()
                nuriPosSpdCtrl
            }
            ProtocolMode.FEEDResptime -> {
                val nuriResponsetime = NuriResponsetime()
                nuriResponsetime.Protocol = Data!![5]
                nuriResponsetime.ID = Data!![2]
//                nuriResponsetime.Responsetime = (Data!![6] * 100).toShort()
                nuriResponsetime.Responsetime = (Data!![6].toUByte() * 100u).toShort()
                nuriResponsetime
            }
            ProtocolMode.FEEDRatedSPD -> {
                val nuriRatedSpeed = NuriRateSpeed()
                nuriRatedSpeed.Protocol = Data!![5]
                nuriRatedSpeed.ID = Data!![2]
//                nuriRatedSpeed.Speed =
//                    (Data!!.slice(5..6).reversed().toByteArray()).toString().toUShort()
                nuriRatedSpeed.Speed = littleEndianConversion(Data!!.slice(6..7).toByteArray()).toUShort()
                nuriRatedSpeed
            }
            ProtocolMode.FEEDResolution -> {
                val nuriResolution = NuriResolution()
                nuriResolution.Protocol = Data!![5]
                nuriResolution.ID = Data!![2]
//                nuriResolution.Resolution =
//                    (Data!!.slice(5..6).reversed().toByteArray()).toString().toUShort()
                nuriResolution.Resolution = littleEndianConversion(Data!!.slice(6..7).toByteArray()).toUShort()
                nuriResolution
            }
            ProtocolMode.FEEDRatio -> {
                val nuriRatio = NuriRatio()
                nuriRatio.Protocol = Data!![5]
                nuriRatio.ID = Data!![2]
                nuriRatio.Ratio =
                    ByteBuffer.wrap(Data!!.slice(6..7).reversed().toByteArray()).getFloat() * 0.1f
                nuriRatio
            }
            ProtocolMode.FEEDCtrlOnOff -> {
                val nuriControlOnOff = NuriControlOnOff()
                nuriControlOnOff.Protocol = Data!![5]
                nuriControlOnOff.ID = Data!![2]
                nuriControlOnOff.IsCtrlOn = Data!![6].toInt() == 0
                nuriControlOnOff
            }
            ProtocolMode.FEEDPosCtrlMode -> {
                val nuriPositionCtrl = NuriPositionCtrl()
                nuriPositionCtrl.Protocol = Data!![5]
                nuriPositionCtrl.ID = Data!![2]
                nuriPositionCtrl.IsAbsolutePostionCtrl = Data!![6].toInt() == 0
                nuriPositionCtrl
            }
            ProtocolMode.FEEDCtrlDirt -> {
                val nuriCtrlDirection = NuriCtrlDirection()
                nuriCtrlDirection.Protocol = Data!![5]
                nuriCtrlDirection.ID = Data!![2]
//                nuriCtrlDirection.Direction = Data!![6] as Direction
                nuriCtrlDirection.Direction = Direction.codesMap[Data!![6]]
                nuriCtrlDirection
            }
            ProtocolMode.FEEDFirmware -> {
                val nuriVersion = NuriVersion()
                nuriVersion.ID = Data!![2]
                nuriVersion.Version = Data!![6]
                nuriVersion
            }
            else -> {
            }
        }
    }


    override fun Parse(data: ByteArray): Boolean {
        var ret = false
        try {
            Data = ByteArray(data.size)
            data.copyInto(Data!!, endIndex = Data!!.size)

            if (Data!![3] + 4 != data.size)
                return ret

            val chksum = GetCheckSum()
            if (Data!![4] == chksum) {
                try {
//                    PacketName = Data!![5].toString()
                    PacketName = ProtocolMode.codesMap[Data!![5]].toString()
                    ret = true
                } catch (e: Exception) {
                    ret = false
                }
            } else ret = false

        } catch (e: Exception) {
            Log.d(TAG, "$e")
        }
        return ret
    }

    /// <summary>
    /// 1. 위치, 속도제어(송신)
    /// </summary>
    /// <param name="arg">위치, 속도, 도달시간</param>

    fun PROT_ControlPosSpeed(arg: NuriPosSpeedAclCtrl) {
        val data: ByteArray = ByteArray(5)
        if (arg.Direction == Direction.CCW) {
            data[0] = 0x00
        } else data[0] = 0x01
        var teno = Math.round(arg.Pos!! / 0.01f)

        //todo
        val tmppos = floatToByteArray(Math.round(arg.Pos!! / 0.01f).toFloat())?.reversedArray()
        tmppos?.copyInto(data, 1, 0, 2)
        val tmpspd = floatToByteArray(Math.round(arg.Speed!! / 0.1f).toFloat())?.reversedArray()
        tmpspd?.copyInto(data, 3, 0, 2)
        BuildProtocol(arg.ID!!, 0x07, 0x01, data)
    }

    fun floatToByteArray(value: Float): ByteArray? {
        val intBits = java.lang.Float.floatToIntBits(value)
        return byteArrayOf(
            (intBits shr 24).toByte(),
            (intBits shr 16).toByte(),
            (intBits shr 8).toByte(),
            intBits.toByte()
        )
    }

    /// <summary>
    /// 1. 위치, 속도제어(송신)
    /// </summary>
    /// <param name="id">장비 아이디</param>
    /// <param name="direction">위치방향</param>
    /// <param name="pos">위치</param>
    /// <param name="spd">속도</param>
    fun ControlPosSpeed(id: Byte, direction: Byte, pos: Float, spd: Float) {
        val nuriPosSpeedAclCtrl = NuriPosSpeedAclCtrl()
        nuriPosSpeedAclCtrl.ID = id
//        nuriPosSpeedAclCtrl.Direction = direction as Direction
        nuriPosSpeedAclCtrl.Direction = Direction.codesMap[direction]
        nuriPosSpeedAclCtrl.Pos = pos
        nuriPosSpeedAclCtrl.Speed = spd
        PROT_ControlPosSpeed(nuriPosSpeedAclCtrl)
    }

    /// <summary>
    /// 2. 가감속 위치제어(송신)
    /// </summary>
    /// <param name="arg"></param>
    fun PROT_ControlAcceleratedPos(arg: NuriPosSpeedAclCtrl) {
        val data = ByteArray(4)
        data[0] = (if (arg.Direction === Direction.CCW) 0x00 else 0x01).toByte()
        val tmppos = floatToByteArray(Math.round(arg.Pos!! / 0.01f).toFloat())?.reversedArray()
        tmppos?.copyInto(data, 1, 0, 2)
        data[3] = Math.round(arg.Arrivetime!! / 0.1f).toInt().toByte()
        BuildProtocol(arg.ID!!, 0x06, 0x02, data)
    }

    /// <summary>
    /// 2. 가감속 위치제어(송신)
    /// </summary>
    /// <param name="id">장비 아이디</param>
    /// <param name="direction">위치방향</param>
    /// <param name="pos">위치</param>
    /// <param name="arrive">도달시간(second)</param>
    fun ControlAcceleratedPos(id: Byte, direction: Byte, pos: Float, arrive: Float) {
        val nuriPosSpeedAclCtrl = NuriPosSpeedAclCtrl()
        nuriPosSpeedAclCtrl.ID = id
//        nuriPosSpeedAclCtrl.Direction = direction as Direction
        nuriPosSpeedAclCtrl.Direction = Direction.codesMap[direction]
        nuriPosSpeedAclCtrl.Pos = pos
        nuriPosSpeedAclCtrl.Arrivetime = arrive
        PROT_ControlAcceleratedPos(nuriPosSpeedAclCtrl)
    }


    /// 3. 가감속 속도제어(송신)
    /// </summary>
    /// <param name="arg"></param>
    fun PROT_ControlAcceleratedSpeed(arg: NuriPosSpeedAclCtrl) {
        val data = ByteArray(4)
        data[0] = (if (arg.Direction === Direction.CCW) 0x00 else 0x01).toByte()
//        val tmpspd = floatToByteArray(Math.round(arg.Pos!! / 0.01f).toFloat())?.reversedArray()
//        val tmpspd = floatToByteArray(Math.round(arg.Speed!! / 0.1f).toFloat())?.reversedArray()
//        tmpspd?.copyInto(data, 1, 0, 2)
//        data[3] = Math.round(arg.Arrivetime!! / 0.1f).toInt().toByte()
//        BuildProtocol(arg.ID!!, 0x06, 0x03, data)

        val tmpspd = round(arg.Speed!! / 0.1f)
        data[2] = (tmpspd and 0xFF).toByte()
        data[1] = ((tmpspd shr 8) and 0xff).toByte()
        data[3] = Math.round(arg.Arrivetime!! / 0.1f).toInt().toByte()
        BuildProtocol(arg.ID!!, 0x06, 0x03, data)
    }

    /// <summary>
    /// 3. 가감속 속도제어(송신)
    /// </summary>
    /// <param name="id">장비 아이디</param>
    /// <param name="direction">위치방향</param>
    /// <param name="speed">속도</param>
    /// <param name="arrive">도달시간(second)</param>
    fun ControlAcceleratedSpeed(id: Byte, direction: Byte, speed: Float, arrive: Float) {
        val nuriPosSpeedAclCtrl = NuriPosSpeedAclCtrl()
        nuriPosSpeedAclCtrl.ID = id
//        nuriPosSpeedAclCtrl.Direction = direction as Direction
        nuriPosSpeedAclCtrl.Direction = Direction.codesMap[direction]
        nuriPosSpeedAclCtrl.Speed = speed
        nuriPosSpeedAclCtrl.Arrivetime = arrive
        PROT_ControlAcceleratedSpeed(nuriPosSpeedAclCtrl)
    }

    /// <summary>
    /// 4. 위치제어기 설정(송신)
    /// </summary>
    /// <param name="arg"></param>
    fun PROT_SettingPositionController(arg: NuriPosSpdCtrl) {
        val data = ByteArray(4)
        data[0] = arg.Kp!!
        data[1] = arg.Ki!!
        data[2] = arg.Kd!!
        data[3] = (arg.Current!! / 100).toByte()
        BuildProtocol(arg.ID!!, 0x06, 0x04, data)
    }

    /// <summary>
    /// 4. 위치제어기 설정(송신)
    /// </summary>
    /// <param name="id">장비 아이디</param>
    /// <param name="kp">P</param>
    /// <param name="ki">I</param>
    /// <param name="kd">D</param>
    /// <param name="current">정격전류</param>
    fun SettingPositionController(id: Byte, kp: Byte, ki: Byte, kd: Byte, current: Short) {
        val nuriPosSpdCtrl = NuriPosSpdCtrl()
        nuriPosSpdCtrl.ID = id
        nuriPosSpdCtrl.Current = current
        nuriPosSpdCtrl.Kd = kd
        nuriPosSpdCtrl.Ki = ki
        nuriPosSpdCtrl.Kp = kp
        PROT_SettingPositionController(nuriPosSpdCtrl)
    }

    /// <summary>
    /// 5. 속도제어기 설정(송신)
    /// </summary>
    /// <param name="arg"></param>
    fun PROT_SettingSpeedController(arg: NuriPosSpdCtrl) {
        val data = ByteArray(4)
        data[0] = arg.Kp!!
        data[1] = arg.Ki!!
        data[2] = arg.Kd!!
        data[3] = (arg.Current!! / 100).toByte()
        BuildProtocol(arg.ID!!, 0x06, 0x05, data)
    }

    /// <summary>
    /// 5. 속도제어기 설정(송신)
    /// </summary>
    /// <param name="id">장비 아이디</param>
    /// <param name="kp">P</param>
    /// <param name="ki">I</param>
    /// <param name="kd">D</param>
    /// <param name="current">정격전류</param>
    fun SettingSpeedController(id: Byte, kp: Byte, ki: Byte, kd: Byte, current: Short) {
        val nuriPosSpdCtrl = NuriPosSpdCtrl()
        nuriPosSpdCtrl.ID = id
        nuriPosSpdCtrl.Current = current
        nuriPosSpdCtrl.Kd = kd
        nuriPosSpdCtrl.Ki = ki
        nuriPosSpdCtrl.Kp = kp
        PROT_SettingPositionController(nuriPosSpdCtrl)
    }

    /// <summary>
    /// 6. ID설정(송신)
    /// </summary>
    /// <param name="arg"></param>
    fun PROT_SettingID(arg: NuriID) {
        BuildProtocol(arg.ID!!, 0x03, 0x06, byteArrayOf(arg.AfterID!!))
    }

    /// <summary>
    /// 6. ID설정(송신)
    /// </summary>
    /// <param name="id">장비 아이디</param>
    /// <param name="afterid">변경 아이디</param>
    fun SettingID(id: Byte, afterid: Byte) {
        val nuriID = NuriID()
        nuriID.ID = id
        nuriID.AfterID = afterid
        PROT_SettingID(nuriID)
    }

    /// <summary>
    /// 7. 통신속도 설정(송신)
    /// </summary>
    /// <param name="arg"></param>
    fun PROT_SettingBaudrate(arg: NuriBaudrate) {
        BuildProtocol(arg.ID!!, 0x03, 0x07, byteArrayOf(GetBaudrateProtocol(arg.Baudrate!!)))
    }

    /// <summary>
    /// 7. 통신속도 설정(송신)
    /// </summary>
    /// <param name="id">장비 아이디</param>
    /// <param name="bps">통신속도 bps</param>
    fun SettingBaudrate(id: Byte, bps: Int) {
        val nuriBaudrate = NuriBaudrate()
        nuriBaudrate.ID = id
        nuriBaudrate.Baudrate = bps
        PROT_SettingBaudrate(nuriBaudrate)
    }

    /// <summary>
    /// 8. 통신응답시간 설정(송신)
    /// </summary>
    /// <param name="arg"></param>
    fun PROT_SettingResponsetime(arg: NuriResponsetime) {
        BuildProtocol(arg.ID!!, 0x03, 0x08, byteArrayOf((arg.Responsetime!! / 100).toByte()))
    }

    /// <summary>
    /// 8. 통신응답시간 설정(송신)
    /// </summary>
    /// <param name="id">장비 아이디</param>
    /// <param name="response">통신응답시간(us)</param>
    fun SettingResponsetime(id: Byte, response: Short) {
        val nuriResponsetime = NuriResponsetime()
        nuriResponsetime.ID = id
        nuriResponsetime.Responsetime = response
        PROT_SettingResponsetime(nuriResponsetime)
    }

    /// <summary>
    /// 9. 모터 정격속도 설정(송신)
    /// </summary>
    /// <param name="arg"></param>
    @ExperimentalUnsignedTypes
    fun PROT_SettingRatedspeed(arg: NuriRateSpeed) {
        val data = ByteArray(2)
        //todo Ushort to ByteArray???
        val tmpspd = arg.Speed!!.toString().toByteArray().reversedArray()
        tmpspd.copyInto(data, 0, 0, 2)
        BuildProtocol(arg.ID!!, 0x04, 0x09, data)
    }

    /// <summary>
    /// 9. 모터 정격속도 설정(송신)
    /// </summary>
    /// <param name="id">장비 아이디</param>
    /// <param name="spd">모터 정격속도 RPM</param>
    @ExperimentalUnsignedTypes
    fun SettingRatedspeed(id: Byte, spd: UShort) {
        val nuriRateSpeed = NuriRateSpeed()
        nuriRateSpeed.ID = id
        nuriRateSpeed.Speed = spd
        PROT_SettingRatedspeed(nuriRateSpeed)
    }


    /// <summary>
    /// 10. 분해능 설정(송신)
    /// </summary>
    /// <param name="arg"></param>
    @ExperimentalUnsignedTypes
    fun PROT_SettingResolution(arg: NuriResolution) {
        val data = ByteArray(2)
        val tmpspd = arg.Resolution!!.toString().toByteArray().reversedArray()
        tmpspd.copyInto(data, 0, 0, 2)
        BuildProtocol(arg.ID!!, 0x04, 0x0A, data)
    }

    /// <summary>
    /// 10. 분해능 설정(송신
    /// </summary>
    /// <param name="id">장비 아이디</param>
    /// <param name="res">분해능</param>
    @ExperimentalUnsignedTypes
    fun SettingResolution(id: Byte, res: UShort) {
        val nuriResolution = NuriResolution()
        nuriResolution.ID = id
        nuriResolution.Resolution = res
        PROT_SettingResolution(nuriResolution)
    }

    /// <summary>
    /// 11.감속비설정(송신)
    /// </summary>
    /// <param name="arg"></param>
    fun PROT_SettingRatio(arg: NuriRatio) {
        val data = ByteArray(2)
        //todo
        val tmpspd = Math.round(arg.Ratio!! / 0.1f).toString().toByteArray().reversedArray()
        tmpspd.copyInto(data, 0, 0, 2)
        BuildProtocol(arg.ID!!, 0x04, 0x0B, data)
    }

    /// <summary>
    /// 11.감속비설정(송신)
    /// </summary>
    /// <param name="id">장비 아이디</param>
    /// <param name="ratio">감속비</param>
    fun SettingRatio(id: Byte, ratio: Float) {
        val nuriRatio = NuriRatio()
        nuriRatio.ID = id
        nuriRatio.Ratio = ratio
        PROT_SettingRatio(nuriRatio)
    }

    /// <summary>
    /// 12. 제어 On/Off 설정(송신)
    /// </summary>
    /// <param name="arg"></param>
    fun PROT_SettingControlOnOff(arg: NuriControlOnOff) {
        BuildProtocol(arg.ID!!, 0x03, 0x0C, byteArrayOf(if (arg.IsCtrlOn == null) 0x00 else 0x01))
    }

    /// <summary>
    /// 12. 제어 On/Off 설정(송신)
    /// </summary>
    /// <param name="id">장비 아이디</param>
    /// <param name="isCtrlOn">제어 On/Off</param>
    fun SettingControlOnOff(id: Byte, isCtrlOn: Boolean) {
        val nuriControlOnOff = NuriControlOnOff()
        nuriControlOnOff.ID = id
        nuriControlOnOff.IsCtrlOn = isCtrlOn
        PROT_SettingControlOnOff(nuriControlOnOff)
    }

    /// <summary>
    /// 13. 위치제어 모드 설정(송신)
    /// </summary>
    /// <param name="arg"></param>
    fun PROT_SettingPositionControl(arg: NuriPositionCtrl) {
        BuildProtocol(
            arg.ID!!,
            0x03,
            0x0D,
            byteArrayOf(if (arg.IsAbsolutePostionCtrl == null) 0x00 else 0x01)
        )
    }

    /// <summary>
    /// 13. 위치제어 모드 설정(송신)
    /// </summary>
    /// <param name="id">장비 아이디</param>
    /// <param name="isAbsolute">절대위치여부</param>
    fun SettingPositionControl(id: Byte, isAbsolute: Boolean) {
        val nuriPositionCtrl = NuriPositionCtrl()
        nuriPositionCtrl.ID = id
        nuriPositionCtrl.IsAbsolutePostionCtrl = isAbsolute
        PROT_SettingPositionControl(nuriPositionCtrl)
    }

    /// <summary>
    /// 14. 제어 방향 설정(송신)
    /// </summary>
    /// <param name="arg"></param>
    fun PROT_SettingControlDirection(arg: NuriCtrlDirection) {
        BuildProtocol(
            arg.ID!!,
            0x03,
            0x0E,
            byteArrayOf(if (arg.Direction == Direction.CCW) 0x00 else 0x01)
        )
    }

    /// <summary>
    /// 14. 제어 방향 설정(송신)
    /// </summary>
    /// <param name="id">장비 아이디</param>
    /// <param name="direction">제어방향</param>
    fun SettingControlDirection(id: Byte, direction: Direction) {
        val nuriCtrlDirection = NuriCtrlDirection()
        nuriCtrlDirection.ID = id
        nuriCtrlDirection.Direction = direction
        PROT_SettingControlDirection(nuriCtrlDirection)
    }

    /// <summary>
    /// 15. 위치초기화(송신)
    /// </summary>
    /// <param name="id">장비 아이디</param>
    fun PROT_ResetPostion(arg: NuriProtocol) {
        BuildProtocol(arg.ID!!, 0x02, 0x0F, byteArrayOf())
    }

    /// <summary>
    /// 15. 위치초기화(송신)
    /// </summary>
    /// <param name="id">장비 아이디</param>
    fun ResetPostion(id: Byte) {
        val nuriProtocol = NuriProtocol()
        nuriProtocol.ID = id
        PROT_ResetPostion(nuriProtocol)
    }

    /// <summary>
    /// 16. 공장초기화(송신)
    /// </summary>
    /// <param name="id">장비아이디</param>
    fun PROT_ResetFactory(arg: NuriProtocol) {
        BuildProtocol(arg.ID!!, 0x02, 0x10, byteArrayOf())
    }

    /// <summary>
    /// 16. 공장초기화(송신)
    /// </summary>
    /// <param name="id">장비아이디</param>
    fun ResetFactory(id: Byte) {
        val nuriProtocol = NuriProtocol()
        nuriProtocol.ID = id
        PROT_ResetFactory(nuriProtocol)
    }

    /// <summary>
    /// 17. 피드백 요청(송신)
    /// </summary>
    /// <param name="arg"></param>
    fun PROT_Feedback(arg: NuriProtocol) {
        if (arg.Protocol!! >= ProtocolMode.REQPing.byte &&
            arg.Protocol!! <= ProtocolMode.REQFirmware.byte
        ) {
            BuildProtocol(arg.ID!!, 0x02, arg.Protocol!!.toByte(), byteArrayOf())
        }
    }

    /// <summary>
    /// 17. 피드백 요청(송신)
    /// </summary>
    /// <param name="id">장비아이디</param>
    /// <param name="mode">피드백 모드</param>
    fun Feedback(id: Byte, mode: Byte) {
        val nuriProtocol = NuriProtocol()
        nuriProtocol.ID = id
        nuriProtocol.Protocol = mode
        PROT_Feedback(nuriProtocol)
    }

    /// <summary>
    /// 18. Ping(수신)
    /// 테스트 개발용
    /// </summary>
    /// <param name="arg"></param>
    /// <param name="isSend">전송여부 기본은 전송안함</param>
    fun PROT_FeedbackPing(arg: NuriProtocol, isSend: Boolean = false) {
        BuildProtocol(arg.ID!!, 0x02, ProtocolMode.FEEDPing.byte, byteArrayOf(), isSend)
    }

    /// <summary>
    /// 19. 위치피드백(수신)
    /// 테스트 개발용
    /// </summary>
    /// <param name="arg"></param>
    /// <param name="isSend">전송여부 기본은 전송안함</param>
    fun PROT_FeedbackPOS(arg: NuriPosSpeedAclCtrl, isSend: Boolean = false) {
        val data = ByteArray(6)
        data[0] = if (arg.Direction == Direction.CCW) 0x00 else 0x01
        var tmmpos = (arg.Pos!! / 0.01f).toString().toByteArray().reversedArray()
        tmmpos.copyInto(data, 1, 0, 2)
        var tmpspd = (arg.Speed!! / 0.1f).toString().toByteArray().reversedArray()
        tmpspd.copyInto(data, 3, 0, 2)
        data[5] = (arg.Current!! / 100 as Byte).toByte()
        BuildProtocol(arg.ID!!, 0x08, 0xD1.toByte(), data, isSend)
    }

    /// <summary>
    /// 20. 속도피드백(수신)
    /// 테스트 개발용
    /// </summary>
    /// <param name="arg"></param>
    /// <param name="isSend"></param>
    fun PROT_FeedbackSpeed(arg: NuriPosSpeedAclCtrl, isSend: Boolean = false) {
        val data = ByteArray(6)
        data[0] = if (arg.Direction == Direction.CCW) 0x00 else 0x01
        //todo
        var tmmpos = Math.round(arg.Speed!! / 0.1f).toString().toByteArray().reversedArray()
        tmmpos.copyInto(data, 1, 0, 2)
        var tmpspd = Math.round(arg.Pos!! / 0.1f).toString().toByteArray().reversedArray()
        tmpspd.copyInto(data, 3, 0, 2)
        data[5] = (arg.Current!! / 100).toByte()
        BuildProtocol(arg.ID!!, 0x08, 0xD2.toByte(), data, isSend)
    }


    /// <summary>
    /// 21. 위치제어기 피드백(수신)
    /// </summary>
    /// <param name="arg"></param>
    /// <param name="isSend"></param>
    fun PROT_FeedbackPosControl(arg: NuriPosSpdCtrl, isSend: Boolean = false) {
        val data = ByteArray(4)
        data[0] = arg.Kp!!
        data[1] = arg.Ki!!
        data[2] = arg.Kd!!
        data[3] = (arg.Current!! / 100).toByte()
        BuildProtocol(arg.ID!!, 0x06, 0xD3.toByte(), data, isSend);

    }

    /// <summary>
    /// 22. 속도제어기 피드백(수신)
    /// </summary>
    /// <param name="arg"></param>
    /// <param name="isSend"></param>
    fun PROT_FeedbackSpeedControl(arg: NuriPosSpdCtrl, isSend: Boolean = false) {
        val data = ByteArray(4)
        data[0] = arg.Kp!!
        data[1] = arg.Ki!!
        data[2] = arg.Kd!!
        data[3] = (arg.Current!! / 100).toByte()
        BuildProtocol(arg.ID!!, 0x06, 0xD4.toByte(), data, isSend);
    }

    /// <summary>
    /// 23. 통신응답시간 피드백(수신)
    /// </summary>
    /// <param name="arg"></param>
    /// <param name="isSend"></param>
    fun PROT_FeedbackResponsetime(arg: NuriResponsetime, isSend: Boolean = false) {
        BuildProtocol(
            arg.ID!!,
            0x03,
            0xD5.toByte(),
            byteArrayOf((arg.Responsetime!! / 100).toByte()),
            isSend
        )
    }

    /// <summary>
    /// 24. 모터 정격속도 피드백(수신)
    /// </summary>
    /// <param name="arg"></param>
    /// <param name="isSend"></param>
    fun PROT_FeedbackRatedSpeed(arg: NuriRateSpeed, isSend: Boolean = false) {
        val data = ByteArray(2)
        var tmpspd = (arg.Speed).toString().toByteArray().reversedArray()
        tmpspd.copyInto(data, 0, 0, 2)
        BuildProtocol(arg.ID!!, 0x04, 0xD6.toByte(), data, isSend);
    }

    /// <summary>
    /// 25. 분해능 피드백(수신)
    /// </summary>
    /// <param name="arg"></param>
    /// <param name="isSend"></param>
    fun PROT_FeedbackResolution(arg: NuriResolution, isSend: Boolean = false) {
        val data = ByteArray(2)
        var tmpspd = (arg.Resolution).toString().toByteArray().reversedArray()
        tmpspd.copyInto(data, 0, 0, 2)
        BuildProtocol(arg.ID!!, 0x04, 0xD7.toByte(), data, isSend);
    }

    /// <summary>
    /// 26. 감속비 피드백(수신)
    /// </summary>
    /// <param name="arg"></param>
    /// <param name="isSend"></param>
    fun PROT_FeedbackRatio(arg: NuriRatio, isSend: Boolean = false) {
        val data = ByteArray(2)
        var tmpspd = Math.round(arg.Ratio!!).toString().toByteArray().reversedArray()
        tmpspd.copyInto(data, 0, 0, 2)
        BuildProtocol(arg.ID!!, 0x04, 0xD8.toByte(), data, isSend);
    }

    /// <summary>
    /// 27. 제어 On/Off 피드백(수신)
    /// </summary>
    /// <param name="arg"></param>
    /// <param name="isSend"></param>
    fun PROT_FeedbackControlOnOff(arg: NuriControlOnOff, isSend: Boolean = false) {
        BuildProtocol(
            arg.ID!!, 0x03, 0xD9.toByte(), byteArrayOf(if (arg.IsCtrlOn!!) 0x00 else 0x01), isSend
        )
    }

    /// <summary>
    /// 28. 위치제어 모드 피드백(수신)
    /// </summary>
    /// <param name="arg"></param>
    /// <param name="isSend"></param>
    fun PROT_FeedbackPositionControl(arg: NuriPositionCtrl, isSend: Boolean = false) {
        BuildProtocol(
            arg.ID!!,
            0x03,
            0xDA.toByte(),
            byteArrayOf(if (arg.IsAbsolutePostionCtrl!!) 0x00 else 0x01),
            isSend
        )
    }

    /// <summary>
    /// 29. 제어 방향 피드백(수신)
    /// </summary>
    /// <param name="arg"></param>
    /// <param name="isSend"></param>
    fun PROT_FeedbackControlDirection(arg: NuriCtrlDirection, isSend: Boolean = false) {
        BuildProtocol(
            arg.ID!!,
            0x03,
            0xDB.toByte(),
            byteArrayOf(if (arg.Direction == Direction.CCW) 0x00 else 0x01),
            isSend
        )
    }

    /// <summary>
    /// 30. 펌웨어 버전 피드백(수신)
    /// </summary>
    /// <param name="arg"></param>
    /// <param name="isSend"></param>
    fun PROT_FeedbackFirmware(arg: NuriVersion, isSend: Boolean = false) {
        BuildProtocol(
            arg.ID!!, 0x03, 0xFD.toByte(), byteArrayOf(arg.Version!!), isSend
        )
    }
}