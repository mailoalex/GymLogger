package com.example.gymloger

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
import android.text.format.Time
import android.util.Log
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.gymloger.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity(), SensorEventListener {
    private lateinit var  connManager: ConnectivityManager
    private lateinit var networkCallback: ConnectivityManager.NetworkCallback
    private lateinit var binding: ActivityMainBinding
    private val viewmodel: ActivityMainViewModel by viewModels()
    private  lateinit var  _sensorManager: SensorManager
    private lateinit var wifi: WifiManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewmodel.accelerometerData.observe(this, androidx.lifecycle.Observer {
            if(viewmodel.isWorkingOut.value!! && viewmodel.isConnectedToWifi.value!!){
                viewmodel.currentTime.postValue(Timestamp(System.currentTimeMillis()))
            }
//            binding.accData.text = String.format("x: %.3f | y: %.3f | z: %.3f ", it[0], it[1], it[2] )
        })
        viewmodel.isWorkingOut.observe(this, androidx.lifecycle.Observer {
            if(!it && viewmodel.numberOfWorkouts.value!! >= 1){
                saveData(viewmodel.numberOfWorkouts.value!! + 1, viewmodel.lastWorkout.value!! )
            }
            if(it){
                viewmodel.startedWorkingOutTime.postValue( Timestamp(System.currentTimeMillis()))
            }
        })
        val request = NetworkRequest.Builder()
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .build()


        connManager = this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager;


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
                viewmodel.isConnectedToWifi.postValue(ssid != "<unknown ssid>")
            }
        }
        connManager.registerNetworkCallback(request, networkCallback)
        _sensorManager = this.getSystemService(Context.SENSOR_SERVICE) as SensorManager

        _sensorManager.getDefaultSensor( Sensor.TYPE_LINEAR_ACCELERATION)?.also {
            _sensorManager.registerListener(
                this,
                it,
                SensorManager.SENSOR_STATUS_ACCURACY_LOW,
                SensorManager.SENSOR_DELAY_FASTEST

            )
        }
        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)


        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_about, R.id.navigation_wifi
            )
        )
        loadData()

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    private  fun clearData(){
        val settings = applicationContext.getSharedPreferences("gymLoggerData", 0)
        val editor = settings.edit()
        editor.clear()
        editor.apply()
    }

     private fun saveData(numWorkouts: Int, lastWorkout: Date ){
        val settings = applicationContext.getSharedPreferences("gymLoggerData", 0)
        val editor = settings.edit()
        val sdfmt = SimpleDateFormat("E d y k:m")

        editor.putInt("numWorkouts", numWorkouts)
        editor.putString("lastWorkout", sdfmt.format(lastWorkout))
        editor.apply()
    }

    private fun loadData() {
        val settings = applicationContext.getSharedPreferences("gymLoggerData", 0)
        val editor = settings.edit();
        val lastWorkout = settings.getString("lastWorkout", null)
        val numWorkouts = settings.getInt( "numWorkouts", -1)
        val sdfmt = SimpleDateFormat("E d y k:m")
        if( lastWorkout!= null){
            viewmodel.lastWorkout.postValue(sdfmt.parse(lastWorkout))
        }
        if ( numWorkouts != -1){
            viewmodel.numberOfWorkouts.postValue(numWorkouts )
        }
    }
    override fun onSensorChanged(p0: SensorEvent?) {
        viewmodel.accelerometerData.postValue(p0!!.values.toMutableList())
        viewmodel.isAccelerometerActive.postValue(p0.values[0] >=5 || p0.values[1] >=5 || p0.values[2] >=5)
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
//        textGyro.text = String.format("accuracy %d, data %s", p0!!.accuracy, p0.values.joinToString() )
    }
}