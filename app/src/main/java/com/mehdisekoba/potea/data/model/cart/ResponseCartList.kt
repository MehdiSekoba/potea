package com.mehdisekoba.potea.data.model.cart


import com.google.gson.annotations.SerializedName

data class ResponseCartList(
    @SerializedName("count")
    val count: Int?, // 3
    @SerializedName("payable_price")
    val payablePrice: Int?, // 55460
    @SerializedName("product_item")
    val productItem: List<ProductItem>?,
    @SerializedName("total_off_price")
    val totalOffPrice: Int?, // 0
    @SerializedName("total_price")
    val totalPrice: Int? // 55460
) {
    data class ProductItem(
        @SerializedName("count")
        var count: Int?, // 1
        @SerializedName("delivery_amount")
        val deliveryAmount: Double?, // 1.59
        @SerializedName("id")
        val id: String?, // 10
        @SerializedName("image")
        val image: String?, // https://solinbazar.com/wp-content/uploads/2022/09/img-6456.webp
        @SerializedName("name")
        val name: String?, // Peach Anthurium Potted Plant
        @SerializedName("off_percent")
        val offPercent: Int?, // 0
        @SerializedName("off_price")
        val offPrice: Int?, // 2950
        @SerializedName("price")
        val price: Double?, // 2950.00
        @SerializedName("total_product_price")
        val totalProductPrice: Int? // 2950
    )
}