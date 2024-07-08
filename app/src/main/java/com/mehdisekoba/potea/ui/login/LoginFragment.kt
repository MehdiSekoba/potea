package com.mehdisekoba.potea.ui.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.content.res.AppCompatResources
import com.mehdisekoba.potea.R
import com.mehdisekoba.potea.databinding.FragmentLoginBinding
import com.mehdisekoba.potea.utils.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : BaseFragment<FragmentLoginBinding>() {
    override val bindingInflater: (inflater: LayoutInflater) -> FragmentLoginBinding
        get() = FragmentLoginBinding::inflate


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //initViews
        binding.apply {
            val smallIcon = AppCompatResources.getDrawable(requireContext(), R.drawable.ic_logo)
            smallIcon!!.setBounds(0, 0, 48, 48)
            txtLogo.setCompoundDrawablesRelative(smallIcon, null, null, null)
        }
    }
}