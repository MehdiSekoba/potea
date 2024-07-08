package com.mehdisekoba.potea.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mehdisekoba.potea.data.model.SimpleResponse
import com.mehdisekoba.potea.data.model.cart.ResponseCartList
import com.mehdisekoba.potea.data.model.cart.ResponseChangeCount
import com.mehdisekoba.potea.data.model.cart.ResponseTransaction
import com.mehdisekoba.potea.data.repository.CartRepository
import com.mehdisekoba.potea.utils.network.NetworkRequest
import com.mehdisekoba.potea.utils.network.NetworkResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CartViewModel
@Inject
constructor(
    private val repository: CartRepository,
) : ViewModel() {
    // Add to cart
    private val _addToCartData = MutableLiveData<NetworkRequest<SimpleResponse>>()
    val addToCartData: LiveData<NetworkRequest<SimpleResponse>> = _addToCartData

    fun callAddTOCartApi(id: Int) =
        viewModelScope.launch {
            _addToCartData.value = NetworkRequest.Loading()
            val response = repository.postAddToCart(id)
            _addToCartData.value = NetworkResponse(response).generateResponse()
        }

    //Cart list
    private val _cartListData = MutableLiveData<NetworkRequest<ResponseCartList>>()
    val cartListData: LiveData<NetworkRequest<ResponseCartList>> = _cartListData

    fun callCartListApi() = viewModelScope.launch {
        _cartListData.value = NetworkRequest.Loading()
        val response = repository.getCartList()
        _cartListData.value = NetworkResponse(response).generateResponse()
    }

    //change count
    private val _changeCountData = MutableLiveData<NetworkRequest<ResponseChangeCount>>()
    fun callChangeCountApi(id: Int, count: Int) =
        viewModelScope.launch {
            _changeCountData.value = NetworkRequest.Loading()
            val response = repository.postChangeCountItem(id, count)
            _changeCountData.value = NetworkResponse(response).generateResponse()
        }

    //delete
    private val _deleteItemData = MutableLiveData<NetworkRequest<SimpleResponse>>()
    val deleteItemData: LiveData<NetworkRequest<SimpleResponse>> = _deleteItemData
    fun callDeleteItemApi(id: Int) =
        viewModelScope.launch {
            _deleteItemData.value = NetworkRequest.Loading()
            val response = repository.postRemoveItem(id)
            _deleteItemData.value = NetworkResponse(response).generateResponse()
        }

}
