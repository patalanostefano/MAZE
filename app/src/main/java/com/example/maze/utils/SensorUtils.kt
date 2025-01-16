// utils/SensorUtils.kt
package com.example.maze.utils

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow

data class AccelerometerData(val x: Float, val y: Float)

fun SensorManager.accelerometerFlow() = callbackFlow {
    val listener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent) {
            if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
                trySend(AccelerometerData(-event.values[1], event.values[0])) // Landscape mode
            }
        }
        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
    }

    val accelerometer = getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    registerListener(listener, accelerometer, SensorManager.SENSOR_DELAY_GAME)

    awaitClose {
        unregisterListener(listener)
    }
}
