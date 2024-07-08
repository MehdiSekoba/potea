package com.mehdisekoba.potea.ui.favorite

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.mehdisekoba.potea.data.model.favourite.ResponseFavourite
import com.mehdisekoba.potea.data.model.favourite.ResponseFavourite.ResponseFavouriteItem
import com.mehdisekoba.potea.databinding.ItemFavouriteBinding
import com.mehdisekoba.potea.utils.base.BaseDiffUtils
import com.mehdisekoba.potea.utils.extensions.loadImage
import javax.inject.Inject

class AdapterFavourite @Inject constructor() : RecyclerView.Adapter<AdapterFavourite.ViewHolder>() {
    //binding
    private var items = emptyList<ResponseFavouriteItem>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemFavouriteBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(items[position])
    override fun getItemCount() = items.size

    inner class ViewHolder(private val binding:ItemFavouriteBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ResponseFavouriteItem) {
            //initViews
            binding.apply {
                itemTitle.text = item.name
                itemImg.loadImage(item.url!!)
                //click
                root.setOnClickListener {
                    onItemClickListener?.let {
                        it(item.id!!)
                    }
                }
            }

        }
    }

    private var onItemClickListener: ((String) -> Unit)? = null
    fun setOnItemClickListener(listener: (String) -> Unit) {
        onItemClickListener = listener
    }

    fun setData(data: List<ResponseFavouriteItem>) {
        val adapterDiffUtil = BaseDiffUtils(items, data)
        val diffUtils = DiffUtil.calculateDiff(adapterDiffUtil)
        items = data
        diffUtils.dispatchUpdatesTo(this)

    }
}