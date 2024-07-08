package com.mehdisekoba.potea.ui.search

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.mehdisekoba.potea.data.model.search.ResponseSearch.Result
import com.mehdisekoba.potea.databinding.ItemSearchBinding
import com.mehdisekoba.potea.utils.base.BaseDiffUtils
import com.mehdisekoba.potea.utils.extensions.formatToCurrency
import com.mehdisekoba.potea.utils.extensions.loadImage
import javax.inject.Inject

class AdapterSearch @Inject constructor() : RecyclerView.Adapter<AdapterSearch.ViewHolder>() {
    var items = emptyList<Result>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemSearchBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(items[position])

    override fun getItemCount() = items.size

    inner class ViewHolder(private val binding: ItemSearchBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Result) {
            binding.apply {
                itemImg.loadImage(item.image!!)
                txtTitle.text = item.name
                txtPrice.text = item.price!!.toDouble().formatToCurrency()
                if (item.score!! > 0.0) {
                    txtRate.isVisible = true
                    txtRate.text = item.score.toString()
                } else {
                    txtRate.visibility = View.INVISIBLE
                }
                //click
                root.setOnClickListener {
                    onItemClickListener?.let { it(item.id.toString()) }
                }
            }
        }

    }

    private var onItemClickListener: ((String) -> Unit)? = null
    fun setOnItemClickListener(listener: (String) -> Unit) {
        onItemClickListener = listener
    }

    fun setData(data: List<Result>) {
        val adapterDiffUtils = BaseDiffUtils(items, data)
        val diffUtils = DiffUtil.calculateDiff(adapterDiffUtils)
        items = data
        diffUtils.dispatchUpdatesTo(this)
    }
}