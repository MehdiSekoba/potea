package com.mehdisekoba.potea.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mehdisekoba.potea.data.model.search.ResponseSearch
import com.mehdisekoba.potea.data.repository.SearchRepository
import com.mehdisekoba.potea.utils.network.NetworkRequest
import com.mehdisekoba.potea.utils.network.NetworkResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repository: SearchRepository

) : ViewModel() {
    //Search

    private val _searchData = MutableLiveData<NetworkRequest<ResponseSearch>>()
    val searchData: LiveData<NetworkRequest<ResponseSearch>> = _searchData

    fun callSearchApi(keyWords: String) = viewModelScope.launch {
        _searchData.value = NetworkRequest.Loading()
        val response = repository.postSearchList(keyWords)
        _searchData.value = NetworkResponse(response).generateResponse()
    }


}