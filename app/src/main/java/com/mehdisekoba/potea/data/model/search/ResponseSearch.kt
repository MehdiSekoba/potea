package com.mehdisekoba.potea.data.model.search


import com.google.gson.annotations.SerializedName

data class ResponseSearch(
    @SerializedName("results")
    val results: List<Result>?,
    @SerializedName("search_term")
    val searchTerm: Any?, // null
    @SerializedName("total_results")
    val totalResults: Int? // 10
) {
    data class Result(
        @SerializedName("id")
        val id: String?, // 1
        @SerializedName("image")
        val image: String?, // https://solinbazar.com/wp-content/uploads/2024/03/photo10409346038.jpg
        @SerializedName("name")
        val name: String?, // Ginseng bonsai pot
        @SerializedName("price")
        val price: String?, // 30.00
        @SerializedName("score")
        val score: Double? // 4.1
    )
}