package com.mehdisekoba.potea.ui.profile_edit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.github.ihermandev.formatwatcher.FormatWatcher
import com.mehdisekoba.potea.R
import com.mehdisekoba.potea.databinding.FragmentEditProfileBinding
import com.mehdisekoba.potea.utils.MASK
import com.mehdisekoba.potea.utils.PHONE_PATTERN
import com.mehdisekoba.potea.utils.base.BaseBottomSheetFragment
import com.mehdisekoba.potea.utils.events.EventBus
import com.mehdisekoba.potea.utils.events.Events
import com.mehdisekoba.potea.utils.extensions.enableLoading
import com.mehdisekoba.potea.utils.extensions.hideKeyboard
import com.mehdisekoba.potea.utils.extensions.onTextChange
import com.mehdisekoba.potea.utils.network.NetworkRequest
import com.mehdisekoba.potea.utils.other.CustomToast
import com.mehdisekoba.potea.viewmodel.ProfileViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class EditProfileFragment : BaseBottomSheetFragment<FragmentEditProfileBinding>() {
    override val bindingInflater: (inflater: LayoutInflater) -> FragmentEditProfileBinding
        get() = FragmentEditProfileBinding::inflate

    // Theme
    override fun getTheme() = R.style.RemoveDialogBackground

    // Other
    private val viewModel by viewModels<ProfileViewModel>()

    private var name: String? = null
    private var family: String? = null
    private var phoneNumber: String? = null

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            // dismiss
            closeImg.setOnClickListener { this@EditProfileFragment.dismiss() }
            val formatter = FormatWatcher(PHONE_PATTERN, MASK)
            phoneEdt.addTextChangedListener(formatter)
            // Submit data
            // Add the TextWatcher to each TextInputEditText
            nameEdt.onTextChange { updateSubmitButtonState() }
            familyEdt.onTextChange { updateSubmitButtonState() }
            phoneEdt.onTextChange { updateSubmitButtonState() }
            submitBtn.setOnClickListener {
                if (nameEdt.text.isNullOrEmpty().not()) {
                    name = nameEdt.text.toString()
                }
                if (familyEdt.text.isNullOrEmpty().not()) {
                    family = familyEdt.text.toString()
                }
                if (phoneEdt.text.isNullOrEmpty().not()) {
                    phoneNumber = phoneEdt.text.toString()
                }
                // call api
                if (isNetworkAvailable) {
                    viewModel.callUpdateProfile(name!!, family!!, formatter.rawInput)
                } else {
                    CustomToast
                        .makeText(
                            requireContext(),
                            getString(R.string.checkYourNetwork),
                            CustomToast.LONG_DURATION,
                            CustomToast.ERROR,
                            true,
                        ).show()
                }
            }
        }
        loadUpdateProfileData()
    }

    private fun loadUpdateProfileData() {
        binding.apply {
            viewModel.updateProfile.observe(viewLifecycleOwner) { response ->
                when (response) {
                    is NetworkRequest.Loading -> {
                        submitBtn.enableLoading(true)
                    }

                    is NetworkRequest.Success -> {
                        submitBtn.enableLoading(false)
                        root.hideKeyboard()
                        this@EditProfileFragment.dismiss()
                        lifecycleScope.launch {
                            EventBus.publish(Events.IsUpdateProfile)
                        }
                    }

                    is NetworkRequest.Error -> {
                        submitBtn.enableLoading(false)
                        CustomToast
                            .makeText(
                                requireContext(),
                                response.error!!,
                                CustomToast.LONG_DURATION,
                                CustomToast.ERROR,
                                true,
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
            submitBtn.isEnabled = isNameNotEmpty && isFamilyNotEmpty && isPhoneNotEmpty
        }
    }
}
