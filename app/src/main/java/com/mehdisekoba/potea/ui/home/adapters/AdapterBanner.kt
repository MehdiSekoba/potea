@file:Suppress("ktlint:standard:no-wildcard-imports")

package com.mehdisekoba.potea.ui.home.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.animation.AnimationUtils.lerp
import com.mehdisekoba.potea.data.model.home.ResponseBanner.ResponseBannerItem
import com.mehdisekoba.potea.databinding.ItemBannerBinding
import com.mehdisekoba.potea.utils.base.BaseDiffUtils
import com.mehdisekoba.potea.utils.extensions.loadImage
import javax.inject.Inject

class AdapterBanner
    @Inject
    constructor() : RecyclerView.Adapter<AdapterBanner.ViewHolder>() {
        private var items = emptyList<ResponseBannerItem>()

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int,
        ): ViewHolder {
            val binding = ItemBannerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return ViewHolder(binding)
        }

        override fun onBindViewHolder(
            holder: ViewHolder,
            position: Int,
        ) = holder.bind(items[position])

        override fun getItemCount() = if (items.size > 5) 5 else items.size

        override fun getItemViewType(position: Int) = position

        override fun getItemId(position: Int) = position.toLong()

        inner class ViewHolder(private val binding:ItemBannerBinding) : RecyclerView.ViewHolder(binding.root) {
            @SuppressLint("RestrictedApi")
            fun bind(item: ResponseBannerItem) {
                binding.apply {
                    // text
                    txtTitle.text = item.title!!.lowercase()
                    carouselItemContainer.setOnMaskChangedListener { maskRect ->
                        txtTitle.translationX = maskRect.left
                        txtTitle.setAlpha(lerp(1F, 0F, 0F, 80F, maskRect.left))
                    }
                    carouselImageView.loadImage(item.image!!)
                    // click
                    root.setOnClickListener {
                        // Click
                        onItemClickListener?.let { it(item) }
                    }
                }
            }
        }

        private var onItemClickListener: ((ResponseBannerItem) -> Unit)? = null

        fun setOnItemClickListener(listener: (ResponseBannerItem) -> Unit) {
            onItemClickListener = listener
        }

        fun setData(data: List<ResponseBannerItem>) {
            val adapterDiffUtils = BaseDiffUtils(items, data)
            val diffUtils = DiffUtil.calculateDiff(adapterDiffUtils)
            items = data
            diffUtils.dispatchUpdatesTo(this)
        }
    }
