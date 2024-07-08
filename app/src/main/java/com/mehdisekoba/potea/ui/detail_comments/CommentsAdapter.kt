package com.mehdisekoba.potea.ui.detail_comments

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.mehdisekoba.potea.R
import com.mehdisekoba.potea.data.model.detail.ResponseShowComment.ResponseShowCommentItem
import com.mehdisekoba.potea.databinding.ItemCommentBinding
import com.mehdisekoba.potea.utils.base.BaseDiffUtils
import com.mehdisekoba.potea.utils.extensions.loadImage
import com.mehdisekoba.potea.utils.extensions.setDynamicallyColor
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class CommentsAdapter
    @Inject
    constructor(
        @ApplicationContext private val context: Context,
    ) : RecyclerView.Adapter<CommentsAdapter.ViewHolder>() {
        private var items = emptyList<ResponseShowCommentItem>()

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int,
        ): ViewHolder {
           val binding = ItemCommentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return ViewHolder(binding)
        }

        override fun onBindViewHolder(
            holder: ViewHolder,
            position: Int,
        ) = holder.bind(items[position])

        override fun getItemCount() = items.size

        override fun getItemViewType(position: Int) = position

        override fun getItemId(position: Int) = position.toLong()

        inner class ViewHolder (private val binding: ItemCommentBinding): RecyclerView.ViewHolder(binding.root) {
            @SuppressLint("SetTextI18n")
            fun bind(item: ResponseShowCommentItem) {
                binding.apply {
                    // text
                    txtUserComment.text = "${item.name} ${item.family}"
                    txtTitleComment.text = item.title
                    txtDateComment.text = item.date
                    txtContentComment.text = item.content
                    txtScore.text = item.value.toString()
                    // default avatar
                    userAvatar.loadImage(R.drawable.placeholder.toString())
                    // change color
                    when (item.value!!) {
                        in 1.0..3.0 -> {
                            txtScore.setDynamicallyColor(R.color.Carnelian)
                            txtTitleComment.setTextColor(ContextCompat.getColor(context, R.color.Carnelian))
                        }
                        in 3.0..4.0 -> {
                            txtScore.setDynamicallyColor(R.color.Vivid_Gamboge)
                            txtTitleComment.setTextColor(ContextCompat.getColor(context, R.color.Vivid_Gamboge))
                        }
                        in 4.0..5.0 -> {
                            txtScore.setDynamicallyColor(R.color.Green)
                            txtTitleComment.setTextColor(ContextCompat.getColor(context, R.color.Green))
                        }
                    }
                    // click
                    root.setOnClickListener {
                        onItemClickListener?.let {
                            it(item)
                        }
                    }
                }
            }
        }

        private var onItemClickListener: ((ResponseShowCommentItem) -> Unit)? = null

        fun setOnItemClickListener(listener: (ResponseShowCommentItem) -> Unit) {
            onItemClickListener = listener
        }

        fun setData(data: List<ResponseShowCommentItem>) {
            val adapterDiffUtils = BaseDiffUtils(items, data)
            val diffUtils = DiffUtil.calculateDiff(adapterDiffUtils)
            items = data
            diffUtils.dispatchUpdatesTo(this)
        }
    }
