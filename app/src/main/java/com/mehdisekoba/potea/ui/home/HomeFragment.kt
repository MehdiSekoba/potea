package com.mehdisekoba.potea.ui.home

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.animation.AnimationUtils
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import com.google.android.material.carousel.CarouselLayoutManager
import com.google.android.material.carousel.HeroCarouselStrategy
import com.mehdisekoba.potea.R
import com.mehdisekoba.potea.data.model.home.ResponseAmazing
import com.mehdisekoba.potea.data.model.home.ResponseBanner
import com.mehdisekoba.potea.data.model.home.ResponseProducts
import com.mehdisekoba.potea.data.model.home.ResponseProducts.ResponseProductsItem
import com.mehdisekoba.potea.databinding.FragmentHomeBinding
import com.mehdisekoba.potea.ui.home.adapters.AdapterBanner
import com.mehdisekoba.potea.ui.home.adapters.AdapterProduct
import com.mehdisekoba.potea.ui.home.adapters.AmazingAdapter
import com.mehdisekoba.potea.utils.BASE_URL_IMAGE_PROFILE
import com.mehdisekoba.potea.utils.DELAY_TIME
import com.mehdisekoba.potea.utils.GOOD_EVENING
import com.mehdisekoba.potea.utils.GOOD_MORNING
import com.mehdisekoba.potea.utils.GOOD_NIGHT
import com.mehdisekoba.potea.utils.REPEAT_TIME
import com.mehdisekoba.potea.utils.base.BaseFragment
import com.mehdisekoba.potea.utils.events.EventBus
import com.mehdisekoba.potea.utils.events.Events
import com.mehdisekoba.potea.utils.extensions.clear
import com.mehdisekoba.potea.utils.extensions.loadImage
import com.mehdisekoba.potea.utils.network.NetworkRequest
import com.mehdisekoba.potea.utils.other.CustomToast
import com.mehdisekoba.potea.viewmodel.HomeViewModel
import com.mehdisekoba.potea.viewmodel.ProfileViewModel
import com.skydoves.androidveil.VeilRecyclerFrameView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>() {
    override val bindingInflater: (inflater: LayoutInflater) -> FragmentHomeBinding
        get() = FragmentHomeBinding::inflate

    @Inject
    lateinit var banner: AdapterBanner

    @Inject
    lateinit var amazing: AmazingAdapter

    @Inject
    lateinit var newProductAdapter: AdapterProduct

    @Inject
    lateinit var popularAdapter: AdapterProduct

    // Other
    private val viewModel by activityViewModels<HomeViewModel>()
    private val profileViewModel by activityViewModels<ProfileViewModel>()
    private var autoScrollIndex = 0

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            // InitViews
            // Navigate to profile
            avatarImg.setOnClickListener { findNavController().navigate(R.id.actionHomeToProfile) }
            // Navigate to search
            searchImg.setOnClickListener { findNavController().navigate(R.id.actionToSearch) }
            // time
            txtTime.text = getTimeOfDay()
            avatarImg.load(R.drawable.placeholder)
            // check connection
            if (isNetworkAvailable) {
                loadBannerData()
                viewModel.callDiscountApi()
                loadAmazingData()
                viewModel.callNewProductApi()
                loadProductsData()
                profileViewModel.callProfileApi()
                loadProfileData()
            } else {
                CustomToast.makeText(requireContext(),getString(R.string.no_internet),CustomToast.LONG_DURATION,CustomToast.WARNING,true).show()
            }
        }
        if (isNetworkAvailable) {
            lifecycleScope.launch {
                EventBus.subscribe<Events.IsUpdateProfile> {
                    profileViewModel.callProfileApi()
                }
                EventBus.subscribe<Events.IsUpdatePhotoProfile> {
                    profileViewModel.callProfileApi()
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun loadProfileData() {
        binding.apply {
            profileViewModel.profileData.observe(viewLifecycleOwner) { response ->
                when (response) {
                    is NetworkRequest.Loading -> {
                        veilLayoutHeader.veil()
                    }

                    is NetworkRequest.Success -> {
                        veilLayoutHeader.unVeil()
                        response.data?.let { data ->
                            //Avatar
                            if (data.profilePhoto != null) {
                                val avatar = "$BASE_URL_IMAGE_PROFILE${data.profilePhoto}"
                                avatarImg.loadImage(avatar)
                                avatarBadgeImg.isVisible = false
                            } else {
                                avatarImg.clear()
                                avatarImg.load(R.drawable.placeholder)
                                avatarBadgeImg.animation = AnimationUtils.loadAnimation(
                                    requireContext(),
                                    R.anim.flash_leave_now
                                )

                                avatarBadgeImg.isVisible = true
                            }
                            //Name
                            if (data.name.isNullOrEmpty().not() && data.family.isNullOrEmpty()
                                    .not()
                            ) {
                                txtNameFamily.text = "${data.name} ${data.family}"
                            } else {
                                txtNameFamily.visibility = View.INVISIBLE
                            }
                        }
                    }

                    is NetworkRequest.Error -> {
                        veilLayoutHeader.unVeil()
                        CustomToast.makeText(
                            requireContext(),
                            response.error!!,
                            CustomToast.LONG_DURATION,
                            CustomToast.WARNING,
                            true
                        ).show()
                    }
                }
            }
        }
    }

    private fun loadBannerData() {
        binding.apply {
            viewModel.bannerData.observe(viewLifecycleOwner) { response ->
                when (response) {
                    is NetworkRequest.Loading -> {
                        bannerList.veil()
                        veilLayoutHeader.veil()
                        discountLay.veil()
                    }

                    is NetworkRequest.Success -> {
                        bannerList.unVeil()
                        veilLayoutHeader.unVeil()
                        response.data?.let { data ->
                            if (data.isNotEmpty()) {
                                initBannerRecycler(data)
                            }
                        }
                    }

                    is NetworkRequest.Error -> {
                        bannerList.unVeil()
                        veilLayoutHeader.unVeil()
                    }
                }
            }
        }
    }

    private fun initBannerRecycler(data: ResponseBanner) {
        banner.setData(data)
        autoScrollPopular(banner)
        binding.bannerList.apply{
        setVeilLayout(layout = R.layout.shimmer_banner, isPrepared = true)
        setAdapter(banner)
        setLayoutManager(
            CarouselLayoutManager(HeroCarouselStrategy()),)
        addVeiledItems(10)
        }
    }

    private fun autoScrollPopular(adapter: AdapterBanner) {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                repeat(REPEAT_TIME) {
                    delay(DELAY_TIME)
                    val itemCount = adapter.itemCount
                    if (autoScrollIndex < itemCount) {
                        autoScrollIndex += 1
                    } else {
                        autoScrollIndex = 0
                    }
                    binding.bannerList.getRecyclerView().smoothScrollToPosition(autoScrollIndex)
                }
            }
        }
    }

    private fun loadAmazingData() {
        binding.apply {
            viewModel.discountData.observe(viewLifecycleOwner) { response ->
                when (response) {
                    is NetworkRequest.Loading -> {
                        discountLay.veil()
                    }

                    is NetworkRequest.Success -> {
                        discountLay.unVeil()
                        response.data?.let { data ->
                            if (data.isNotEmpty()) {
                                initAmazingRecycler(data)
                            }
                        }
                    }

                    is NetworkRequest.Error -> {
                        discountLay.unVeil()
                        CustomToast.makeText(requireContext(),response.error!!,CustomToast.LONG_DURATION,CustomToast.WARNING,true).show()
                    }
                }
            }
        }
    }

    private fun initAmazingRecycler(data: ResponseAmazing) {
        binding.apply {
            amazing.setData(data)

            amazingList.apply {
                setVeilLayout(layout = R.layout.shimmer_banner, isPrepared = true)
                setAdapter(amazing)
                setLayoutManager(
                    LinearLayoutManager(
                        requireContext(),
                        LinearLayoutManager.HORIZONTAL,
                        false
                    )
                )
                addVeiledItems(10)
            }
            // click
            amazing.setonItemClickListener {
                val direction = HomeFragmentDirections.actionToDetail(it)
                findNavController().navigate(direction)
            }
        }
    }

    private fun handleProductsRequest(
        request: NetworkRequest<ResponseProducts>,
        recyclerView: VeilRecyclerFrameView,
        adapter: AdapterProduct,
    ) {
        binding.apply {
            viewModel.newProductData.observe(viewLifecycleOwner) { response ->
                when (response) {
                    is NetworkRequest.Loading -> {
                        recyclerView.veil()
                    }

                    is NetworkRequest.Success -> {
                        recyclerView.unVeil()
                        request.data?.let { data ->
                            if (data.isNotEmpty()) {
                                initProductsRecyclers(data, recyclerView, adapter)
                            }
                        }
                    }

                    is NetworkRequest.Error -> {
                        recyclerView.unVeil()
                        CustomToast.makeText(requireContext(),response.error!!,CustomToast.LONG_DURATION,CustomToast.WARNING,true).show()
                    }
                }
            }
        }
    }

    private fun loadProductsData() {
        binding.apply {
            // newProduct
            if (newProductLay.parent != null) {
                val newProduct = newProductLay.inflate()
                viewModel.newProductData.observe(viewLifecycleOwner) {
                    handleProductsRequest(
                        it,
                        newProduct.findViewById(R.id.newProduct_List),
                        newProductAdapter,
                    )
                }
            }
            // popular
            if (popularLay.parent != null) {
                val popular = popularLay.inflate()
                viewModel.popularProduct.observe(viewLifecycleOwner) {
                    handleProductsRequest(
                        it,
                        popular.findViewById(R.id.popular_List),
                        popularAdapter,
                    )
                }
            }
        }
    }

    private fun initProductsRecyclers(
        data: List<ResponseProductsItem>,
        recyclerView: VeilRecyclerFrameView,
        adapter: AdapterProduct,
    ) {
        adapter.setData(data)
        recyclerView.apply {
            setVeilLayout(layout = R.layout.shimmer_product, isPrepared = true)
            setAdapter(adapter)
            setLayoutManager(
                LinearLayoutManager(
                    requireContext(),
                    LinearLayoutManager.HORIZONTAL,
                    false
                )
            )
            addVeiledItems(10)
        }
        // Click
        adapter.setOnItemClickListener {
            val direction = HomeFragmentDirections.actionToDetail(it.id!!)
            findNavController().navigate(direction)
        }
    }

    private fun getTimeOfDay(): String {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        return when (hour) {
            in 6..11 -> "$GOOD_MORNING ${getEmojiByUnicode()}"
            in 12..19 -> "$GOOD_EVENING ${getEmojiByUnicode()}"
            in 19..23 -> "$GOOD_NIGHT ${getEmojiByUnicode()}"
            else -> "$GOOD_NIGHT ${getEmojiByUnicode()}"
        }
    }

    private fun getEmojiByUnicode(): String = String(Character.toChars(0x1f44b))
}
