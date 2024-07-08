package com.mehdisekoba.potea.data.model.detail


import com.google.gson.annotations.SerializedName

data class ResponseDetails(
    @SerializedName("amazing_time")
    val amazingTime: Long?, // 1721473464708
    @SerializedName("comment_count")
    val commentCount: Int?, // 0
    @SerializedName("delivery_time")
    val deliveryTime: Int?, // 2
    @SerializedName("description")
    val description: String?, // Bonsai is one of the resistant indoor plants that originates from East Asia. One of the most abundant types of bonsai is ginseng bonsai. This name evokes a large plant that is placed in a small environment like a pot for cultivation. In fact, the size of this plant is large, and with a lot of effort, it is possible to use it in a small size. Ginseng bonsai plant is the most famous species of this type of plant in Iran. That is why this plant is one of the most attractive indoor plants.Maintenance conditions: You only need to pay attention to a few points regarding its maintenance. One of these things is paying attention to the right light for the growth of this plant. This plant needs a lot of light. This plant hates the dark, and the best place to keep it can be behind the window, where, despite absorbing a lot of light, it is not exposed to strong sunlight.
    @SerializedName("general_cat_id")
    val generalCatId: Int?, // 1
    @SerializedName("id")
    val id: Int?, // 1
    @SerializedName("images")
    val images: List<Image>?,
    @SerializedName("in_basket")
    val inBasket: Int?, // 0
    @SerializedName("name")
    val name: String?, // Ginseng bonsai pot
    @SerializedName("off_percent")
    val offPercent: Int?, // 10
    @SerializedName("off_price")
    val offPrice: String?, // 27
    @SerializedName("price")
    val price: Int?, // 30
    @SerializedName("properties")
    val properties: List<Property>?,
    @SerializedName("score")
    val score: Double?, // 4.1
    @SerializedName("sells_count")
    val sellsCount: Int?, // 1
    @SerializedName("status")
    val status: String?, // false
    @SerializedName("view")
    val view: Int?, // 0
    val isFavorite: Boolean

) {
    data class Image(
        @SerializedName("image")
        val image: String? // https://solinbazar.com/wp-content/uploads/2024/03/photo10409346038.jpg
    )

    data class Property(
        @SerializedName("property_name")
        val propertyName: String?, // Flowers used:
        @SerializedName("value")
        val value: String? // Ginseng
    )
}