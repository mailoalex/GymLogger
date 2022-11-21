package com.example.gymloger.ui

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.example.gymloger.databinding.FragmentWifiBinding
import kotlinx.coroutines.delay

class WifiFragment : Fragment(), SensorEventListener {

    private var _binding: FragmentWifiBinding? = null


    private val binding get() = _binding!!
    private  lateinit var  _sensorManager: SensorManager
    private lateinit var wifi: WifiManager

//    private lateinit var textGyro: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {



        _binding = FragmentWifiBinding.inflate(inflater, container, false)
        wifi = requireContext().applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager

        val request = NetworkRequest.Builder()
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .build()


        val connManager: ConnectivityManager =
            requireContext().getSystemService(Context.CONNECTIVITY_SERVICE)
                    as ConnectivityManager;



        val networkCallback = object : ConnectivityManager.NetworkCallback(
            FLAG_INCLUDE_LOCATION_INFO
        ) {
            @RequiresApi(Build.VERSION_CODES.Q)
            override fun onCapabilitiesChanged(
                network: Network,
                networkCapabilities: NetworkCapabilities
            ) {
                super.onCapabilitiesChanged(network, networkCapabilities)
                val wifiInfo = networkCapabilities.transportInfo as WifiInfo
                val ssid = wifiInfo.ssid;
                var textSsid = "NO CONNECTION"
                if (ssid != "<unknown ssid>"){
                    textSsid = "Connected to $ssid";
                }

                binding.wifiText.text = textSsid;
            }
        }
        connManager.registerNetworkCallback(request, networkCallback)
//        textGyro = _binding!!.gyro
        val root: View = binding.root

        _sensorManager = context?.getSystemService(Context.SENSOR_SERVICE) as SensorManager

        _sensorManager.getDefaultSensor( Sensor.TYPE_LINEAR_ACCELERATION)?.also {
            _sensorManager.registerListener(
                this,
                it,
                SensorManager.SENSOR_STATUS_ACCURACY_LOW,
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
//        textGyro.text = String.format("x: %f | y: %f | z: %f ", p0!!.values[0],p0.values[1], p0.values[2]  )

    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
//        textGyro.text = String.format("accuracy %d, data %s", p0!!.accuracy, p0.values.joinToString() )

    }

}