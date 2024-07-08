package com.mehdisekoba.potea.data.model.profile


import com.google.gson.annotations.SerializedName

class ResponseShowAddress : ArrayList<ResponseShowAddress.ResponseShowAddressItem>(){
    data class ResponseShowAddressItem(
        @SerializedName("address")
        val address: String?, // shiraz city
        @SerializedName("city")
        val city: String?, // shiraz
        @SerializedName("family")
        val family: String?, // sekoba
        @SerializedName("id")
        val id: Int?, // 3
        @SerializedName("name")
        val name: String?, // mehdi
        @SerializedName("phone")
        val phone: String?, // 09213218287
        @SerializedName("postal_code")
        val postalCode: String?, // 7388133333
        @SerializedName("State")
        val state: String? // shiraz
    )
}