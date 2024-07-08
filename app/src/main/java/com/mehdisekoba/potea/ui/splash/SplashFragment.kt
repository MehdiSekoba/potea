package com.mehdisekoba.potea.ui.splash

import android.animation.Animator
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import coil.load
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.mehdisekoba.potea.R
import com.mehdisekoba.potea.data.stored.SessionManager
import com.mehdisekoba.potea.databinding.DisconnectInternetBinding
import com.mehdisekoba.potea.databinding.FragmentSplashBinding
import com.mehdisekoba.potea.utils.base.BaseFragment
import com.mehdisekoba.potea.utils.extensions.openSettingDataMobile
import com.mehdisekoba.potea.utils.extensions.openSettingWifi
import com.mehdisekoba.potea.utils.network.MyReceiver
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
@Suppress("DEPRECATION")
class SplashFragment :
    BaseFragment<FragmentSplashBinding>(),
    MyReceiver.ConnectivityReceiverListener {
    override val bindingInflater: (inflater: LayoutInflater) -> FragmentSplashBinding
        get() = FragmentSplashBinding::inflate

    @Inject
    lateinit var sessionManager: SessionManager

    @Inject
    lateinit var myReceiver: MyReceiver

    // other
    private var dialog: AlertDialog? = null

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        requireContext().registerReceiver(
            myReceiver,
            IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION),
        )
    }

    // Shows the dialog for internet disconnection
    private fun showDialog() {
        if (dialog == null || !dialog!!.isShowing) {
            val layoutInflater = LayoutInflater.from(requireContext())
            val customDialogView = layoutInflater.inflate(R.layout.disconnect_internet, null)
            val bindingDialog = DisconnectInternetBinding.bind(customDialogView)
            dialog =
                MaterialAlertDialogBuilder(requireContext())
                    .setView(bindingDialog.root)
                    .setCancelable(false)
                    .create()

            bindingDialog.apply {
                turnData.setOnClickListener { requireContext().openSettingDataMobile() }
                turnWifi.setOnClickListener { requireContext().openSettingWifi() }
                imgClose.setOnClickListener { dialog?.dismiss() }
            }

            dialog?.show()
        }
    }

    // Dismisses the dialog
    private fun dismissDialog() {
        dialog?.dismiss()
        dialog = null
    }

    // Updates the connection UI based on the provided flag
    private fun updateViewConnection(isShown: Boolean) {
        binding.apply {
            txtLight.isVisible = isShown
            imgLogo.isVisible = isShown
            animationView.isVisible = isShown
        }
    }

    override fun onNetworkConnectionChanger(isConnected: Boolean) {
        binding.apply {
            if (isConnected) {
                dismissDialog()
                handleAnimation()
                updateViewConnection(true)
                // logo
                imgLogo.load(R.drawable.ic_logo)
                // animation
                txtLight.on()
                lifecycleScope.launch {
                    updateViewConnection(true)
                    delay(3000)
                    navigateToNextScreen()
                }
            } else {
                showDialog()
                updateViewConnection(false)
            }
        }
    }

    private fun handleAnimation() {
        binding.animationView.apply {
            addAnimatorListener(
                object : Animator.AnimatorListener {
                    override fun onAnimationStart(animation: Animator) {}

                    override fun onAnimationEnd(animation: Animator) {
                        lifecycleScope.launch {
                            delay(2000)
                            playAnimation()
                        }
                    }

                    override fun onAnimationCancel(animation: Animator) {}

                    override fun onAnimationRepeat(animation: Animator) {}
                },
            )
        }
    }
    private suspend fun navigateToNextScreen() {
        val firstTime = sessionManager.getOnboardingPreference.first()
        val token = sessionManager.getToken.first()
        findNavController().popBackStack(R.id.splashFragment, true)
        if (firstTime == false) {
            findNavController().navigate(R.id.action_splash_to_onboarding)
        } else {
            if (token == null) {
                findNavController().navigate(R.id.action_onboarding_to_login)
            } else {
                findNavController().navigate(R.id.action_verify_to_home)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        MyReceiver.connectivityReceiverListener = this
    }

    override fun onStop() {
        super.onStop()
        requireContext().unregisterReceiver(myReceiver)
    }
}
