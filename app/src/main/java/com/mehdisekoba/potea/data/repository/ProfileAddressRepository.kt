package com.mehdisekoba.potea.data.repository

import com.mehdisekoba.potea.data.model.profile.BodySubmitAddress
import com.mehdisekoba.potea.data.network.ApiServices
import javax.inject.Inject

class ProfileAddressRepository @Inject constructor(private val api: ApiServices) {
    /*suspend fun getProfileAddressesList() = api.getProfileAddressesList()
    suspend fun getProvinceList() = api.getProvinceList()
    suspend fun getCityList(id: Int) = api.getCityList(id)
    suspend fun deleteProfileAddress(id: Int) = api.deleteProfileAddress(id)
     */
    suspend fun postSubmitAddress(body: BodySubmitAddress) = api.postSubmitAddress(body)
    suspend fun getShowAddress() = api.getShowAddress()

}