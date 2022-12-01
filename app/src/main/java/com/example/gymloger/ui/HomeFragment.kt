package com.example.gymloger.ui

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.net.wifi.WifiManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.gymloger.ActivityMainViewModel
import com.example.gymloger.databinding.FragmentHomeBinding
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.*
import java.util.logging.Logger

class HomeFragment : Fragment() {
    private  lateinit var  _sensorManager: SensorManager

    private var _binding: FragmentHomeBinding? = null
    private lateinit var wifi: WifiManager
    private val viewModel: ActivityMainViewModel by activityViewModels()
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!


    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        viewModel.accelerometerData.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            if(viewModel.isWorkingOut.value!! && viewModel.isConnectedToWifi.value!!){
                viewModel.currentTime.postValue(Timestamp(System.currentTimeMillis()))
            }
        })

        viewModel.currentTime.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            Log.d("CURRENTTIME", "current time changed")
            println("THIS IS BEING CALLED")
            val diffInMillis = viewModel.timeDifference()
            val diffInSeconds = diffInMillis /1000 as Int % 60
            val diffInMinutes = diffInMillis / 1000 / 60 as Int  % 60
            val diffInHours = diffInMillis / 1000 / 3600 as Int % 24

            binding.currentTime.text = "TIME SINCE STARTED: $diffInHours:$diffInMinutes:$diffInSeconds";
        })
        viewModel.isWorkingOut.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            val text = if (it) { "YES"} else {"NO"}
            binding.workingOut.text = "Currently working out ? $text";
        })
        viewModel.numberOfWorkouts.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            binding.numberWorkouts.text = "Number of workouts: $it";
        })

        viewModel.lastWorkout.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            val sdfmt = SimpleDateFormat("E d k:m")
            binding.lastWorkout.text = "Last Workout: ${sdfmt.format(it)}";
        })


        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}