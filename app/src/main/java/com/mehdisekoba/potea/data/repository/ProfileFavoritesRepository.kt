package com.mehdisekoba.potea.data.repository

import com.mehdisekoba.potea.data.network.ApiServices
import javax.inject.Inject

class ProfileFavoritesRepository @Inject constructor(private val api: ApiServices) {
    suspend fun getBookmarkList() = api.getBookmarkList()
}