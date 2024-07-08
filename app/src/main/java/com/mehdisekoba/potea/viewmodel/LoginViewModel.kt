package com.mehdisekoba.potea.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mehdisekoba.potea.data.model.login.ResponseRegister
import com.mehdisekoba.potea.data.repository.LoginRepository
import com.mehdisekoba.potea.utils.network.NetworkRequest
import com.mehdisekoba.potea.utils.network.NetworkResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(private val repository: LoginRepository) : ViewModel() {
    //Register
    private val _registerData = MutableLiveData<NetworkRequest<ResponseRegister>>()
    val registerData: LiveData<NetworkRequest<ResponseRegister>> = _registerData

    fun callRegisterApi(email: String) = viewModelScope.launch {
        _registerData.value = NetworkRequest.Loading()
        val response = repository.postLogin(email)
        _registerData.value = NetworkResponse(response).generateResponse()
    }
}