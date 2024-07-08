package com.mehdisekoba.potea.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.iammert.library.ui.multisearchviewlib.MultiSearchView
import com.mehdisekoba.potea.R
import com.mehdisekoba.potea.data.model.search.ResponseSearch
import com.mehdisekoba.potea.databinding.FragmentSearchBinding
import com.mehdisekoba.potea.utils.base.BaseFragment
import com.mehdisekoba.potea.utils.extensions.alert
import com.mehdisekoba.potea.utils.network.NetworkRequest
import com.mehdisekoba.potea.utils.other.CustomToast
import com.mehdisekoba.potea.viewmodel.SearchViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class SearchFragment : BaseFragment<FragmentSearchBinding>() {
    override val bindingInflater: (LayoutInflater) -> FragmentSearchBinding
        get() = FragmentSearchBinding::inflate

    @Inject
    lateinit var adapterSearch: AdapterSearch

    //other
    private val viewModel by viewModels<SearchViewModel>()
    private var selectedSortIndex = 0
    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            multiSearchView.apply {
                setClearIconColor(R.color.gray_200)
                setSelectedTabColor(R.color.UFO_Green)
                setSearchViewListener(object : MultiSearchView.MultiSearchViewListener {
                    override fun onItemSelected(index: Int, s: CharSequence) {
                    }

                    override fun onTextChanged(index: Int, s: CharSequence) {
                    }

                    override fun onSearchComplete(index: Int, s: CharSequence) {
                        if (s.toString().length > 2) {
                            viewModel.callSearchApi(
                                s.toString()
                            )
                        }
                    }

                    override fun onSearchItemRemoved(index: Int) {
                    }

                })
            }
            emptyLay.isVisible = true
            searchEmpty.apply {
                txtEmpty.text = getString(R.string.default_search)
                imageView.setImageResource(R.drawable.easter_egg_hunt_bro)
            }
            searchList.isVisible = false
            imgSort.setOnClickListener {
                val filterProduct = resources.getStringArray(R.array.filter_search)
                requireContext().alert {
                    setTitle(R.string.filter_search)
                    setSingleChoiceItems(
                        filterProduct,
                        selectedSortIndex
                    ) { dialog, which ->
                        selectedSortIndex = which
                        dialog.dismiss()

                    }
                }
            }

        }
        //Load data
        loadSearchData()
    }


    private fun loadSearchData() {
        binding.apply {
            viewModel.searchData.observe(viewLifecycleOwner) { response ->
                when (response) {
                    is NetworkRequest.Loading -> {
                        searchList.veil()
                    }

                    is NetworkRequest.Success -> {
                        searchList.unVeil()
                        response.data?.let { data ->
                            if (data.results.isNullOrEmpty().not()) {
                                initRecyclerView(data.results)
                                emptyLay.isVisible = false
                                searchList.isVisible = true
                            } else {
                                searchEmpty.apply {
                                    txtEmpty.text = getString(R.string.not_found_search)
                                    imageView.setImageResource(R.drawable.ic_empty)
                                }
                                emptyLay.isVisible = true
                                searchList.isVisible = false
                            }
                        }
                    }

                    is NetworkRequest.Error -> {
                        searchList.unVeil()
                        CustomToast.makeText(
                            requireContext(),
                            response.error!!,
                            CustomToast.LONG_DURATION,
                            CustomToast.ERROR,
                            true
                        )
                    }
                }
            }
        }
    }

    private fun initRecyclerView(results: List<ResponseSearch.Result>?) {
        adapterSearch.setData(results!!)
        binding.searchList.apply {
            setAdapter(adapterSearch)
            setLayoutManager(LinearLayoutManager(requireContext()))
            addVeiledItems(10)
        }
        adapterSearch.setOnItemClickListener {
            val direction = SearchFragmentDirections.actionSearchToDetail(it.toInt())
            findNavController().navigate(direction)
        }
    }


}
