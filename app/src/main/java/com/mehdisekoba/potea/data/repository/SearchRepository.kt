package com.mehdisekoba.potea.data.repository

import com.mehdisekoba.potea.data.network.ApiServices
import javax.inject.Inject

class SearchRepository @Inject constructor(private val api: ApiServices) {
    suspend fun postSearchList(
        keyword: String,
    ) = api.postSearch(keyword)
}