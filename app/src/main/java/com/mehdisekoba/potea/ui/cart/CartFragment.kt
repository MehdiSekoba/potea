package com.mehdisekoba.potea.ui.cart

import android.graphics.Paint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.mehdisekoba.potea.R
import com.mehdisekoba.potea.data.model.cart.ResponseCartList
import com.mehdisekoba.potea.databinding.FragmentCartBinding
import com.mehdisekoba.potea.utils.DELETE
import com.mehdisekoba.potea.utils.MINUS
import com.mehdisekoba.potea.utils.PLUS
import com.mehdisekoba.potea.utils.base.BaseFragment
import com.mehdisekoba.potea.utils.events.EventBus
import com.mehdisekoba.potea.utils.events.Events
import com.mehdisekoba.potea.utils.extensions.formatToCurrency
import com.mehdisekoba.potea.utils.network.NetworkRequest
import com.mehdisekoba.potea.utils.other.CustomToast
import com.mehdisekoba.potea.viewmodel.CartViewModel
import com.mehdisekoba.potea.viewmodel.ProfileViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class CartFragment : BaseFragment<FragmentCartBinding>() {
    override val bindingInflater: (inflater: LayoutInflater) -> FragmentCartBinding
        get() = FragmentCartBinding::inflate

    @Inject
    lateinit var cartListAdapter: CartListAdapter

    private val viewModel by viewModels<CartViewModel>()
    private val profileViewModel by viewModels<ProfileViewModel>()

    private var totalPrice = 0.0
    private var totalDelivery = 0.0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Call API
        if (isNetworkAvailable) {
            viewModel.callCartListApi()
        } else {
            CustomToast.makeText(
                requireContext(),
                getString(R.string.checkYourNetwork),
                CustomToast.LONG_DURATION,
                CustomToast.ERROR,
                true
            ).show()
        }
        //load data
        loadCartList()
        loadUserInfo()
    }

    private fun loadCartList() {
        binding.apply {
            viewModel.cartListData.observe(viewLifecycleOwner) { response ->
                when (response) {
                    is NetworkRequest.Loading -> {
                        veilLayoutCart.veil()
                    }

                    is NetworkRequest.Success -> {
                        veilLayoutCart.unVeil()
                        response.data?.let { data ->
                            val productItems = data.productItem ?: emptyList()
                            if (productItems.isEmpty().not()) {
                                txtCount.text = productItems[0].count.toString()
                                //fil data
                                deliverPrice.apply {
                                    text = (productItems[0].deliveryAmount.toString().toDouble()
                                        .formatToCurrency())
                                    paintFlags = this.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                                    setTextColor(ContextCompat.getColor(context, R.color.Carnelian))
                                }
                                initRecyclerView(productItems)
                                updateTotalPrice(productItems)

                                //check not empty username
                                makePayment.setOnClickListener {
                                    //call api
                                    if (isNetworkAvailable)
                                        profileViewModel.callProfileApi()
                                }
                            } else {
                                showEmptyLayout()
                            }
                        }
                    }

                    is NetworkRequest.Error -> {
                        veilLayoutCart.unVeil()
                    }

                }
            }
        }
    }


    private fun initRecyclerView(productItems: List<ResponseCartList.ProductItem>) {
        cartListAdapter.setData(productItems)
        binding.cartList.apply {
            setAdapter(cartListAdapter)
            setLayoutManager(LinearLayoutManager(requireContext()))
        }

        // Handle click events
        cartListAdapter.setOnItemClickListener { type, productItem, count ->
            when (type) {
                PLUS -> {
                    val newCount = count + 1
                    cartListAdapter.plusItemCount(productItem, count)
                    if (isNetworkAvailable)
                        viewModel.callChangeCountApi(productItem.id!!.toInt(), newCount)
                    updateTotalPrice(productItems)
                    lifecycleScope.launch {
                        EventBus.publish(Events.IsUpdateCart)
                    }
                }

                MINUS -> {
                    val newCount = count - 1
                    if (newCount > 0) {
                        cartListAdapter.minusItemCount(productItem, count)
                        if (isNetworkAvailable)
                            viewModel.callChangeCountApi(productItem.id!!.toInt(), newCount)
                        updateTotalPrice(productItems)
                        lifecycleScope.launch {
                            EventBus.publish(Events.IsUpdateCart)
                        }

                    }

                }

                DELETE -> {
                    if (isNetworkAvailable)
                        viewModel.callDeleteItemApi(productItem.id!!.toInt())
                    viewModel.callCartListApi()
                    lifecycleScope.launch {
                        EventBus.publish(Events.IsUpdateCart)
                    }

                }
            }
        }
    }

    private fun updateTotalPrice(productItems: List<ResponseCartList.ProductItem>) {
        totalPrice = 0.0
        totalDelivery = 0.0
        for (item in productItems) {
            val itemPrice = try {
                item.price ?: 0.0
            } catch (e: NumberFormatException) {
                0.0
            }
            val itemCount = item.count ?: 0
            totalPrice += itemPrice * itemCount
        }
        binding.totalPrice.text = totalPrice.formatToCurrency()
    }

    private fun loadUserInfo() {
        binding.apply {
            profileViewModel.profileData.observe(viewLifecycleOwner) { response ->
                when (response) {
                    is NetworkRequest.Loading -> {
                        veilLayoutCart.veil()
                    }

                    is NetworkRequest.Success -> {
                        veilLayoutCart.unVeil()
                        response.data?.let { data ->
                            if (data.name.isNullOrEmpty() && data.family.isNullOrEmpty()) {
                                lifecycleScope.launch {
                                    CustomToast.makeText(
                                        requireContext(),
                                        getString(R.string.empty_profile),
                                        CustomToast.LONG_DURATION,
                                        CustomToast.WARNING,
                                        true
                                    ).show()
                                    delay(3000)
                                    val direction = CartFragmentDirections.actionHomeToProfile()
                                    findNavController().navigate(direction)

                                }
                            } else {
                            }

                        }
                    }

                    is NetworkRequest.Error -> {
                        veilLayoutCart.unVeil()
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

    private fun showEmptyLayout() {
        binding.apply {
            emptyLay.isVisible = true
            empty.txtEmpty.text = getString(R.string.empty_cart)
            basketCard.isVisible = false
            txtCount.isVisible = false
            cartList.isVisible = false
        }
    }

    override fun onResume() {
        super.onResume()
        lifecycleScope.launch {
            EventBus.publish(Events.IsUpdateCart)
            if (isNetworkAvailable)
                viewModel.callCartListApi()
        }
    }
}



