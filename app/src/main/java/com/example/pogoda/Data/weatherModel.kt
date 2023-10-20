package com.example.pogoda.Data

data class weatherModel(
    val city:String,
    val time:String,
    val currentTemp:String,
    val condition:String,
    val icon:String,
    val maxTemp:String,
    val minTemp:String,
    val hours:String,
)