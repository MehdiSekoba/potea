package com.mehdisekoba.potea.ui.detail.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.mehdisekoba.potea.R
import com.mehdisekoba.potea.data.model.detail.ResponseDetails
import com.mehdisekoba.potea.data.model.detail.ResponseDetails.Image
import com.mehdisekoba.potea.databinding.ItemProductImagesBinding
import com.mehdisekoba.potea.utils.base.BaseDiffUtils
import com.mehdisekoba.potea.utils.extensions.loadImage
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class ImagesAdapter
    @Inject
    constructor(
        @ApplicationContext private val context: Context,
    ) : RecyclerView.Adapter<ImagesAdapter.ViewHolder>() {
        private var items = emptyList<Image>()

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int,
        ): ViewHolder {
            val binding =
                ItemProductImagesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return ViewHolder(binding)
        }

        override fun onBindViewHolder(
            holder: ViewHolder,
            position: Int,
        ) = holder.bind(items[position])

        override fun getItemCount() = items.size

        override fun getItemViewType(position: Int) = position

        override fun getItemId(position: Int) = position.toLong()

        inner class ViewHolder(
            private val binding: ItemProductImagesBinding,
        ) : RecyclerView.ViewHolder(binding.root) {
            fun bind(item: Image) {
                binding.apply {
                    itemImg.loadImage(item.image!!)
                    itemImg.startAnimation(AnimationUtils.loadAnimation(context, R.anim.zoomin_out))
                    // click
                    root.setOnClickListener {
                        onItemClickListener?.let { it(item) }
                    }
                }
            }
        }

        private var onItemClickListener: ((Image) -> Unit)? = null

        fun setOnItemClickListener(listener: (Image) -> Unit) {
            onItemClickListener = listener
        }

        fun setData(data: List<Image>) {
            val adapterDiffUtils = BaseDiffUtils(items, data)
            val diffUtils = DiffUtil.calculateDiff(adapterDiffUtils)
            items = data
            diffUtils.dispatchUpdatesTo(this)
        }
    }
