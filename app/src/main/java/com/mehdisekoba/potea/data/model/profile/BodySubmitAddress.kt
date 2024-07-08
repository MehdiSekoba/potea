package com.mehdisekoba.potea.data.model.profile

import com.google.gson.annotations.SerializedName

data class BodySubmitAddress(
    @SerializedName("address")
    var address: String? = null,
    @SerializedName("postal_code")
    var postalCode: String? = null,
    @SerializedName("name")
    var name: String? = null,
    @SerializedName("family")
    var Family: String? = null,
    @SerializedName("phone")
    var phone: String? = null,
    @SerializedName("State")
    var state: String? = null,
    @SerializedName("city")
    var city: String? = null
)



