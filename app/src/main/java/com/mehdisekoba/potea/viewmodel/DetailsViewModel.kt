package com.mehdisekoba.potea.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mehdisekoba.potea.data.model.SimpleResponse
import com.mehdisekoba.potea.data.model.detail.ResponseAddBookmark
import com.mehdisekoba.potea.data.model.detail.ResponseDetails
import com.mehdisekoba.potea.data.model.detail.ResponsePriceHistory
import com.mehdisekoba.potea.data.model.detail.ResponseShowComment
import com.mehdisekoba.potea.data.repository.DetailsRepository
import com.mehdisekoba.potea.utils.network.NetworkRequest
import com.mehdisekoba.potea.utils.network.NetworkResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailsViewModel
@Inject
constructor(
    private val repository: DetailsRepository,
) : ViewModel() {
    // Detail
    private val _detailData = MutableLiveData<NetworkRequest<ResponseDetails>>()
    val detailData: LiveData<NetworkRequest<ResponseDetails>> = _detailData

    fun callDetailApi(id: Int) =
        viewModelScope.launch {
            _detailData.value = NetworkRequest.Loading()
            val response = repository.getDetailsProduct(id)
            _detailData.value = NetworkResponse(response).generateResponse()
        }

    // Product ID
    private val _productID = MutableLiveData<Int>()
    val productID: LiveData<Int> = _productID

    fun setProductId(id: Int) {
        _productID.value = id
    }

    // property
    private val _property = MutableLiveData<List<ResponseDetails.Property>>()
    val property: LiveData<List<ResponseDetails.Property>> = _property

    fun setProperty(property: List<ResponseDetails.Property>) {
        _property.value = property
    }

    // price history
    private var _priceHistory = MutableLiveData<NetworkRequest<ResponsePriceHistory>>()
    val priceHistory: LiveData<NetworkRequest<ResponsePriceHistory>> = _priceHistory

    fun callPriceHistoryApi(id: Int) =
        viewModelScope.launch {
            _priceHistory.value = NetworkRequest.Loading()
            val response = repository.getHistoryPriceChart(id)
            _priceHistory.value = NetworkResponse(response).generateResponse()
        }

    // show comment
    private var _showComment = MutableLiveData<NetworkRequest<ResponseShowComment>>()
    val showComment: LiveData<NetworkRequest<ResponseShowComment>> = _showComment

    fun callShowCommentApi(id: Int) =
        viewModelScope.launch {
            _showComment.value = NetworkRequest.Loading()
            val response = repository.getShowComment(id)
            _showComment.value = NetworkResponse(response).generateResponse()
        }

    // Send Comments
    private val _sendCommentsData = MutableLiveData<NetworkRequest<SimpleResponse>>()
    val sendCommentsData: LiveData<NetworkRequest<SimpleResponse>> = _sendCommentsData

    fun callAddNewComment(
        productId: Int,
        content: String,
        title: String,
        score: Int,
    ) = viewModelScope.launch {
        _sendCommentsData.value = NetworkRequest.Loading()
        val response = repository.postAddNewComment(productId, content, title, score)
        _sendCommentsData.value = NetworkResponse(response).generateResponse()
    }

    private val _likeData = MutableLiveData<NetworkRequest<ResponseAddBookmark>>()
    val likeData: LiveData<NetworkRequest<ResponseAddBookmark>> = _likeData

    fun callProductLike(id: Int) = viewModelScope.launch {
        _likeData.value = NetworkRequest.Loading()
        val response = repository.postAddToBookMark(id)
        _likeData.value = NetworkResponse(response).generateResponse()
    }


}
