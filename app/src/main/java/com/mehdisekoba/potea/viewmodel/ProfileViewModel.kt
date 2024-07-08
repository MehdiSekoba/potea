package com.mehdisekoba.potea.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mehdisekoba.potea.data.model.SimpleResponse
import com.mehdisekoba.potea.data.model.home.ResponseBanner
import com.mehdisekoba.potea.data.model.profile.ResponseProfile
import com.mehdisekoba.potea.data.model.profile.ResponseShowAddress
import com.mehdisekoba.potea.data.repository.ProfileRepository
import com.mehdisekoba.potea.utils.network.NetworkRequest
import com.mehdisekoba.potea.utils.network.NetworkResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.RequestBody
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(private val repository: ProfileRepository) :
    ViewModel() {
    // show info user
    private val _profileData = MutableLiveData<NetworkRequest<ResponseProfile>>()
    val profileData: LiveData<NetworkRequest<ResponseProfile>> = _profileData

    fun callProfileApi() =
        viewModelScope.launch {
            _profileData.value = NetworkRequest.Loading()
            val response = repository.getInfoUser()
            _profileData.value = NetworkResponse(response).generateResponse()
        }

    //UpdateProfile
    private val _updateProfile = MutableLiveData<NetworkRequest<SimpleResponse>>()
    val updateProfile: LiveData<NetworkRequest<SimpleResponse>> = _updateProfile

    fun callUpdateProfile(name: String, family: String, phone: String) = viewModelScope.launch {
        _updateProfile.value = NetworkRequest.Loading()
        val response = repository.postUpdateProfile(name, family, phone)
        _updateProfile.value = NetworkResponse(response).generateResponse()
    }

    //Upload Avatar
    private val _avatarData = MutableLiveData<NetworkRequest<Unit>>()
    val avatarData: LiveData<NetworkRequest<Unit>> = _avatarData

    fun callUploadAvatarApi(body: RequestBody) = viewModelScope.launch {
        _avatarData.value = NetworkRequest.Loading()
        val response = repository.postUploadAvatar(body)
        _avatarData.value = NetworkResponse(response).generateResponse()
    }


}