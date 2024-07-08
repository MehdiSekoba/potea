package com.mehdisekoba.potea.ui.login

import android.animation.Animator
import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Spannable
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.goodiebag.pinview.Pinview
import com.mehdisekoba.potea.R
import com.mehdisekoba.potea.data.stored.SessionManager
import com.mehdisekoba.potea.databinding.FragmentLoginVerifyBinding
import com.mehdisekoba.potea.utils.IS_CALLED_VERIFY
import com.mehdisekoba.potea.utils.TIME_ONE
import com.mehdisekoba.potea.utils.TIME_SIXTY
import com.mehdisekoba.potea.utils.TIME_TEN
import com.mehdisekoba.potea.utils.TIME_ZERO
import com.mehdisekoba.potea.utils.base.BaseFragment
import com.mehdisekoba.potea.utils.extensions.getClickableSpan
import com.mehdisekoba.potea.utils.extensions.hideKeyboard
import com.mehdisekoba.potea.utils.other.CustomToast
import com.mehdisekoba.potea.utils.other.FontSpan
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class LoginVerifyFragment : BaseFragment<FragmentLoginVerifyBinding>() {
    override val bindingInflater: (inflater: LayoutInflater) -> FragmentLoginVerifyBinding
        get() = FragmentLoginVerifyBinding::inflate

    @Inject
    lateinit var sessionManager: SessionManager

    // other
    private val args by navArgs<LoginVerifyFragmentArgs>()
    private var otpCode = ""
    private var email = ""
    private var token = ""
    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        IS_CALLED_VERIFY = false
        // Args
        args.data.let {
            otpCode = it.code.toString()
            email = it.email.toString()
            token = it.token.toString()
        }
        binding.apply {
            val spanAction: (view: View) -> Unit = {
            }
            val clickableSpan =
                getClickableSpan(
                    ContextCompat.getColor(requireContext(), R.color.UFO_Green),
                    spanAction,
                )
            val spannableString = SpannableString("${getString(R.string.otp_send)}  $email")
            spannableString.setSpan(
                clickableSpan,
                24,
                spannableString.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE,
            )

            spannableString.setSpan(
                FontSpan(ResourcesCompat.getFont(requireContext(), R.font.urbanist_bold)),
                24,
                spannableString.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE,
            )
            txtMail.text = spannableString
            txtMail.movementMethod = LinkMovementMethod.getInstance()
            txtMail.highlightColor = Color.TRANSPARENT
            // Customize pin view text color
            pinView.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            // Complete code
            pinView.setPinViewEventListener(
                object : Pinview.PinViewEventListener {
                    override fun onDataEntered(
                        pinview: Pinview?,
                        fromUser: Boolean,
                    ) {
                        val enteredOtp = pinview?.value ?: ""
                        if (isNetworkAvailable) {
                            if (enteredOtp == otpCode) {
                                lifecycleScope.launch {
                                    sessionManager.saveToken(token)
                                }
                                root.hideKeyboard()
                                findNavController().popBackStack(R.id.loginVerifyFragment, true)
                                findNavController().popBackStack(R.id.registerEmailFragment, true)
                                // Home
                                findNavController().navigate(R.id.action_verify_to_home)
                            } else {
                                CustomToast.makeText(requireContext(),getString(R.string.not_match_code),CustomToast.LONG_DURATION,CustomToast.ERROR,true).show()
                            }
                        }
                    }
                },
            )
        }
        // Start timer
        lifecycleScope.launch {
            delay(500)
            handleTimer().start()
        }
        // Load data
        handleAnimation()
    }

    private fun handleTimer(): CountDownTimer {
        binding.apply {
            return object : CountDownTimer(TIME_SIXTY, TIME_ONE) {
                @SuppressLint("SetTextI18n")
                override fun onTick(millis: Long) {
                    sendAgainBtn.isVisible = false
                    timerTxt.isVisible = true
                    timerProgress.isVisible = true
                    timerTxt.text = "${millis / TIME_ONE} ${getString(R.string.second)}"
                    timerProgress.setProgressCompat((millis / TIME_ONE).toInt(), true)
                    if (millis < TIME_TEN) timerProgress.setIndicatorColor(ContextCompat.getColor(requireContext(),R.color.Carnelian))
                    if (millis < TIME_ONE) {
                        timerProgress.progress = TIME_ZERO
                    }
                }

                override fun onFinish() {
                    sendAgainBtn.isVisible = true
                    timerTxt.isVisible = false
                    timerProgress.isVisible = false
                    timerProgress.progress = 0
                }
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

    override fun onStop() {
        super.onStop()
        handleTimer().cancel()
    }
}
