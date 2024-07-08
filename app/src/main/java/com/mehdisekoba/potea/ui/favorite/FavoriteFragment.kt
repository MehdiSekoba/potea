package com.mehdisekoba.potea.ui.favorite

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.mehdisekoba.potea.R
import com.mehdisekoba.potea.data.model.favourite.ResponseFavourite
import com.mehdisekoba.potea.databinding.FragmentFavoriteBinding
import com.mehdisekoba.potea.utils.base.BaseFragment
import com.mehdisekoba.potea.utils.network.NetworkRequest
import com.mehdisekoba.potea.utils.other.CustomToast
import com.mehdisekoba.potea.viewmodel.ProfileFavoritesViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class FavoriteFragment : BaseFragment<FragmentFavoriteBinding>() {
    override val bindingInflater: (inflater: LayoutInflater) -> FragmentFavoriteBinding
        get() = FragmentFavoriteBinding::inflate

    @Inject
    lateinit var adapterFavourite: AdapterFavourite

    //other
    private val viewModel by viewModels<ProfileFavoritesViewModel>()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (isNetworkAvailable) {
            viewModel.callFavoritesApi()
        } else {
            CustomToast.makeText(
                requireContext(),
                getString(R.string.checkYourNetwork),
                CustomToast.LONG_DURATION,
                CustomToast.WARNING,
                true
            )
        }
        //load data
        loadFavouriteData()
    }

    private fun loadFavouriteData() {
        binding.apply {
            viewModel.favoritesData.observe(viewLifecycleOwner) { response ->
                when (response) {
                    is NetworkRequest.Loading -> {
                        favoriteList.veil()
                    }

                    is NetworkRequest.Success -> {
                        favoriteList.unVeil()
                        response.data?.let { data ->
                            if (data.isNotEmpty()) {
                                initRecycler(data)
                            } else {
                                emptyLay.isVisible = true
                                favoriteList.isVisible = false
                                emptyFav.txtEmpty.text = getString(R.string.empty_favorite)
                            }
                        }
                    }

                    is NetworkRequest.Error -> {
                        favoriteList.unVeil()

                    }
                }
            }
        }
    }

    private fun initRecycler(data: List<ResponseFavourite.ResponseFavouriteItem>) {
        binding.apply {
            adapterFavourite.setData(data)
            favoriteList.apply {
                setAdapter(adapterFavourite)
                setLayoutManager(LinearLayoutManager(requireContext()))
            }
            adapterFavourite.setOnItemClickListener {
                val direction = FavoriteFragmentDirections.actionFavouriteToDetail(it.toInt())
                findNavController().navigate(direction)
            }
        }
    }
}