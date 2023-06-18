package com.example.onesos

import android.app.Service
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.telephony.SmsManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import java.io.Serializable
import java.util.Objects
import kotlin.math.sqrt


class MyService: Service() {
    private val binder = LocalBinder()
    private var sensorManager: SensorManager? = null
    private var acceleration = 0f
    private var currentAcceleration = 0f
    private var lastAcceleration = 0f
    private var send = MutableLiveData<Int>()
    private var ctcList:ArrayList<Contact> = ArrayList()
    val getSend : LiveData<Int> get() = send

    inner class LocalBinder : Binder() {
        // Return this instance of LocalService so clients can call public methods.
        fun getService(): MyService? {
            return this@MyService
        }
    }


    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {

//        MainActivity()
//        println(intent)
//        val args = intent.getBundleExtra("bundle")
//        println(ctcList)
        @Suppress("DEPRECATION")
//        println(args?.getSerializable("ARRAYLIST"))
        ctcList = intent.getSerializableExtra("ARRAYLIST") as ArrayList<Contact>
        // Getting the Sensor Manager instance
        onTaskRemoved(intent)
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager

        Objects.requireNonNull(sensorManager)!!
            .registerListener(sensorListener, sensorManager!!
                .getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL)

        acceleration = 10f
        currentAcceleration = SensorManager.GRAVITY_EARTH
        lastAcceleration = SensorManager.GRAVITY_EARTH

//        Toast.makeText(
//            applicationContext, "This is a Service running in Background",
//            Toast.LENGTH_SHORT
//        ).show()
        return START_REDELIVER_INTENT
    }

    override fun onBind(intent: Intent?): IBinder? {
        if (intent != null) {
            @Suppress("DEPRECATION")
            ctcList = intent.getSerializableExtra("ARRAYLIST") as ArrayList<Contact>
        }
        return binder
    }

    override fun onTaskRemoved(rootIntent: Intent) {
        val restartServiceIntent = Intent(applicationContext, this.javaClass)
        restartServiceIntent.setPackage(packageName)
        @Suppress("DEPRECATION")
        val list = rootIntent.getSerializableExtra("ARRAYLIST")
        restartServiceIntent.putExtra("ARRAYLIST", list)
        startService(restartServiceIntent)
        super.onTaskRemoved(rootIntent)
    }

    private val sensorListener: SensorEventListener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent) {

            // Fetching x,y,z values
            val x = event.values[0]
            val y = event.values[1]
            val z = event.values[2]
            lastAcceleration = currentAcceleration

            // Getting current accelerations
            // with the help of fetched x,y,z values
            currentAcceleration = sqrt((x * x + y * y + z * z).toDouble()).toFloat()
            val delta: Float = currentAcceleration - lastAcceleration
            acceleration = acceleration * 0.9f + delta

            // Display a Toast message if
            // acceleration value is over 12
            if (acceleration > 12) {
                Toast.makeText(applicationContext, "Shake event detected", Toast.LENGTH_SHORT).show()
                try {
                    if (ctcList.size > 0) {
                        val smsManager: SmsManager = applicationContext.getSystemService(SmsManager::class.java)

                        // on below line we are sending text message.
                        for (contact in ctcList) {
                            smsManager.sendTextMessage(contact.number, null, "help", null, null)
                        }

                        // on below line we are displaying a toast message for message send,
                        Toast.makeText(applicationContext, "Message Sent", Toast.LENGTH_LONG).show()
                    }


                } catch (e: Exception) {

                    // on catch block we are displaying toast message for error.
                    Toast.makeText(applicationContext, "Please enter all the data.."+e.message.toString(), Toast.LENGTH_LONG)
                        .show()
                    println(e.message.toString())
                }
            }
        }
        override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {}

//        fun onResume() {
//            sensorManager?.registerListener(sensorListener, sensorManager!!.getDefaultSensor(
//                Sensor .TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL
//            )
//        }
//
//         fun onPause() {
//            sensorManager!!.unregisterListener(sensorListener)
//        }
    }
}