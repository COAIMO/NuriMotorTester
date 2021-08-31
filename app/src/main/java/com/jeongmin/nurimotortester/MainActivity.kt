package com.jeongmin.nurimotortester

import android.R
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbDeviceConnection
import android.hardware.usb.UsbManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.hoho.android.usbserial.driver.UsbSerialDriver
import com.hoho.android.usbserial.driver.UsbSerialPort
import com.hoho.android.usbserial.driver.UsbSerialProber
import com.jeongmin.nurimotortester.Nuri.AppState
import com.jeongmin.nurimotortester.Nuri.Buadrate
import com.jeongmin.nurimotortester.Nuri.NuriProtocol
import com.jeongmin.nurimotortester.databinding.ActivityMainBinding
import java.io.IOException

class MainActivity : AppCompatActivity() {
    lateinit var mBinding: ActivityMainBinding
    private var mVerticalSplitLayout: SplitLayout? = null
    var serialPort: UsbSerialPort? = null
    val FONT_SIZE = 10
    lateinit var availableDrivers: List<UsbSerialDriver>
    lateinit var m_usbManager: UsbManager
    lateinit var usbDriver: UsbSerialDriver
    private val ACTION_USB_PERMISSION = "com.jeongmin.serial_nuri_tester.USB_PERMISSION"
    var usbConnection: UsbDeviceConnection? = null
    var dataReceivedThread: Thread? = null
    var dataSendThread: Thread? = null
    private var isRunning = true
    val READ_WAIT_MILLIS = 50
    val WRITE_WAIT_MILLIS = 1000
    val TAG = "태그"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        val view = mBinding.root
        setSpinner()
        setContentView(view)
    }

    private fun setSpinner() {
        var arrayAdapter = ArrayAdapter(
            this,
            R.layout.simple_spinner_item,
            Buadrate.values()
        )
        mBinding.toolbar.spinnerBaudrate.adapter = arrayAdapter
        mBinding.toolbar.spinnerBaudrate.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    adapterView: AdapterView<*>,
                    view: View,
                    position: Int,
                    id: Long
                ) {//스피너가 선택 되었을때
                    if (serialPort == null) {
                        creatTextview("Baud Rate : " + Buadrate.values().toString() + "선택했습니다.")
//                        SelectSleepMillis = THREAD_SLEEP_MILLIS[position].toLong()
                    } else {
                        creatTextview("시리얼 연결 해제 후 선택해 주세요.")

                    }
                    creatTextview("Baud Rate : " + Buadrate.values()[position] + "선택했습니다.")
//                    SelectSleepMillis = THREAD_SLEEP_MILLIS[position].toLong()
                }

                override fun onNothingSelected(adapterView: AdapterView<*>) {
                    Toast.makeText(this@MainActivity, "Baud Rate를 선택해주세요.", Toast.LENGTH_SHORT)
                        .show()
                }
            }

    }

    private fun UsbConnecting() {
        val usbDevice: UsbDevice? = null
        availableDrivers = emptyList()
        // 사용 가능한 Usb diver 찾기
        m_usbManager = getSystemService(USB_SERVICE) as UsbManager

        //시리얼 통신가능한 포트만 search
        availableDrivers = UsbSerialProber.getDefaultProber().findAllDrivers(m_usbManager)

        try {
            // 연결가능한 usb드라이버 열기
            usbDriver = availableDrivers[0]
            //serial permission 요청하기
            val intent: PendingIntent =
                PendingIntent.getBroadcast(this, 0, Intent(ACTION_USB_PERMISSION), 0)
            m_usbManager.requestPermission(availableDrivers[0].device, intent)
        } catch (e: Exception) {
            creatTextview("USB Serial Port를 연결해주세요.")
        }
    }

    private val usbReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent?.action!! == ACTION_USB_PERMISSION) {
                val granted: Boolean =
                    intent.extras!!.getBoolean(UsbManager.EXTRA_PERMISSION_GRANTED)
                val selectBaudRate = mBinding.toolbar.spinnerBaudrate.selectedItem

                if (granted) {

                    if (m_usbManager.hasPermission(usbDriver.device) && serialPort == null) {
                        usbConnection = m_usbManager.openDevice(usbDriver.device)
                        serialPort = usbDriver.ports[0]
                        serialPort!!.open(usbConnection)
                        serialPort!!.setParameters(
                            selectBaudRate as Int,
                            8,
                            UsbSerialPort.STOPBITS_1,
                            UsbSerialPort.PARITY_NONE
                        )
                        creatTextview("port1 : $serialPort 정상 연결되었습니다.")
                    }
                }
            } else if (intent.action == UsbManager.ACTION_USB_ACCESSORY_ATTACHED) {
                UsbConnecting()
            } else if (intent.action == UsbManager.ACTION_USB_ACCESSORY_DETACHED) {
                creatTextview("Serial port가 연결되지 않았습니다.")
                disconnect()
            }
        }
    }

    private fun disconnect() {
        try {
            serialPort?.close()
        } catch (e: IOException) {
        }
        serialPort = null
        creatTextview("Serial port가 연결을 해제하였습니다.")
    }

    fun creatTextview(text: String?) {
        //TextView 생성
        val addTextView = TextView(this)
        addTextView.text = text
        addTextView.textSize = FONT_SIZE.toFloat()
        addTextView.setTextColor(Color.BLACK)
        //layout_width, layout_height, gravity 설정
        val lp = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        lp.gravity = Gravity.CENTER
        //addTextView.layoutParams

        runOnUiThread {
//            부모 뷰에 추가
            mBinding.textViewer.addView(addTextView)
        }
    }


    fun logViewerOnOff() {
        mBinding.logOnOffSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
            } else {

            }
        }
    }

    private fun DataReceivedThread() {
        if (serialPort == null) return
        val buffRecvProtocol = ByteArray(1024)
        var idxRecv = 0
        var startRecv: Long = 0
        val buff = ByteArray(1024)
        dataReceivedThread = Thread {
            while (isRunning) {
                val cnt = serialPort!!.read(buff, READ_WAIT_MILLIS)
                if (cnt > 0) {

                    Log.d(TAG,"$buff")
                }
            }
            dataReceivedThread!!.start()
        }

    }
    val tmpRSA = NurirobotRSA()
    val nuriProtocol = NuriProtocol().also {
        it.ID = 0xff.toByte()
        it.Protocol = 0xa0.toByte()
    }
    private fun dataSendThread() {
        if (serialPort == null) return
//        val buff = chk_Header2.plus(initialData!!)
//        val buff2 = buff.plus(chk_Header)
        dataSendThread = Thread {
            while (isRunning) {
//                serialPort?.write()
                Log.d(TAG, "port1SendThread-start")

            }
        }
        dataSendThread!!.start()
    }
    // 대상 아이디 목록을 생성한다.

    fun checkIDList(){
        val state = AppState()
        val ttt = mutableListOf<Byte>()
        if (state.SearchDevice!!.isNotEmpty()){
            for (item in state.SearchDevice!!){
                ttt.add(item)
            }
        }else{
            for (i in 0..254){
                ttt.add(i.toByte())
            }
        }
        val TargetIDs = ttt

    }

    fun checkPing(id:Byte, isSpControl:Boolean = true){
        var tmp = NurirobotRSA()
    }
}