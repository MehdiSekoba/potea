package com.mehdisekoba.potea.data.repository

import com.mehdisekoba.potea.data.network.ApiServices
import javax.inject.Inject

class LoginRepository @Inject constructor(private val api: ApiServices) {
    suspend fun postLogin(email:String) = api.postLogin(email)
}