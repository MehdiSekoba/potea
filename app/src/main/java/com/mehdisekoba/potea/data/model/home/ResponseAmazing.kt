package com.mehdisekoba.potea.data.model.home

import com.google.gson.annotations.SerializedName

class ResponseAmazing : ArrayList<ResponseAmazing.ResponseAmazingItem>() {
    data class ResponseAmazingItem(
        @SerializedName("amazing_price")
        val amazingPrice: Double?, // 377.3
        @SerializedName("amazing_time")
        val amazingTime: Long?, // 1719459792715
        @SerializedName("id")
        val id: Int?, // 1
        @SerializedName("image")
        val image: String?, // https://solinbazar.com/wp-content/uploads/2024/03/photo10409346038.jpg
        @SerializedName("name")
        val name: String?, // Ginseng bonsai pot
        @SerializedName("number")
        val number: Int?, // 10
        @SerializedName("off_percent")
        val offPercent: Int?, // 10
        @SerializedName("price")
        val price: Int?, // 30
        @SerializedName("sells_count")
        val sellsCount: Int?, // 1
        @SerializedName("status")
        val status: String?, // false
    )
}
