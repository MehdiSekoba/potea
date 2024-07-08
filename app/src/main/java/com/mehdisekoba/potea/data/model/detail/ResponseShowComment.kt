package com.mehdisekoba.potea.data.model.detail

import com.google.gson.annotations.SerializedName

class ResponseShowComment : ArrayList<ResponseShowComment.ResponseShowCommentItem>() {
    data class ResponseShowCommentItem(
        @SerializedName("Advice")
        val advice: String?, // I recommend
        @SerializedName("content")
        val content: String?, // There was no problem until now
        @SerializedName("date")
        val date: String?, // 2 year(s) ago
        @SerializedName("family")
        val family: String?, // Smith
        @SerializedName("id")
        val id: Int?, // 2
        @SerializedName("name")
        val name: String?, // Jane
        @SerializedName("negative")
        val negative: String?,
        @SerializedName("positive")
        val positive: String?,
        @SerializedName("title")
        val title: String?, // It is a very good product
        @Suppress("ktlint:standard:value-parameter-comment")
        @SerializedName("value")
        val value: Float?, // 3.1
    )
}
