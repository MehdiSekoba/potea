package com.mehdisekoba.potea.ui.detail_comments_add

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.fragment.app.viewModels
import com.mehdisekoba.potea.R
import com.mehdisekoba.potea.databinding.FragmentAddCommentBinding
import com.mehdisekoba.potea.utils.PRODUCT_ID
import com.mehdisekoba.potea.utils.base.BaseBottomSheetFragment
import com.mehdisekoba.potea.utils.extensions.enableLoading
import com.mehdisekoba.potea.utils.extensions.hideKeyboard
import com.mehdisekoba.potea.utils.extensions.onTextChange
import com.mehdisekoba.potea.utils.network.NetworkRequest
import com.mehdisekoba.potea.utils.other.CustomToast
import com.mehdisekoba.potea.viewmodel.DetailsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddCommentFragment : BaseBottomSheetFragment<FragmentAddCommentBinding>() {
    override val bindingInflater: (inflater: LayoutInflater) -> FragmentAddCommentBinding
        get() = FragmentAddCommentBinding::inflate

    // Other
    private val viewModel by viewModels<DetailsViewModel>()

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        // InitViews
        binding.apply {
            // Close
            closeImg.setOnClickListener { this@AddCommentFragment.dismiss() }
            commentTitleEdt.onTextChange {updateSubmitButtonState() }
            commentEdt.onTextChange { updateSubmitButtonState()}
            submitBtn.isEnabled =
                commentTitleTxt.text.isNullOrEmpty().not() &&
                        commentEdt.text.isNullOrEmpty().not()
            // Submit
            submitBtn.setOnClickListener {
                val rate = rateSlider.value.toInt().toString()
                if (isNetworkAvailable) {
                    viewModel.callAddNewComment(
                        PRODUCT_ID,
                        commentEdt.text.toString(),
                        commentTitleEdt.text.toString(),
                        rate.toInt(),
                    )
                    root.hideKeyboard()
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
        // Load data
        loadSendCommentData()
    }

    private fun loadSendCommentData() {
        binding.apply {
            viewModel.sendCommentsData.observe(viewLifecycleOwner) { response ->
                when (response) {
                    is NetworkRequest.Loading -> {
                        submitBtn.enableLoading(true)
                    }

                    is NetworkRequest.Success -> {
                        submitBtn.enableLoading(false)
                        response.data?.let { data ->
                            CustomToast
                                .makeText(
                                    requireContext(),
                                    data.message!!,
                                    CustomToast.LONG_DURATION,
                                    CustomToast.SUCCESS,
                                    true,
                                ).show()
                            this@AddCommentFragment.dismiss()
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
                            ).show()
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.sendCommentsData.removeObservers(viewLifecycleOwner)
    }
    private fun updateSubmitButtonState() {
        binding.apply {
            val isTitleNotEmpty = commentTitleEdt.text.toString().isNotEmpty()
            val isCommentNotEmpty = commentEdt.text.toString().isNotEmpty()
            submitBtn.isEnabled =
                isTitleNotEmpty && isCommentNotEmpty
        }
    }

}
