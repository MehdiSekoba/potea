package com.mehdisekoba.potea.ui.login

import android.Manifest
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.mehdisekoba.potea.R
import com.mehdisekoba.potea.databinding.FragmentRegisterEmailBinding
import com.mehdisekoba.potea.utils.IS_CALLED_VERIFY
import com.mehdisekoba.potea.utils.SUCCESS
import com.mehdisekoba.potea.utils.base.BaseFragment
import com.mehdisekoba.potea.utils.extensions.enableLoading
import com.mehdisekoba.potea.utils.extensions.getClickableSpan
import com.mehdisekoba.potea.utils.extensions.hideKeyboard
import com.mehdisekoba.potea.utils.extensions.isValidEmail
import com.mehdisekoba.potea.utils.extensions.onTextChange
import com.mehdisekoba.potea.utils.network.NetworkRequest
import com.mehdisekoba.potea.utils.other.CustomToast
import com.mehdisekoba.potea.utils.other.FontSpan
import com.mehdisekoba.potea.viewmodel.LoginViewModel
import com.permissionx.guolindev.PermissionX
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterEmailFragment : BaseFragment<FragmentRegisterEmailBinding>() {
    override val bindingInflater: (inflater: LayoutInflater) -> FragmentRegisterEmailBinding
        get() = FragmentRegisterEmailBinding::inflate

    // other
    private val viewModel by viewModels<LoginViewModel>()
    private var email = ""

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        // initViews
        binding.apply {
            txtLight.on()
            // Email
            emailEdt.onTextChange {
                sendEmailBtn.isEnabled = emailEdt.text.toString().isValidEmail()
                if (it.isValidEmail()) {
                    email = it
                    emailInpLay.error = ""
                } else {
                    emailInpLay.error = getString(R.string.emailNotValid)
                }
                if (it.isEmpty()) emailInpLay.error = ""
            }
            // Send
            sendEmailBtn.setOnClickListener {
                getPermission()
                root.hideKeyboard()
                if (isNetworkAvailable) {
                    IS_CALLED_VERIFY = true
                    viewModel.callRegisterApi(email)
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
        // load data
        loadLoginData()
        // navigate
        navigateToLogin()
    }

    private fun loadLoginData() {
        binding.apply {
            viewModel.registerData.observe(viewLifecycleOwner) { response ->
                when (response) {
                    is NetworkRequest.Loading -> {
                        sendEmailBtn.enableLoading(true)
                    }

                    is NetworkRequest.Success -> {
                        sendEmailBtn.enableLoading(false)
                        response.data?.let { data ->
                            CustomToast.makeText(requireContext(),data.message!!,CustomToast.LONG_DURATION,CustomToast.SUCCESS,true).show()
                            if (data.message == SUCCESS) {
                                if (IS_CALLED_VERIFY) {
                                    val direction =
                                        RegisterEmailFragmentDirections.actionEmailToVerify(data)
                                    findNavController().navigate(direction)
                                }
                            }
                        }
                    }

                    is NetworkRequest.Error -> {
                        sendEmailBtn.enableLoading(false)
                        CustomToast.makeText(
                            requireContext(),
                            response.error!!,
                            CustomToast.LONG_DURATION,
                            CustomToast.WARNING,
                            true
                        ).show()
                    }
                }
            }
        }
    }

    private fun getPermission() {
        // Permission notification
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            PermissionX
                .init(this)
                .permissions(Manifest.permission.POST_NOTIFICATIONS)
                .request { allGranted, _, deniedList ->
                    if (allGranted) {
                        // All permissions are granted
                    } else {
                        CustomToast.makeText(
                            requireContext(),
                            getString(R.string.deniedPermission),
                            CustomToast.LONG_DURATION,
                            CustomToast.WARNING,
                            true
                        ).show()
                    }
                }
        }
    }


    private fun navigateToLogin() {
        binding.apply {
            val spanAction: (view: View) -> Unit = {
                val action = RegisterEmailFragmentDirections.actionRegisterToLogin()
                findNavController().navigate(action)
            }
            val clickableSpan =
                getClickableSpan(
                    ContextCompat.getColor(requireContext(), R.color.UFO_Green),
                    spanAction,
                )
            val spannableString = SpannableString(getString(R.string.login_account))
            spannableString.setSpan(
                clickableSpan,
                25,
                spannableString.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE,
            )

            spannableString.setSpan(
                FontSpan(ResourcesCompat.getFont(requireContext(), R.font.urbanist_bold)),
                25,
                spannableString.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE,
            )
            txtLogin.text = spannableString
            txtLogin.movementMethod = LinkMovementMethod.getInstance()
            txtLogin.highlightColor = Color.TRANSPARENT
        }
    }
}
