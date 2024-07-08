package com.mehdisekoba.potea.data.model.profile


import com.google.gson.annotations.SerializedName

data class ResponseProfile(
    @SerializedName("date")
    val date: String?, // 2024-06-17 07:08:04
    @SerializedName("email")
    val email: String?, // webmahdi72@gmail.com
    @SerializedName("family")
    val family: String?, // imani
    @SerializedName("name")
    val name: String?, // hasti
    @SerializedName("profile_photo")
    val profilePhoto: String?, // profile_photo_667aa247af5f82.02001830.jpg
    @SerializedName("wallet")
    val wallet: String? // 0
)