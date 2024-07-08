package com.mehdisekoba.potea.data.repository

import com.mehdisekoba.potea.data.network.ApiServices
import javax.inject.Inject

class DetailsRepository
    @Inject
    constructor(
        private val api: ApiServices,
    ) {
        suspend fun getDetailsProduct(id: Int) = api.getDetailsProduct(id)

        suspend fun getHistoryPriceChart(id: Int) = api.getHistoryPriceChart(id)

        suspend fun postAddToBookMark(id: Int) = api.postAddToBookMark(id)

        suspend fun getShowComment(id: Int) = api.getShowComment(id)

        suspend fun postAddNewComment(
            productId: Int,
            content: String,
            title: String,
            score: Int,
        ) = api.postAddNewComment(productId, content, title, score)
    }
