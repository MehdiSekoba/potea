package com.mehdisekoba.potea.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mehdisekoba.potea.data.model.profile.BodySubmitAddress
import com.mehdisekoba.potea.data.model.profile.ResponseShowAddress
import com.mehdisekoba.potea.data.model.profile.ResponseSubmitAddress
import com.mehdisekoba.potea.data.repository.ProfileAddressRepository
import com.mehdisekoba.potea.utils.network.NetworkRequest
import com.mehdisekoba.potea.utils.network.NetworkResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileAddressViewModel @Inject constructor(private val repository: ProfileAddressRepository) :
    ViewModel() {
    //City list
    private val _submitAddressData = MutableLiveData<NetworkRequest<ResponseSubmitAddress>>()
    val submitAddressData: LiveData<NetworkRequest<ResponseSubmitAddress>> = _submitAddressData

    fun callSubmitAddressApi(body: BodySubmitAddress) = viewModelScope.launch {
        _submitAddressData.value = NetworkRequest.Loading()
        val response = repository.postSubmitAddress(body)
        _submitAddressData.value = NetworkResponse(response).generateResponse()
    }

    //show address
    private val _addressData = MutableLiveData<NetworkRequest<ResponseShowAddress>>()
    val addressData: LiveData<NetworkRequest<ResponseShowAddress>> = _addressData
    fun callShowAddress() = viewModelScope.launch {
        _addressData.value = NetworkRequest.Loading()
        val response = repository.getShowAddress()
        _addressData.value = NetworkResponse(response).generateResponse()
    }
}