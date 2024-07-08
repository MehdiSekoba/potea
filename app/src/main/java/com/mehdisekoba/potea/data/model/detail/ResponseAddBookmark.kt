package com.mehdisekoba.potea.data.model.detail


import com.google.gson.annotations.SerializedName

data class ResponseAddBookmark(
    @SerializedName("message")
    val message: String?, // Added to your favorites list
    @SerializedName("status")
    val status: String? // true
)