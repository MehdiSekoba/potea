package com.mehdisekoba.potea.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mehdisekoba.potea.data.model.favourite.ResponseFavourite
import com.mehdisekoba.potea.data.repository.ProfileFavoritesRepository
import com.mehdisekoba.potea.utils.network.NetworkRequest
import com.mehdisekoba.potea.utils.network.NetworkResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ProfileFavoritesViewModel @Inject constructor(private val repository: ProfileFavoritesRepository) :
    ViewModel() {
    //Favorites list
    private val _favoritesData = MutableLiveData<NetworkRequest<ResponseFavourite>>()
    val favoritesData: LiveData<NetworkRequest<ResponseFavourite>> = _favoritesData

    fun callFavoritesApi() = viewModelScope.launch {
        _favoritesData.value = NetworkRequest.Loading()
        val response = repository.getBookmarkList()
        _favoritesData.value = NetworkResponse(response).generateResponse()
    }


}