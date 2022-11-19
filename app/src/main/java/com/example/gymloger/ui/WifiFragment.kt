package com.example.gymloger.ui

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.gymloger.databinding.FragmentWifiBinding

class WifiFragment : Fragment(), SensorEventListener {

    private var _binding: FragmentWifiBinding? = null


    private val binding get() = _binding!!
    private  lateinit var  _sensorManager: SensorManager

    private lateinit var textGyro: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {



        _binding = FragmentWifiBinding.inflate(inflater, container, false)
        textGyro = _binding!!.gyro
        val root: View = binding.root

        _sensorManager = context?.getSystemService(Context.SENSOR_SERVICE) as SensorManager

        _sensorManager.getDefaultSensor( Sensor.TYPE_GYROSCOPE)?.also {
            _sensorManager.registerListener(
                this,
                it,
                SensorManager.SENSOR_DELAY_FASTEST,
                SensorManager.SENSOR_DELAY_FASTEST

                )
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        _sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(p0: SensorEvent?) {
        textGyro.text = String.format("accuracy %d, data %s", p0!!.accuracy, p0.values.joinToString() )
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
        textGyro.text = String.format("accuracy %d", p1)
        return
    }

}