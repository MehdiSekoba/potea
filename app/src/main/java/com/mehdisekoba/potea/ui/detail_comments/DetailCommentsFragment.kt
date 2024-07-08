package com.mehdisekoba.potea.ui.detail_comments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.mehdisekoba.potea.R
import com.mehdisekoba.potea.data.model.detail.ResponseShowComment
import com.mehdisekoba.potea.databinding.FragmentDetailCommentsBinding
import com.mehdisekoba.potea.utils.base.BaseFragment
import com.mehdisekoba.potea.utils.network.NetworkRequest
import com.mehdisekoba.potea.utils.other.CustomToast
import com.mehdisekoba.potea.viewmodel.DetailsViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class DetailCommentsFragment : BaseFragment<FragmentDetailCommentsBinding>() {
    override val bindingInflater: (inflater: LayoutInflater) -> FragmentDetailCommentsBinding
        get() = FragmentDetailCommentsBinding::inflate

    @Inject
    lateinit var commentsAdapter: CommentsAdapter

    // other
    private val viewModel by activityViewModels<DetailsViewModel>()

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        if (isNetworkAvailable) {
            viewModel.productID.observe(viewLifecycleOwner) {
                viewModel.callShowCommentApi(it)
            }
        } else {
            CustomToast
                .makeText(
                    requireContext(),
                    getString(R.string.checkYourNetwork),
                    CustomToast.LONG_DURATION,
                    CustomToast.WARNING,
                ).show()
        }
        // Add new comment
        binding.addNewCommentTxt.setOnClickListener {
            findNavController().navigate(R.id.actionDetailToAddComment)
        }
        loadCommentData()
    }

    private fun loadCommentData() {
        binding.apply {
            viewModel.showComment.observe(viewLifecycleOwner) { response ->
                when (response) {
                    is NetworkRequest.Loading -> {
                        commentRecycler.veil()
                    }

                    is NetworkRequest.Success -> {
                        commentRecycler.unVeil()
                        response.data!!.let { data ->
                            if (data.size > 0) {
                                emptyLay.isVisible = false
                                initShowComment(data)
                            } else {
                                commentRecycler.isVisible = false
                                emptyLay.isVisible = true
                            }
                        }
                    }

                    is NetworkRequest.Error -> {
                        commentRecycler.unVeil()
                    }
                }
            }
        }
    }

    private fun initShowComment(data: ResponseShowComment) {
        binding.apply {
            commentsAdapter.setData(data)
             commentRecycler.apply {
                setVeilLayout(layout = R.layout.shimmer_comment,isPrepared = true)
                setLayoutManager(LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false))
                setAdapter(commentsAdapter)
            }
            // click
            commentsAdapter.setOnItemClickListener { item ->
                Toast.makeText(requireContext(), item.id.toString(), Toast.LENGTH_LONG).show()
            }
        }
    }
}
