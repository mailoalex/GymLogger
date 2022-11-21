package com.example.gymloger.ui

import android.content.Context
import android.net.ConnectivityManager
import android.net.ConnectivityManager.NetworkCallback
import android.net.ConnectivityManager.NetworkCallback.FLAG_INCLUDE_LOCATION_INFO
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
import androidx.lifecycle.ViewModelProvider
import com.example.gymloger.databinding.FragmentHomeBinding
import java.nio.channels.NetworkChannel
import java.util.*

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private lateinit var wifi: WifiManager

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        wifi = requireContext().applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager

        val request = NetworkRequest.Builder()
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .build()


        val connManager: ConnectivityManager =
            requireContext().getSystemService(Context.CONNECTIVITY_SERVICE)
                as ConnectivityManager;



        val networkCallback = object : ConnectivityManager.NetworkCallback(
            FLAG_INCLUDE_LOCATION_INFO) {
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





        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}