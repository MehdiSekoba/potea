package com.mehdisekoba.potea.data.model.profile

import com.google.gson.annotations.SerializedName

data class ResponseSubmitAddress(
    @SerializedName("State")
    val state: String,
    @SerializedName("city")
    val city: String,
    @SerializedName("address")
    val address: String,
    @SerializedName("poatal_code")
    val poatalCode: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("family")
    val Family: String,
    @SerializedName("phone")
    val phone: String
)
