package com.example.gymloger

import android.os.Bundle
import android.text.format.Time
import androidx.activity.viewModels
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


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewmodel: ActivityMainViewModel by viewModels()

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

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)


        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_about, R.id.navigation_wifi
            )
        )
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
}