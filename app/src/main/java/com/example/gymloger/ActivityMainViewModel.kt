package com.example.gymloger

import android.hardware.SensorManager
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.util.*
import java.sql.Timestamp
import java.text.SimpleDateFormat

class ActivityMainViewModel : ViewModel() {
    var numberOfWorkouts = MutableLiveData<Int>( 0)
    var currentTime = MutableLiveData<Timestamp>(Timestamp( System.currentTimeMillis()))
    var startedWorkingOutTime = MutableLiveData<Timestamp>( Timestamp( System.currentTimeMillis()));
    var isWorkingOut = MutableLiveData<Boolean>( false)
    var lastWorkout = MutableLiveData<Date>(Date())
    var accelerometerData = MutableLiveData<MutableList<Float>>()
    var isConnectedToWifi = MutableLiveData<Boolean>(false);
    var isAccelerometerActive  = MutableLiveData<Boolean>(false);
    fun reset(){
        numberOfWorkouts.postValue(0);
        isWorkingOut.postValue(false)
        accelerometerData.postValue(mutableListOf(0.0F,0.0F,0.0F))
        isConnectedToWifi.postValue(false)
        isAccelerometerActive.postValue(false)
        startedWorkingOutTime.postValue(Timestamp( System.currentTimeMillis()))
        currentTime.postValue(Timestamp( System.currentTimeMillis()))
    }





    fun timeDifference(): Long{
        return currentTime.value!!.time - startedWorkingOutTime.value!!.time  ;
    }
}