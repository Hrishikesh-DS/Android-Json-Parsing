package com.example.androidassignment

data class DataClass(
    val activityName: String,
    val contentDescription: String,
    val elementName: String,
    val children: List<DataClass>
)