package com.mehdisekoba.potea.data.model

import com.google.gson.annotations.SerializedName

data class SimpleResponse(
    @SerializedName("message")
    val message: String?,
)
