package com.example.androidassignment

// the data class to store the json data, note that the children of this data
// class is the data class itself as we are populating the same type of data repeatedly.
data class DataClass(
    val activityName: String,
    val contentDescription: String,
    val elementName: String,
    val children: List<DataClass>
)