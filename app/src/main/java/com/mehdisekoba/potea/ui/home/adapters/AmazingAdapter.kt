package com.mehdisekoba.potea.ui.home.adapters

import android.graphics.Paint
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.mehdisekoba.potea.R
import com.mehdisekoba.potea.data.model.home.ResponseAmazing.ResponseAmazingItem
import com.mehdisekoba.potea.databinding.ItemAmazingBinding
import com.mehdisekoba.potea.utils.ERROR_TIME
import com.mehdisekoba.potea.utils.PATTERN_TIME
import com.mehdisekoba.potea.utils.base.BaseDiffUtils
import com.mehdisekoba.potea.utils.extensions.formatToCurrency
import com.mehdisekoba.potea.utils.extensions.implementSpringAnimationTrait
import com.mehdisekoba.potea.utils.extensions.loadImage
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class AmazingAdapter
@Inject
constructor() : RecyclerView.Adapter<AmazingAdapter.ViewHolder>() {
    // List of items
    private var items = emptyList<ResponseAmazingItem>()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ViewHolder {
        val binding = ItemAmazingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int,
    ) {
        holder.bind(items[position])
    }

    override fun getItemCount() = items.size

    inner class ViewHolder(
        private val binding: ItemAmazingBinding,
    ) : RecyclerView.ViewHolder(binding.root) {
        private var countDownTimer: CountDownTimer? = null

        fun bind(item: ResponseAmazingItem) {
            binding.apply {
                // Load image
                itemImg.loadImage(item.image ?: "")
                //  name
                itemTitle.text = item.name
                // time
                item.amazingTime?.let { time ->
                    discountTimer(time.toString())
                }
                // Final price
                itemPrice.text =
                    item.amazingPrice
                        .toString()
                        .toDouble()
                        .formatToCurrency()
                // Discount
                itemPriceDiscount.apply {
                    text =
                        item.price
                            .toString()
                            .toDouble()
                            .formatToCurrency()
                    paintFlags = this.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                    setTextColor(ContextCompat.getColor(context, R.color.Carnelian))
                }
                // animation
                root.implementSpringAnimationTrait()
                // click
                root.setOnClickListener {
                    onItemClickListener?.let {
                        it(item.id!!)
                    }
                }
            }
        }

        private fun discountTimer(fullDate: String) {
            try {
                val date: Date =
                    if (fullDate.matches("\\d+".toRegex())) {
                        Date(fullDate.toLong())
                    } else {
                        val formatter = SimpleDateFormat(PATTERN_TIME, Locale.ENGLISH)
                        formatter.parse(fullDate) ?: throw ParseException(ERROR_TIME, 0)
                    }
                val currentMillis = System.currentTimeMillis()
                val finalMillis = date.time - currentMillis
                countDownTimer?.cancel() // Cancel any existing timer

                countDownTimer =
                    object : CountDownTimer(finalMillis, 1000) {
                        override fun onTick(millisUntilFinished: Long) {
                            // Calculate time
                            var timer = millisUntilFinished
                            val days: Long = TimeUnit.MILLISECONDS.toDays(timer)
                            timer -= TimeUnit.DAYS.toMillis(days)
                            val hours: Long = TimeUnit.MILLISECONDS.toHours(timer)
                            timer -= TimeUnit.HOURS.toMillis(hours)
                            val minutes: Long = TimeUnit.MILLISECONDS.toMinutes(timer)
                            timer -= TimeUnit.MINUTES.toMillis(minutes)
                            val seconds: Long = TimeUnit.MILLISECONDS.toSeconds(timer)

                            // Update UI
                            try {
                                binding.apply {
                                    if (days > 0) {
                                        dayTxt.isVisible = true
                                        dayTxt.text = days.toString()
                                    } else {
                                        dayTxt.isVisible = false
                                    }
                                    hourTxt.text = hours.toString()
                                    minuteTxt.text = minutes.toString()
                                    secondTxt.text = seconds.toString()
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }

                        override fun onFinish() {
                            // Handle countdown finished
                        }
                    }.start()
            } catch (e: ParseException) {
                e.printStackTrace()
            }
        }
    }

    private var onItemClickListener: ((Int) -> Unit)? = null

    fun setonItemClickListener(listener: (Int) -> Unit) {
        onItemClickListener = listener
    }

    fun setData(data: List<ResponseAmazingItem>) {
        val adapterDiffUtils = BaseDiffUtils(items, data)
        val diffUtils = DiffUtil.calculateDiff(adapterDiffUtils)
        items = data
        diffUtils.dispatchUpdatesTo(this)
    }
}
