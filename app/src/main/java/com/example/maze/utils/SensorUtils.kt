// utils/SensorUtils.kt
package com.example.maze.utils

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow

data class SensorData(
    val accelerometerX: Float = 0f,
    val accelerometerY: Float = 0f,
    val gyroscopeX: Float = 0f,
    val gyroscopeY: Float = 0f
)

fun SensorManager.combinedSensorFlow() = callbackFlow {
    var currentAccelX = 0f
    var currentAccelY = 0f
    var currentGyroX = 0f
    var currentGyroY = 0f

    val listener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent) {
            when (event.sensor.type) {
                Sensor.TYPE_ACCELEROMETER -> {
                    // For landscape mode, we swap X and Y and invert as needed
                    currentAccelX = -event.values[1]
                    currentAccelY = event.values[0]
                }
                Sensor.TYPE_GYROSCOPE -> {
                    currentGyroX = event.values[1]
                    currentGyroY = -event.values[0]
                }
            }

            // Combine both sensor data and send
            trySend(
                SensorData(
                    accelerometerX = currentAccelX,
                    accelerometerY = currentAccelY,
                    gyroscopeX = currentGyroX,
                    gyroscopeY = currentGyroY
                )
            )
        }

        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
    }

    // Register both sensors
    val accelerometer = getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    val gyroscope = getDefaultSensor(Sensor.TYPE_GYROSCOPE)

    registerListener(listener, accelerometer, SensorManager.SENSOR_DELAY_GAME)
    registerListener(listener, gyroscope, SensorManager.SENSOR_DELAY_GAME)

    awaitClose {
        unregisterListener(listener)
    }
}

// Extension function to get weighted sensor values
fun SensorData.getWeightedMovement(
    accelWeight: Float = 0.7f,
    gyroWeight: Float = 0.3f
): Pair<Float, Float> {
    val weightedX = (accelerometerX * accelWeight) + (gyroscopeX * gyroWeight)
    val weightedY = (accelerometerY * accelWeight) + (gyroscopeY * gyroWeight)
    return Pair(weightedX, weightedY)
}
