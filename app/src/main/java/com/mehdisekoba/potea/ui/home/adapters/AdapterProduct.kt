package com.mehdisekoba.potea.ui.home.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.mehdisekoba.potea.data.model.home.ResponseProducts.ResponseProductsItem
import com.mehdisekoba.potea.databinding.ItemProductBinding
import com.mehdisekoba.potea.utils.base.BaseDiffUtils
import com.mehdisekoba.potea.utils.extensions.formatToCurrency
import com.mehdisekoba.potea.utils.extensions.implementSpringAnimationTrait
import com.mehdisekoba.potea.utils.extensions.loadImage
import javax.inject.Inject

class AdapterProduct
    @Inject
    constructor() : RecyclerView.Adapter<AdapterProduct.ViewHolder>() {

        // list
        private var items = emptyList<ResponseProductsItem>()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ViewHolder {
          val  binding = ItemProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return ViewHolder(binding)
        }

        override fun onBindViewHolder(
            holder: ViewHolder,
            position: Int,
        ) = holder.bind(items[position])

        override fun getItemCount() = items.size

        override fun getItemId(position: Int) = position.toLong()

        override fun getItemViewType(position: Int) = position

        inner class ViewHolder (private val binding:ItemProductBinding): RecyclerView.ViewHolder(binding.root) {
            fun bind(item: ResponseProductsItem) {
                // initView
                binding.apply {
                    // image
                    itemImg.loadImage(item.image!!)
                    // title
                    itemTitle.text = item.name
                    // price
                    itemPrice.text = item.price!!.toDouble().formatToCurrency()
                    // animation
                    root.implementSpringAnimationTrait()
                    // click
                    root.setOnClickListener {
                        onItemClickListener?.let {
                            it(item)
                        }
                    }
                }
            }
        }

        private var onItemClickListener: ((ResponseProductsItem) -> Unit)? = null

        fun setOnItemClickListener(listener: (ResponseProductsItem) -> Unit) {
            onItemClickListener = listener
        }

        fun setData(data: List<ResponseProductsItem>) {
            val adapterDiffutils = BaseDiffUtils(items, data)
            val diffUtils = DiffUtil.calculateDiff(adapterDiffutils)
            items = data
            diffUtils.dispatchUpdatesTo(this)
        }
    }
