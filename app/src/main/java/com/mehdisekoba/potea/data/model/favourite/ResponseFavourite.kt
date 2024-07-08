package com.mehdisekoba.potea.data.model.favourite


import com.google.gson.annotations.SerializedName

class ResponseFavourite : ArrayList<ResponseFavourite.ResponseFavouriteItem>(){
    data class ResponseFavouriteItem(
        @SerializedName("id")
        val id: String?, // 1
        @SerializedName("name")
        val name: String?, // Ginseng bonsai pot
        @SerializedName("url")
        val url: String? // https://solinbazar.com/wp-content/uploads/2024/03/photo10409346038.jpg
    )
}