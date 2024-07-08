package com.mehdisekoba.potea.data.repository

import com.mehdisekoba.potea.data.network.ApiServices
import okhttp3.RequestBody
import javax.inject.Inject

class ProfileRepository @Inject constructor(private val api: ApiServices) {
    suspend fun getInfoUser() = api.getInfoUser()
    suspend fun postUpdateProfile(name: String, family: String, phone: String) =
        api.postUpdateProfile(name, family, phone)

    suspend fun postUploadAvatar(body: RequestBody) = api.postUploadAvatar(body)
}