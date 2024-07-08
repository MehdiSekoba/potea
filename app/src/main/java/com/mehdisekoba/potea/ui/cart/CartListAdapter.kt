package com.mehdisekoba.potea.ui.cart

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.mehdisekoba.potea.data.model.cart.ResponseCartList.ProductItem
import com.mehdisekoba.potea.databinding.ItemCartBinding
import com.mehdisekoba.potea.utils.DELETE
import com.mehdisekoba.potea.utils.MINUS
import com.mehdisekoba.potea.utils.PLUS
import com.mehdisekoba.potea.utils.base.BaseDiffUtils
import com.mehdisekoba.potea.utils.extensions.formatToCurrency
import com.mehdisekoba.potea.utils.extensions.loadImage
import javax.inject.Inject

class CartListAdapter @Inject constructor() :
    RecyclerView.Adapter<CartListAdapter.ViewHolder>() {

    var items = emptyList<ProductItem>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemCartBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(items[position])

    override fun getItemCount() = items.size

    inner class ViewHolder(private val binding: ItemCartBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ProductItem) {
            binding.apply {
                itemImg.loadImage(item.image!!)
                itemTitle.text = item.name!!
                itemCountTxt.text = item.count.toString()
                itemPrice.text= item.price!!.formatToCurrency()
                itemPriceDiscount.text=item.offPercent!!.toDouble().formatToCurrency()

                // Handle visibility of delete and minus buttons
                if (item.count == 1) {
                    itemDeleteImg.visibility=View.VISIBLE
                    itemMinusImg.visibility = View.INVISIBLE
                } else {
                    itemDeleteImg.visibility = View.INVISIBLE
                    itemMinusImg.visibility = View.VISIBLE
                }

                //click
                itemPlusImg.setOnClickListener {
                    onItemClickListener?.let { it(PLUS, item, item.count!!) }
                }
                itemMinusImg.setOnClickListener {
                    onItemClickListener?.let { it(MINUS, item, item.count!!) }
                }
                itemDeleteImg.setOnClickListener {
                    onItemClickListener?.let {
                        it(DELETE, item, item.id!!.toInt())
                    }
                }
            }
        }
    }

    private var onItemClickListener: ((String, ProductItem, Int) -> Unit)? = null

    fun setOnItemClickListener(listener: (String, ProductItem, Int) -> Unit) {
        onItemClickListener = listener
    }

    fun plusItemCount(cartItem: ProductItem, count: Int) {
        val index = items.indexOf(cartItem)
        if (index != -1) {
            items[index].count = count + 1
            notifyItemChanged(index)
        }
    }

    fun minusItemCount(cartItem: ProductItem, count: Int) {
        val index = items.indexOf(cartItem)
        if (index != -1) {
            val newCount = count - 1
            if (newCount >= 0) {
                items[index].count = newCount
                notifyItemChanged(index)
            }
        }
    }
    fun setData(data: List<ProductItem>) {
        val adapterDiffUtils = BaseDiffUtils(items, data)
        val diffUtils = DiffUtil.calculateDiff(adapterDiffUtils)
        items = data
        diffUtils.dispatchUpdatesTo(this)
    }
}
