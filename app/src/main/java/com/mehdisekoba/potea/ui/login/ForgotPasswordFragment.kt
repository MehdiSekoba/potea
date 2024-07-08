package com.mehdisekoba.potea.ui.login

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mehdisekoba.potea.R
import com.mehdisekoba.potea.databinding.FragmentForgotPasswordBinding
import com.mehdisekoba.potea.databinding.FragmentLoginVerifyBinding
import com.mehdisekoba.potea.utils.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ForgotPasswordFragment : BaseFragment<FragmentForgotPasswordBinding>() {
    override val bindingInflater: (inflater: LayoutInflater) -> FragmentForgotPasswordBinding
        get() = FragmentForgotPasswordBinding::inflate

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {

        }
    }
}