package com.mehdisekoba.potea.data.repository

import com.mehdisekoba.potea.data.network.ApiServices
import javax.inject.Inject

class HomeRepository
    @Inject
    constructor(private val api: ApiServices) {
        suspend fun getBannerList() = api.getBannerList()

        suspend fun getAmazingList() = api.getAmazingList()

        suspend fun getNewProductList() = api.getNewProductList()

        suspend fun getPopularProductList() = api.getPopularProductList()
    }
