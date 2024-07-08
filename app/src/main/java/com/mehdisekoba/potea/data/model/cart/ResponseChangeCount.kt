package com.mehdisekoba.potea.data.model.cart

import com.google.gson.annotations.SerializedName

data class ResponseChangeCount(

	@field:SerializedName("count")
	val count: Int,

	@field:SerializedName("message")
	val message: String
)
