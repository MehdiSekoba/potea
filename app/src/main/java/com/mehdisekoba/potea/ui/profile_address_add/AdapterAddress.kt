package com.mehdisekoba.potea.ui.profile_address_add

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.mehdisekoba.potea.data.model.profile.ResponseShowAddress.ResponseShowAddressItem
import com.mehdisekoba.potea.databinding.ItemAddressBinding
import com.mehdisekoba.potea.utils.base.BaseDiffUtils
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class AdapterAddress @Inject constructor(@ApplicationContext private val context: Context) :
    RecyclerView.Adapter<AdapterAddress.ViewHolder>() {

    private var items = emptyList<ResponseShowAddressItem>()
    private var isExpanded = false
    private var selectedPosition = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemAddressBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(items[position])

    override fun getItemCount() = items.size

    inner class ViewHolder(private val binding: ItemAddressBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(item: ResponseShowAddressItem) {
            // initViews
            binding.apply {
                // address
                txtAddress.text = item.address
                txtCity.text = item.city
                txtState.text = item.state
                txtNameFamily.text = "${item.name} ${item.family}"
                collapseCard(cardRoot)
                cardRoot.setOnClickListener {
                    if (isExpanded) {
                        collapseCard(cardRoot)
                    } else {
                        expandCard(cardRoot)
                    }
                    isExpanded = !isExpanded
                }
                // Set radio button
                txtDefault.visibility = if (bindingAdapterPosition == 0) View.VISIBLE else View.INVISIBLE
                radio.isChecked = bindingAdapterPosition == selectedPosition
                radio.setOnClickListener {
                    if (selectedPosition != bindingAdapterPosition){
                        val previousPosition = selectedPosition
                        selectedPosition = bindingAdapterPosition
                        notifyItemChanged(previousPosition)
                        notifyItemChanged(selectedPosition)

                    }
                }
            }
        }

        private fun expandCard(view: ConstraintLayout) {
            val layoutParams = view.layoutParams as ConstraintLayout.LayoutParams
            layoutParams.height = ConstraintLayout.LayoutParams.WRAP_CONTENT
            view.layoutParams = layoutParams
            view.requestLayout()
        }

        private fun collapseCard(view: ConstraintLayout) {
            val layoutParams = view.layoutParams as ConstraintLayout.LayoutParams
            layoutParams.height =
                context.resources.getDimensionPixelSize(`in`.nouri.dynamicsizeslib.R.dimen._60mdp)
            view.layoutParams = layoutParams
            view.requestLayout()
        }
    }

    fun setData(data: List<ResponseShowAddressItem>) {
        val adapterDiffUtils = BaseDiffUtils(items, data)
        val diffUtils = DiffUtil.calculateDiff(adapterDiffUtils)
        items = data
        diffUtils.dispatchUpdatesTo(this)
    }
}
