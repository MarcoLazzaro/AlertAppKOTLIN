package com.example.alertapp

import com.google.android.gms.maps.model.LatLng

data class alerts(
    val __v: Int,
    val _id: String,
    val alertLevel: Int,
    val createdAt: String,
    val location: Location,
    var text: String
)