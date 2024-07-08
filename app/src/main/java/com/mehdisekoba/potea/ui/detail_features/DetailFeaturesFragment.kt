package com.mehdisekoba.potea.ui.detail_features

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.mehdisekoba.potea.R
import com.mehdisekoba.potea.databinding.FragmentDetailFeaturesBinding
import com.mehdisekoba.potea.utils.base.BaseFragment
import com.mehdisekoba.potea.utils.other.CustomToast
import com.mehdisekoba.potea.viewmodel.DetailsViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class DetailFeaturesFragment : BaseFragment<FragmentDetailFeaturesBinding>() {
    override val bindingInflater: (inflater: LayoutInflater) -> FragmentDetailFeaturesBinding
        get() = FragmentDetailFeaturesBinding::inflate

    @Inject
    lateinit var adapterFeature: FeaturesAdapter
    private val viewModel by activityViewModels<DetailsViewModel>()

    // Other
    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            if (isNetworkAvailable) {
                viewModel.property.observe(viewLifecycleOwner) { property ->
                    if (property.isNullOrEmpty().not()) {
                        emptyLay.isVisible = false
                        vRecyclerview.isVisible = true
                        adapterFeature.setData(property)
                        vRecyclerview.setAdapter(adapterFeature)
                        vRecyclerview.setLayoutManager(LinearLayoutManager(requireContext()))
                    } else {
                        vRecyclerview.isVisible = false
                        emptyLay.isVisible = true
                    }
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
        }
    }
}
