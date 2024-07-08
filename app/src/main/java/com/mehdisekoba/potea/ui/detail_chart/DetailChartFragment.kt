package com.mehdisekoba.potea.ui.detail_chart

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartAnimationType
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartModel
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartType
import com.github.aachartmodel.aainfographics.aachartcreator.AASeriesElement
import com.github.aachartmodel.aainfographics.aachartcreator.aa_toAAOptions
import com.github.aachartmodel.aainfographics.aaoptionsmodel.AADateTimeLabelFormats
import com.github.aachartmodel.aainfographics.aaoptionsmodel.AAStyle
import com.github.aachartmodel.aainfographics.aaoptionsmodel.AATooltip
import com.github.aachartmodel.aainfographics.aatools.AAColor
import com.mehdisekoba.potea.data.model.detail.ResponsePriceHistory
import com.mehdisekoba.potea.databinding.FragmentDetailChartBinding
import com.mehdisekoba.potea.utils.PRICE
import com.mehdisekoba.potea.utils.base.BaseFragment
import com.mehdisekoba.potea.utils.network.NetworkRequest
import com.mehdisekoba.potea.utils.other.CustomToast
import com.mehdisekoba.potea.viewmodel.DetailsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailChartFragment : BaseFragment<FragmentDetailChartBinding>() {
    override val bindingInflater: (inflater: LayoutInflater) -> FragmentDetailChartBinding
        get() = FragmentDetailChartBinding::inflate

    // other
    private val viewModel by activityViewModels<DetailsViewModel>()
    private val daysList = arrayListOf<String>()
    private val priceList = arrayListOf<Double>()

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        // Get id
        viewModel.productID.observe(viewLifecycleOwner) {
            if (isNetworkAvailable) {
                viewModel.callPriceHistoryApi(it)
            }
        }
        // Load data
        loadPriceChartData()
    }

    private fun loadPriceChartData() {
        binding.apply {
            viewModel.priceHistory.observe(viewLifecycleOwner) { response ->
                when (response) {
                    is NetworkRequest.Loading -> {
                        veilLayoutPrice.veil()
                    }

                    is NetworkRequest.Success -> {
                        veilLayoutPrice.unVeil()
                        response.data?.let { data ->
                            if (data.isNullOrEmpty().not()) {
                                initLineChartData(data)
                                emptyLay.isVisible = false
                            } else {
                                emptyLay.isVisible = true
                            }
                        }
                    }

                    is NetworkRequest.Error -> {
                        veilLayoutPrice.unVeil()
                        CustomToast
                            .makeText(
                                requireContext(),
                                response.error.toString(),
                                CustomToast.LONG_DURATION,
                                CustomToast.ERROR,
                            ).show()
                    }
                }
            }
        }
    }

    private fun initLineChartData(data: ResponsePriceHistory) {
        daysList.clear()
        priceList.clear()

        data.forEach { item ->
            daysList.add(item.date!!.dropLast(3))

            priceList.add(item.price!!)
        }

        binding.apply {
            val aaChartModel =
                AAChartModel()
                    .chartType(AAChartType.Line)
                    .dataLabelsEnabled(false)
                    .legendEnabled(false)
                    .yAxisLabelsEnabled(true)
                    .xAxisLabelsEnabled(true)
                    .animationType(AAChartAnimationType.EaseInOutSine)
                    .animationDuration(1000)
                    .yAxisTitle(PRICE)
                    .categories(daysList.toTypedArray())
                    .series(
                        arrayOf(
                            AASeriesElement()
                                .name(PRICE)
                                .lineWidth(2)
                                .fillOpacity(0.4f)
                                .data(priceList.toTypedArray()),
                        ),
                    ).yAxisGridLineWidth(0)

            val aaOptions = aaChartModel.aa_toAAOptions()
            aaOptions.tooltip =
                AATooltip()
                    .padding(10)
                    .dateTimeLabelFormats(AADateTimeLabelFormats())
                    .shadow(true)
                    .shadow(true)
                    .style(
                        AAStyle.Companion.style(
                            AAColor.Green,
                            `in`.nouri.dynamicsizeslib.R.dimen._12font_mdp,
                        ),
                    )

            aaChartView.aa_drawChartWithChartModel(aaChartModel)
        }
    }
}
