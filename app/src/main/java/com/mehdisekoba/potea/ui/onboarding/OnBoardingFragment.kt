package com.mehdisekoba.potea.ui.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.mehdisekoba.potea.R
import com.mehdisekoba.potea.data.model.onboarding.dataLocal
import com.mehdisekoba.potea.data.stored.SessionManager
import com.mehdisekoba.potea.databinding.FragmentOnBoardingBinding
import com.mehdisekoba.potea.utils.GET_STARTED
import com.mehdisekoba.potea.utils.NEXT
import com.mehdisekoba.potea.utils.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class OnBoardingFragment : BaseFragment<FragmentOnBoardingBinding>() {
    override val bindingInflater: (inflater: LayoutInflater) -> FragmentOnBoardingBinding
        get() = FragmentOnBoardingBinding::inflate

    @Inject
    lateinit var adapterOnBoarding: AdapterOnBoarding

    @Inject
    lateinit var sessionManager: SessionManager
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //initViews
        binding.apply {
            adapterOnBoarding.setData(dataLocal)
            onboardingViewPager.adapter = adapterOnBoarding
            dotsIndicator.attachTo(onboardingViewPager)
            onboardingViewPager.registerOnPageChangeCallback(
                object :
                    ViewPager2.OnPageChangeCallback() {
                    override fun onPageSelected(position: Int) {
                        super.onPageSelected(position)
                        onboardingViewPager.currentItem = position
                        // Update the button text based on the current position
                        when (position) {
                            in 0..2 -> {
                                btnStart.text = NEXT
                            }

                            3 -> {
                                btnStart.text = GET_STARTED
                            }
                        }
                    }
                },
            )
            // Handle the button click event
            btnStart.setOnClickListener {
                if (onboardingViewPager.currentItem + 1 < adapterOnBoarding.itemCount) {
                    onboardingViewPager.currentItem += 1
                } else {
                    navigateToHome()
                }
            }
        }
    }

    private fun navigateToHome() {
        lifecycleScope.launch {
            sessionManager.saveOnboardingPreference(true)
            findNavController().popBackStack(R.id.onBoardingFragment, true)
            findNavController().navigate(R.id.action_onboarding_to_login)
        }
    }
}
