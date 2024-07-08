package com.mehdisekoba.potea.data.model.detail

import com.google.gson.annotations.SerializedName

class ResponsePriceHistory : ArrayList<ResponsePriceHistory.ResponsePriceHistoryItem>() {
    data class ResponsePriceHistoryItem(
        @SerializedName("date")
        val date: String?, // 2024/05/04
        @SerializedName("id")
        val id: String?, // 91
        @SerializedName("price")
        val price: Double?, // 36.78
    )
}
