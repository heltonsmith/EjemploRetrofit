package com.heltonbustos.ejemploretrofit01.retrofit

import com.google.gson.annotations.SerializedName

class Raza(
    @SerializedName("status")
    var status: String,
    @SerializedName("message")
    var imagenes: List<String>
    )