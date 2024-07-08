package com.mehdisekoba.potea.data.model.cart

import com.google.gson.annotations.SerializedName

data class ResponseTransaction(

	@field:SerializedName("ref_id")
	val refId: String?,

	@field:SerializedName("message")
	val message: String?
)
