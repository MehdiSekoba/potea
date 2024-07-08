package com.mehdisekoba.potea.utils.other

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.mehdisekoba.potea.R

@SuppressLint("InflateParams")
@Suppress("ktlint:standard:property-naming")
class CustomToast private constructor(
    private val context: Context,
    private val message: String,
    private var duration: Int? = 4000,
    private val type: Int,
    private val imageResource: Int? = null,
    private val useAndroidIcon: Boolean = false,
) : Toast(context) {
    companion object {
        const val SUCCESS = 1
        const val ERROR = 2
        const val WARNING = 3
        const val SHORT_DURATION = LENGTH_SHORT
        const val LONG_DURATION = LENGTH_LONG

        fun makeText(
            context: Context,
            message: String,
            duration: Int = SHORT_DURATION,
            type: Int,
            useAndroidIcon: Boolean = false,
            imageResource: Int? = null,
        ): CustomToast =
            CustomToast(
                context = context,
                message = message,
                duration = duration,
                type = type,
                imageResource = imageResource,
                useAndroidIcon = useAndroidIcon,
            )
    }

    init {
        val layout = LayoutInflater.from(context).inflate(R.layout.customtoast_layout, null, false)
        val toastText = layout.findViewById<TextView>(R.id.toast_text)
        val toastTypeLayout = layout.findViewById<LinearLayout>(R.id.toast_type)
        val toastIcon = layout.findViewById<ImageView>(R.id.toast_icon)
        val androidIcon = layout.findViewById<ImageView>(R.id.imageView4)

        toastText.text = message
        androidIcon.visibility = if (useAndroidIcon) View.VISIBLE else View.GONE

        when (type) {
            SUCCESS -> {
                toastTypeLayout.setBackgroundResource(R.drawable.success_shape)
                toastIcon.setImageResource(R.drawable.ic_success)
            }

            ERROR -> {
                toastTypeLayout.setBackgroundResource(R.drawable.error_shape)
                toastIcon.setImageResource(R.drawable.ic_error)
            }

            WARNING -> {
                toastTypeLayout.setBackgroundResource(R.drawable.warning_shape)
                toastIcon.setImageResource(R.drawable.ic_warning)
            }
        }

        imageResource?.let {
            toastIcon.setImageResource(it)
        }

        view = layout
        this.setDuration(duration!!)
    }
}
