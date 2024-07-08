package com.mehdisekoba.potea.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mehdisekoba.potea.data.model.home.ResponseAmazing
import com.mehdisekoba.potea.data.model.home.ResponseBanner
import com.mehdisekoba.potea.data.model.home.ResponseProducts
import com.mehdisekoba.potea.data.repository.HomeRepository
import com.mehdisekoba.potea.utils.network.NetworkRequest
import com.mehdisekoba.potea.utils.network.NetworkResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel
    @Inject
    constructor(private val repository: HomeRepository) : ViewModel() {
        init {
            viewModelScope.launch {
                delay(200)
                callBannerApi()
                callPopularProductApi()
            }
        }

        @Suppress("ktlint:standard:backing-property-naming")

        // Banner
        private val _bannerData = MutableLiveData<NetworkRequest<ResponseBanner>>()
        val bannerData: LiveData<NetworkRequest<ResponseBanner>> = _bannerData

        private fun callBannerApi() =
            viewModelScope.launch {
                _bannerData.value = NetworkRequest.Loading()
                val response = repository.getBannerList()
                _bannerData.value = NetworkResponse(response).generateResponse()
            }

        // Discount
        private val _discountData = MutableLiveData<NetworkRequest<ResponseAmazing>>()
        val discountData: LiveData<NetworkRequest<ResponseAmazing>> = _discountData

        fun callDiscountApi() =
            viewModelScope.launch {
                _discountData.value = NetworkRequest.Loading()
                val response = repository.getAmazingList()
                _discountData.value = NetworkResponse(response).generateResponse()
            }

        // NewProduct
        private val _newProductData = MutableLiveData<NetworkRequest<ResponseProducts>>()
        val newProductData: LiveData<NetworkRequest<ResponseProducts>> = _newProductData

        fun callNewProductApi() =
            viewModelScope.launch {
                _newProductData.value = NetworkRequest.Loading()
                val response = repository.getNewProductList()
                _newProductData.value = NetworkResponse(response).generateResponse()
            }

        // popularProduct
        private val _popularProduct = MutableLiveData<NetworkRequest<ResponseProducts>>()
        val popularProduct: LiveData<NetworkRequest<ResponseProducts>> = _popularProduct

        private fun callPopularProductApi() =
            viewModelScope.launch {
                _popularProduct.value = NetworkRequest.Loading()
                val response = repository.getPopularProductList()
                _popularProduct.value = NetworkResponse(response).generateResponse()
            }
    }
