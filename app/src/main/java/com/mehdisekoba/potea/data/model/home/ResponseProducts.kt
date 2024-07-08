package com.mehdisekoba.potea.data.model.home

import com.google.gson.annotations.SerializedName

class ResponseProducts : ArrayList<ResponseProducts.ResponseProductsItem>() {
    data class ResponseProductsItem(
        @SerializedName("id")
        val id: Int?, // 17
        @SerializedName("image")
        val image: String?, // https://solinbazar.com/wp-content/uploads/2024/03/photo10409460568.jpg
        @SerializedName("name")
        val name: String?, // Kalanchoe
        @SerializedName("price")
        val price: Int?, // 2150
        @SerializedName("status")
        val status: String?, // false
    )
}
