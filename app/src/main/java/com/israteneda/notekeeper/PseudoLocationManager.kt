package com.israteneda.notekeeper

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log

class PseudoLocationManager(private val context: Context,
                            private val callback: (Double, Double) -> Unit) : Runnable {
    private val tag = this::class.simpleName

    private val latitudes = doubleArrayOf(
        pluralsightFarmingtonWestLat,
        pluralsightFarmingtonEastLat,
        pluralsightSouthJordanLat)
    private val longitudes = doubleArrayOf(
        pluralsightFarmingtonWestLon,
        pluralsightFarmingtonEastLon,
        pluralsightSouthJordanLon)
    private var locationIndex = 0

    private val callbackMilliseconds = 3000L

    private var enabled = false
    private val postHandler = Handler(Looper.getMainLooper())

    fun start() {
        enabled = true
        Log.d(tag, "Location manager started")
        triggerCallbackAndScheduleNext()
    }

    fun stop() {
        enabled = false
        postHandler.removeCallbacks(this)
        Log.d(tag, "Location manager stopped")
    }

    override fun run() {
        triggerCallbackAndScheduleNext()
    }

    private fun triggerCallbackAndScheduleNext() {
        callback(latitudes[locationIndex], longitudes[locationIndex])
        locationIndex = (locationIndex + 1) % latitudes.size
        if (enabled)
            postHandler.postDelayed(this, callbackMilliseconds)

    }
}

private const val pluralsightFarmingtonWestLat = 40.983550
private const val pluralsightFarmingtonWestLon = -111.908230
private const val pluralsightFarmingtonEastLat = 40.983870
private const val pluralsightFarmingtonEastLon = -111.906580
private const val pluralsightSouthJordanLat = 40.544370
private const val pluralsightSouthJordanLon = -111.897790