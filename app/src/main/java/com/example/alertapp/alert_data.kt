package com.example.alertapp;

import android.location.Location
import com.google.gson.annotations.SerializedName


data class AlertData (

    @SerializedName("alertLevel") var alertLevel : Int,
    @SerializedName("_id") var Id : String,
    @SerializedName("text") var text : String,
    @SerializedName("location") var location : Location,
    @SerializedName("createdAt") var createdAt : String,
    @SerializedName("__v") var _v : Int

)