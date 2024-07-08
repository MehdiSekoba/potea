package com.mehdisekoba.potea.data.repository

import com.mehdisekoba.potea.data.network.ApiServices
import javax.inject.Inject

class CartRepository
@Inject
constructor(
    private val api: ApiServices,
) {
    suspend fun postAddToCart(productId: Int) = api.postAddToCart(productId)

    suspend fun getCartList() = api.getCartList()
    suspend fun postChangeCountItem(id: Int, count: Int) = api.postChangeCountItem(id, count)
    suspend fun postRemoveItem(id: Int) = api.postRemoveItem(id)
}
