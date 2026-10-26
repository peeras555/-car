package com.carlauncher.dash

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat

enum class Net { WIFI, MOBILE, OTHER, NONE }

/** อ่านสถานะเครือข่ายและ Bluetooth จริง ทำงานเฉพาะตอนหน้าจอ Launcher แสดงอยู่ */
class StatusMonitor(private val ctx: Context) {

    var net: Net by mutableStateOf(Net.NONE)
        private set
    var bluetoothOn: Boolean by mutableStateOf(false)
        private set

    private val cm = ctx.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    private var running = false

    private fun classify(c: NetworkCapabilities?): Net = when {
        c == null -> Net.NONE
        c.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> Net.WIFI
        c.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> Net.MOBILE
        else -> Net.OTHER
    }

    private val netCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onCapabilitiesChanged(network: Network, caps: NetworkCapabilities) {
            net = classify(caps)
        }

        override fun onLost(network: Network) {
            net = Net.NONE
        }
    }

    private val btReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            bluetoothOn = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR) ==
                BluetoothAdapter.STATE_ON
        }
    }

    fun start() {
        if (running) return
        running = true
        try {
            net = classify(cm.getNetworkCapabilities(cm.activeNetwork))
            cm.registerDefaultNetworkCallback(netCallback)
        } catch (e: Exception) {
            net = Net.NONE
        }
        try {
            val adapter = (ctx.getSystemService(Context.BLUETOOTH_SERVICE) as? BluetoothManager)?.adapter
            bluetoothOn = adapter?.isEnabled == true
        } catch (e: SecurityException) {
            bluetoothOn = false
        }
        ContextCompat.registerReceiver(
            ctx, btReceiver, IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED),
            ContextCompat.RECEIVER_EXPORTED
        )
    }

    fun stop() {
        if (!running) return
        running = false
        try {
            cm.unregisterNetworkCallback(netCallback)
        } catch (e: Exception) {
        }
        try {
            ctx.unregisterReceiver(btReceiver)
        } catch (e: Exception) {
        }
    }
}
