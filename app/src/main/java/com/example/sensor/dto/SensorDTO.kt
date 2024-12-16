package com.example.sensor.dto

data class SensorDTO(
    val humidity: Double = 0.0,
    val temperature_c: Double = 0.0,
    val soil_moisture: Double = 0.0,
    val timestamp: String = ""
)