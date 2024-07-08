package com.mehdisekoba.potea.ui.detail

import android.graphics.Paint
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.button.MaterialButton
import com.google.android.material.tabs.TabLayout
import com.mehdisekoba.potea.R
import com.mehdisekoba.potea.data.model.detail.ResponseDetails
import com.mehdisekoba.potea.databinding.FragmentDetailBinding
import com.mehdisekoba.potea.ui.detail.adapters.ImagesAdapter
import com.mehdisekoba.potea.ui.detail.adapters.PagerAdapter
import com.mehdisekoba.potea.utils.ERROR_TIME
import com.mehdisekoba.potea.utils.PATTERN_TIME
import com.mehdisekoba.potea.utils.PRODUCT_ID
import com.mehdisekoba.potea.utils.RESPONSE_TRUE
import com.mehdisekoba.potea.utils.base.BaseFragment
import com.mehdisekoba.potea.utils.events.EventBus
import com.mehdisekoba.potea.utils.events.Events
import com.mehdisekoba.potea.utils.extensions.clear
import com.mehdisekoba.potea.utils.extensions.enableLoading
import com.mehdisekoba.potea.utils.extensions.formatToCurrency
import com.mehdisekoba.potea.utils.extensions.loadImage
import com.mehdisekoba.potea.utils.extensions.setTint
import com.mehdisekoba.potea.utils.network.NetworkRequest
import com.mehdisekoba.potea.utils.other.CustomToast
import com.mehdisekoba.potea.viewmodel.CartViewModel
import com.mehdisekoba.potea.viewmodel.DetailsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@AndroidEntryPoint
class DetailFragment : BaseFragment<FragmentDetailBinding>() {
    override val bindingInflater: (inflater: LayoutInflater) -> FragmentDetailBinding
        get() = FragmentDetailBinding::inflate

    @Inject
    lateinit var imagesAdapter: ImagesAdapter

    @Inject
    lateinit var pagerAdapter: PagerAdapter

    // other
    private var productId = 0
    private val viewModel by activityViewModels<DetailsViewModel>()
    private val viewModelCart by viewModels<CartViewModel>()

    private val args by navArgs<DetailFragmentArgs>()
    private lateinit var countDownTimer: CountDownTimer
    private var addedToCart = 0

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        // Args
        args.let {
            productId = it.id
            viewModel.setProductId(productId)
        }
        //back
        binding.detailHeaderLay.imgBack.setOnClickListener {
            findNavController().popBackStack()
        }
        // Call api
        if (productId > 0) {
            if (isNetworkAvailable) {
                viewModel.callDetailApi(productId)
            }
        }

        // Load data
        loadDetailData()
        loadAddToCartData()
        loadLikeData()
    }

    private fun initDetailViews(data: ResponseDetails) {
        PRODUCT_ID = data.id!!
        initDetailHeaderView(data)
        initDetailTimerView(data)
        initDetailBottomView(data)
        setupViewPager()
    }

    private fun initDetailHeaderView(data: ResponseDetails) {
        // Header
        binding.detailHeaderLay.apply {
            // title
            productTitle.text = data.name
            // rate
            if (data.score!! > 0.0) {
                txtRate.isVisible = true
                txtRate.text = data.score.toString()
            } else {
                txtRate.isVisible = false
            }
            // Desc
            if (data.description.isNullOrEmpty().not()) {
                val info =
                    HtmlCompat.fromHtml(
                        data.description.toString(),
                        HtmlCompat.FROM_HTML_MODE_COMPACT,
                    )
                productInfo.text = info
            } else {
                productInfo.isVisible = false
            }
            // Images
            initImagesRecycler(data.images!!)
            // poster
            singleImage.loadImage(data.images[0].image!!)
            //Favorite
            updateFavUI(data.status!!)
            imgFavorite.setOnClickListener {
                if (isNetworkAvailable) {
                    viewModel.callProductLike(data.id!!)
                }
            }
        }
        // setData
        viewModel.setProperty(data.properties ?: emptyList())

    }

    private fun loadDetailData() {
        binding.apply {
            viewModel.detailData.observe(viewLifecycleOwner) { response ->
                when (response) {
                    is NetworkRequest.Loading -> {
                        detailVeilLayoutBody.veil()
                        veilLayoutCart.veil()
                    }

                    is NetworkRequest.Success -> {
                        detailVeilLayoutBody.unVeil()
                        veilLayoutCart.unVeil()

                        response.data?.let { data ->
                            initDetailViews(data)
                        }
                    }

                    is NetworkRequest.Error -> {
                        detailVeilLayoutBody.unVeil()
                        veilLayoutCart.unVeil()
                        CustomToast.makeText(requireContext(),response.error!!,CustomToast.LONG_DURATION,CustomToast.ERROR,true).show()
                    }
                }
            }
        }
    }

    private fun initImagesRecycler(data: List<ResponseDetails.Image>) {
        imagesAdapter.setData(data)
        binding.detailHeaderLay.apply {
            imageRecyclerView.apply {
                setLayoutManager(LinearLayoutManager(requireContext()))
                setAdapter(imagesAdapter)
            }


            // Click
            imagesAdapter.setOnItemClickListener {
                singleImage.clear()
                singleImage.loadImage(it.image!!)
            }
        }
    }

    private fun initDetailBottomView(data: ResponseDetails) {
        addedToCart = data.inBasket!!
        binding.detailBottom.apply {
            // Exist in cart
            if (data.inBasket == 1) {
                updateAddToCartUI(addToCartBtn)
            }

            // Price
            finalPriceTxt.text = data.price?.toDouble()!!.formatToCurrency()
            // Click
            addToCartBtn.setOnClickListener {
                viewModelCart.callAddTOCartApi(productId)
            }
        }
    }

    private fun setupViewPager() {
        binding.detailPagerLay.apply {
            detailTabLayout.addTab(detailTabLayout.newTab().setText(getString(R.string.features)))
            detailTabLayout.addTab(
                detailTabLayout.newTab().setText(getString(R.string.price_Chart)),
            )
            detailTabLayout.addTab(detailTabLayout.newTab().setText(getString(R.string.comments)))
            // View pager adapter
            detailViewPager.adapter = pagerAdapter
            // Select
            detailTabLayout.addOnTabSelectedListener(
                object : TabLayout.OnTabSelectedListener {
                    override fun onTabSelected(tab: TabLayout.Tab?) {
                        if (tab != null) detailViewPager.currentItem = tab.position
                    }

                    override fun onTabUnselected(tab: TabLayout.Tab?) {
                    }

                    override fun onTabReselected(tab: TabLayout.Tab?) {}
                },
            )
            // View pager
            detailViewPager.registerOnPageChangeCallback(
                object : ViewPager2.OnPageChangeCallback() {
                    override fun onPageSelected(position: Int) {
                        super.onPageSelected(position)
                        detailTabLayout.selectTab(detailTabLayout.getTabAt(position))
                    }
                },
            )
            // Disable swipe
            detailViewPager.isUserInputEnabled = false
        }
    }

    private fun loadLikeData() {
        binding.detailHeaderLay.apply {
            viewModel.likeData.observe(viewLifecycleOwner) { response ->
                when (response) {
                    is NetworkRequest.Loading -> {}
                    is NetworkRequest.Success -> {
                        response.data?.let { data ->
                            updateFavUI(data.status!!)
                        }
                    }

                    is NetworkRequest.Error -> {
                        CustomToast.makeText(
                            requireContext(),
                            response.error!!,
                            CustomToast.LONG_DURATION,
                            CustomToast.ERROR,
                            true
                        ).show()
                    }
                }
            }
        }
    }

    private fun initDetailTimerView(data: ResponseDetails) {
        binding.detailTimerLay.apply {
            if ((data.offPrice?.toInt() ?: 0) > 0) {
                data.amazingTime?.let {
                    priceDiscountLay.isVisible = true
                    discountTimer(it.toString())
                    countDownTimer.start()
                } ?: run {
                    priceDiscountLay.visibility = View.GONE
                }

                // Discount
                binding.detailBottom.oldPriceTxt.apply {
                    text =
                        data.price
                            .toString()
                            .toDouble()
                            .formatToCurrency()
                    paintFlags = this.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                }
            } else {
                priceDiscountLay.isVisible = false
            }
        }
    }

    private fun discountTimer(fullDate: String) {
        try {
            val date =
                when {
                    fullDate.matches("\\d+".toRegex()) -> Date(fullDate.toLong())
                    else ->
                        SimpleDateFormat(PATTERN_TIME, Locale.ENGLISH).parse(fullDate)
                            ?: throw ParseException(ERROR_TIME, 0)
                }

            val currentMillis = System.currentTimeMillis()
            val finalMillis = date.time - currentMillis

            countDownTimer =
                object : CountDownTimer(finalMillis, 1_000) {
                    override fun onTick(millisUntilFinished: Long) {
                        val days = TimeUnit.MILLISECONDS.toDays(millisUntilFinished)
                        val hours =
                            TimeUnit.MILLISECONDS.toHours(
                                millisUntilFinished % TimeUnit.DAYS.toMillis(1),
                            )
                        val minutes =
                            TimeUnit.MILLISECONDS.toMinutes(
                                millisUntilFinished % TimeUnit.HOURS.toMillis(1),
                            )
                        val seconds =
                            TimeUnit.MILLISECONDS.toSeconds(
                                millisUntilFinished % TimeUnit.MINUTES.toMillis(1),
                            )

                        try {
                            binding.detailTimerLay.timerLay.apply {
                                dayLay.isVisible = days > 0
                                dayTxt.text = if (days > 0) days.toString() else ""
                                hourTxt.text = hours.toString()
                                minuteTxt.text = minutes.toString()
                                secondTxt.text = seconds.toString()
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }

                    override fun onFinish() {
                        // Handle timer finish if needed
                    }
                }
        } catch (e: ParseException) {
            e.printStackTrace()
        }
    }


    private fun loadAddToCartData() {
        binding.detailBottom.apply {
            viewModelCart.addToCartData.observe(viewLifecycleOwner) { response ->
                when (response) {
                    is NetworkRequest.Loading -> {
                        addToCartBtn.enableLoading(true)
                    }

                    is NetworkRequest.Success -> {
                        addToCartBtn.enableLoading(false)
                        response.data?.let { data ->
                            CustomToast
                                .makeText(
                                    requireContext(),
                                    data.message!!,
                                    CustomToast.LONG_DURATION,
                                    CustomToast.SUCCESS,
                                    true,
                                ).show()
                            addedToCart = 1
                            updateAddToCartUI(addToCartBtn)
                            lifecycleScope.launch {
                                EventBus.publish(Events.IsUpdateCart)
                            }
                        }
                    }

                    is NetworkRequest.Error -> {
                        addToCartBtn.enableLoading(false)
                        CustomToast.makeText(
                            requireContext(),
                            response.error!!,
                            CustomToast.LONG_DURATION,
                            CustomToast.ERROR,
                            true
                        ).show()
                    }
                }
            }
        }
    }

    private fun updateAddToCartUI(btn: MaterialButton) {
        btn.apply {
            setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.Blue))
            icon = ContextCompat.getDrawable(requireContext(), R.drawable.check_basket)
            text = getString(R.string.existsInCart)
        }
    }

    private fun updateFavUI(fav: String) {
        binding.detailHeaderLay.imgFavorite.apply {
            if (fav == RESPONSE_TRUE) {
                setImageResource(R.drawable.favorite)
                setTint(R.color.UFO_Green)
            } else {
                setImageResource(R.drawable.un_favorite)
                setTint(R.color.UFO_Green)

            }
        }
    }
}
