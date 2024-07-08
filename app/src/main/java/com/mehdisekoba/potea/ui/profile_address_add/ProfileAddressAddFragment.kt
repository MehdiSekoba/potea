package com.mehdisekoba.potea.ui.profile_address_add

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.github.ihermandev.formatwatcher.FormatWatcher
import com.mehdisekoba.potea.R
import com.mehdisekoba.potea.data.model.profile.BodySubmitAddress
import com.mehdisekoba.potea.databinding.FragmentProfileAddressAddBinding
import com.mehdisekoba.potea.utils.MASK
import com.mehdisekoba.potea.utils.PHONE_PATTERN
import com.mehdisekoba.potea.utils.POSTAL_PATTERN
import com.mehdisekoba.potea.utils.base.BaseFragment
import com.mehdisekoba.potea.utils.events.EventBus
import com.mehdisekoba.potea.utils.events.Events
import com.mehdisekoba.potea.utils.extensions.enableLoading
import com.mehdisekoba.potea.utils.extensions.onTextChange
import com.mehdisekoba.potea.utils.network.NetworkRequest
import com.mehdisekoba.potea.utils.other.CustomToast
import com.mehdisekoba.potea.viewmodel.ProfileAddressViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ProfileAddressAddFragment : BaseFragment<FragmentProfileAddressAddBinding>() {

    override val bindingInflater: (inflater: LayoutInflater) -> FragmentProfileAddressAddBinding
        get() = FragmentProfileAddressAddBinding::inflate

    @Inject
    lateinit var body: BodySubmitAddress

    //other
    private val viewModel by viewModels<ProfileAddressViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //initViews
        binding.apply {
            //Args
            toolbar.apply {
                //back
                toolbarBackImg.setOnClickListener { findNavController().popBackStack() }

            }

            //check not empty
            nameEdt.onTextChange { updateSubmitButtonState() }
            familyEdt.onTextChange { updateSubmitButtonState() }
            phoneEdt.onTextChange { updateSubmitButtonState() }
            cityEdt.onTextChange { updateSubmitButtonState() }
            addressEdt.onTextChange { updateSubmitButtonState() }
            streetEdt.onTextChange { updateSubmitButtonState() }
            postalEdt.onTextChange { updateSubmitButtonState() }
            val formatter = FormatWatcher(PHONE_PATTERN, MASK)
            phoneEdt.addTextChangedListener(formatter)
            val formatterPostal = FormatWatcher(POSTAL_PATTERN, MASK)
            postalEdt.addTextChangedListener(formatterPostal)
            submitBtn.setOnClickListener {
                if (nameEdt.text.isNullOrEmpty().not()) {
                    body.name = nameEdt.text.toString()
                }
                if (familyEdt.text.isNullOrEmpty().not()) {
                    body.Family = familyEdt.text.toString()
                }
                if (phoneEdt.text.isNullOrEmpty().not()) {
                    body.phone = formatter.rawInput
                }
                if (cityEdt.text.isNullOrEmpty().not()) {
                    body.city = cityEdt.text.toString()
                }
                if (addressEdt.text.isNullOrEmpty().not()) {
                    body.address = addressEdt.text.toString()
                }
                if (streetEdt.text.isNullOrEmpty().not()) {
                    body.state = streetEdt.text.toString()
                }
                if (postalEdt.text.isNullOrEmpty().not()) {
                    body.postalCode = formatterPostal.rawInput
                }
                //Call api
                if (isNetworkAvailable) {
                    viewModel.callSubmitAddressApi(body)
                } else {
                    CustomToast.makeText(
                        requireContext(),
                        getString(R.string.checkYourNetwork),
                        CustomToast.LONG_DURATION,
                        CustomToast.WARNING,
                        true
                    ).show()
                }
            }
        }
        loadSubmitAddressData()

    }

    private fun loadSubmitAddressData() {
        binding.apply {
            viewModel.submitAddressData.observe(viewLifecycleOwner) { response ->
                when (response) {
                    is NetworkRequest.Loading -> {
                        submitBtn.enableLoading(true)
                    }

                    is NetworkRequest.Success -> {
                        submitBtn.enableLoading(false)
                        CustomToast.makeText(
                            requireContext(),
                            getString(R.string.success_address),
                            CustomToast.LONG_DURATION,
                            CustomToast.SUCCESS,
                            true
                        ).show()
                        response.data?.let {
                            lifecycleScope.launch { EventBus.publish(Events.IsUpdateAddress) }
                            findNavController().popBackStack()
                        }
                    }

                    is NetworkRequest.Error -> {
                        submitBtn.enableLoading(false)
                        CustomToast.makeText(
                            requireContext(),
                            response.error!!,
                            CustomToast.LONG_DURATION,
                            CustomToast.ERROR,
                            true
                        ).show()
                    }
                }
            }
        }
    }

    private fun updateSubmitButtonState() {
        binding.apply {
            val isNameNotEmpty = nameEdt.text.toString().isNotEmpty()
            val isFamilyNotEmpty = familyEdt.text.toString().isNotEmpty()
            val isPhoneNotEmpty = phoneEdt.text.toString().isNotEmpty()
            val isCityNotEmpty = cityEdt.text.toString().isNotEmpty()
            val isAddressNotEmpty = addressEdt.text.toString().isNotEmpty()
            val isStreetNotEmpty = streetEdt.text.toString().isNotEmpty()
            val isPostalNotEmpty = postalEdt.text.toString().isNotEmpty()
            submitBtn.isEnabled =
                isNameNotEmpty && isFamilyNotEmpty && isPhoneNotEmpty && isCityNotEmpty && isAddressNotEmpty && isStreetNotEmpty && isPostalNotEmpty
        }
    }

}