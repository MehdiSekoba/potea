package com.mehdisekoba.potea.ui.onboarding

import android.content.Context
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.mehdisekoba.potea.R
import com.mehdisekoba.potea.data.model.onboarding.IntroSlide
import com.mehdisekoba.potea.databinding.ItemOnboardingBinding
import com.mehdisekoba.potea.utils.base.BaseDiffUtils
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class AdapterOnBoarding
@Inject
constructor(@ApplicationContext val context: Context) :
    RecyclerView.Adapter<AdapterOnBoarding.ViewHolder>() {
    // Binding
    private var items = emptyList<IntroSlide>()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ViewHolder {
        val binding = ItemOnboardingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int,
    ) = holder.bind(items[position])

    override fun getItemCount() = items.size

    inner class ViewHolder (private val binding:ItemOnboardingBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(item: IntroSlide) {
            binding.apply {
                // image
                itemImg.load(item.iconResourceId)
                // description
                val title = SpannableStringBuilder(item.description)
                if (bindingAdapterPosition == 0) {
                    val colorSpan =
                        ForegroundColorSpan(ContextCompat.getColor(context, R.color.Green))
                    val start = title.indexOf("Potea")
                    if (start >= 0) {
                        val end = start + "Potea".length
                        title.setSpan(colorSpan, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    }
                }
                txtDesc.text = title
            }
        }
    }


    fun setData(data: List<IntroSlide>) {
        val adapterDiffUtils = BaseDiffUtils(items, data)
        val diffUtils = DiffUtil.calculateDiff(adapterDiffUtils)
        items = data
        diffUtils.dispatchUpdatesTo(this)
    }
}
