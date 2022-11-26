package com.example.gymloger.ui

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.net.ConnectivityManager
import android.net.ConnectivityManager.NetworkCallback
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
import androidx.fragment.app.activityViewModels
import com.example.gymloger.ActivityMainViewModel
import com.example.gymloger.databinding.FragmentWifiBinding
import kotlinx.coroutines.delay
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.*

class WifiFragment : Fragment(), SensorEventListener {

    private var _binding: FragmentWifiBinding? = null


    private val binding get() = _binding!!
    private  lateinit var  _sensorManager: SensorManager
    private lateinit var wifi: WifiManager
    private lateinit var  connManager: ConnectivityManager
    private lateinit var networkCallback: NetworkCallback
    private val viewModel: ActivityMainViewModel by activityViewModels()
    private fun  saveData(numWorkouts: Int, lastWorkout: Date){
        val settings = requireContext().getSharedPreferences("gymLoggerData", 0)
        val editor = settings.edit()
        val sdfmt = SimpleDateFormat("E d y k:m")

        editor.putInt("numWorkouts", numWorkouts)
        editor.putString("lastWorkout", sdfmt.format(lastWorkout))
        editor.apply()
    }
    private fun stopWorkout(){
        viewModel.reset()
        saveData(viewModel.numberOfWorkouts.value!!, viewModel.lastWorkout.value!!)
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentWifiBinding.inflate(inflater, container, false)
        wifi = requireContext().applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager

        viewModel.accelerometerData.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            if(viewModel.isWorkingOut.value!! && viewModel.isConnectedToWifi.value!!){
                viewModel.currentTime.postValue(Timestamp(System.currentTimeMillis()))
            }
            binding.accData.text = String.format("x: %.3f | y: %.3f | z: %.3f ", it[0], it[1], it[2] )
        })
        viewModel.isAccelerometerActive.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
           binding.isAccActive.text = if (it) { "Activity detected"} else{ "No Activity detected. Please move!"}
            viewModel.isWorkingOut.postValue(it && viewModel.isConnectedToWifi.value == true)
        })
        val request = NetworkRequest.Builder()
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .build()


        connManager =
            requireContext().getSystemService(Context.CONNECTIVITY_SERVICE)
                    as ConnectivityManager;



        networkCallback = object : ConnectivityManager.NetworkCallback(
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
                }else{

                }
                viewModel.isConnectedToWifi.postValue(ssid != "<unknown ssid>")
                binding.wifiText.text = textSsid;
            }
        }
        connManager.registerNetworkCallback(request, networkCallback)

        val root: View = binding.root

        _sensorManager = context?.getSystemService(Context.SENSOR_SERVICE) as SensorManager

        _sensorManager.getDefaultSensor( Sensor.TYPE_LINEAR_ACCELERATION)?.also {
            _sensorManager.registerListener(
                this,
                it,
                SensorManager.SENSOR_STATUS_ACCURACY_LOW,
                SensorManager.SENSOR_DELAY_NORMAL

                )
        }

        binding.stopWorkoutButton.setOnClickListener {
            stopWorkout()
        }


        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        _sensorManager.unregisterListener(this)
        connManager.unregisterNetworkCallback(networkCallback)

    }

    override fun onSensorChanged(p0: SensorEvent?) {
        viewModel.accelerometerData.postValue(p0!!.values.toMutableList())
        viewModel.isAccelerometerActive.postValue(p0.values[0] >=5 || p0.values[1] >=5 || p0.values[2] >=5)
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
//        textGyro.text = String.format("accuracy %d, data %s", p0!!.accuracy, p0.values.joinToString() )
    }

}