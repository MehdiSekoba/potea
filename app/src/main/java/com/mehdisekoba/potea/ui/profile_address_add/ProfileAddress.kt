package com.mehdisekoba.potea.ui.profile_address_add

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.mehdisekoba.potea.R
import com.mehdisekoba.potea.data.model.profile.ResponseShowAddress
import com.mehdisekoba.potea.databinding.FragmentProfileAddressBinding
import com.mehdisekoba.potea.ui.profile.ProfileFragmentDirections
import com.mehdisekoba.potea.utils.base.BaseFragment
import com.mehdisekoba.potea.utils.network.NetworkRequest
import com.mehdisekoba.potea.utils.other.CustomToast
import com.mehdisekoba.potea.viewmodel.ProfileAddressViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ProfileAddress : BaseFragment<FragmentProfileAddressBinding>() {
    override val bindingInflater: (inflater: LayoutInflater) -> FragmentProfileAddressBinding
        get() = FragmentProfileAddressBinding::inflate

    @Inject
    lateinit var address: AdapterAddress

    //other
    private val viewModel by viewModels<ProfileAddressViewModel>()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //initViews
        binding.apply {
            lifecycleScope.launch {
                shimmerToolbar.veil()
                addAddress.isVisible = false
                delay(400)
                shimmerToolbar.unVeil()
                addAddress.isVisible = true

            }
            //toolbar
            toolbar.apply {
                toolbarTitleTxt.text = getString(R.string.address)
                toolbarBackImg.setOnClickListener { findNavController().popBackStack() }
                toolbarOptionImg.isVisible = false

            }
            //check network
            if (isNetworkAvailable) {
                viewModel.callShowAddress()
            } else {
                CustomToast.makeText(
                    requireContext(),
                    getString(R.string.checkYourNetwork),
                    CustomToast.LONG_DURATION,
                    CustomToast.WARNING, true
                ).show()
            }
            //click
            addAddress.setOnClickListener {
                val direction =ProfileAddressDirections.actionToProfileAddAddresses()
                findNavController().navigate(direction)
            }
        }
        //load Data
        loadAddressData()
    }

    private fun loadAddressData() {
        binding.apply {
            viewModel.addressData.observe(viewLifecycleOwner) { response ->
                when (response) {
                    is NetworkRequest.Loading -> {
                        addressList.veil()
                    }

                    is NetworkRequest.Success -> {
                        addressList.unVeil()
                        response.data?.let { data ->
                            if (data.size > 0) {
                                initRecyclerView(data)
                                emptyLay.isVisible = false
                            } else {
                                addressList.isVisible = false
                                emptyLay.isVisible = true
                                emptyAddress.txtEmpty.text = getString(R.string.empty_address)
                            }

                        }
                    }

                    is NetworkRequest.Error -> {
                        addressList.unVeil()
                        CustomToast.makeText(
                            requireContext(),
                            response.error!!,
                            CustomToast.LONG_DURATION,
                            CustomToast.ERROR, true
                        ).show()
                    }
                }
            }
        }
    }

    private fun initRecyclerView(data: ResponseShowAddress) {
        address.setData(data)
        binding.addressList.apply {
            setAdapter(address)
            setLayoutManager(LinearLayoutManager(requireContext()))
            addVeiledItems(10)
        }
    }
}