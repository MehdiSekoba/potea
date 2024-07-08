package com.mehdisekoba.potea.data.model.login


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
data class ResponseRegister(
    @SerializedName("code")
    val code: @RawValue String?, // 2025
    @SerializedName("email")
    val email: @RawValue String?, // androidstorage@gmail.com
    @SerializedName("message")
    val message: String?, // success
    @SerializedName("token")
    val token: @RawValue String? // c1adb81231d18c38acf71d15f2f03fdc2d002b427c5f349592d18e00c35104a7c6f2eb061717340917
) : Parcelable