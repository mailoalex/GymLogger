package com.example.gymloger.ui.wifi

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class WifiViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is Wifi Fragment"
    }
    val text: LiveData<String> = _text
}