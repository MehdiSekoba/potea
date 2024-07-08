package com.mehdisekoba.potea.utils.extensions

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.net.Uri
import android.provider.MediaStore
import android.provider.Settings
import android.text.Editable
import android.text.TextPaint
import android.text.TextWatcher
import android.text.style.ClickableSpan
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.core.view.isVisible
import androidx.core.widget.ImageViewCompat
import androidx.dynamicanimation.animation.DynamicAnimation
import androidx.dynamicanimation.animation.SpringAnimation
import androidx.dynamicanimation.animation.SpringForce
import androidx.fragment.app.FragmentActivity
import coil.load
import coil.request.CachePolicy
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.mehdisekoba.potea.R
import java.util.Currency
import java.util.Locale

fun View.hideKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(windowToken, 0)
}


inline fun EditText.onTextChange(crossinline listener: (String) -> Unit) {
    this.addTextChangedListener(
        object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence?, p1: Int, p2: Int, p3: Int) {
                // NO OP
            }

            override fun onTextChanged(charSequence: CharSequence?, p1: Int, p2: Int, p3: Int) {
                listener(charSequence.toString())
            }

            override fun afterTextChanged(p0: Editable?) {
                // NO OP
            }
        }
    )
}


fun ImageView.loadImage(url: String) {
    this.load(url) {
        crossfade(true)
        crossfade(500)
        diskCachePolicy(CachePolicy.ENABLED)
        error(R.drawable.placeholder)
    }
}

private fun Context.openSettings(action: String) {
    val intent = Intent(action)
    startActivity(intent)
}

fun Context.openSettingWifi() = openSettings(Settings.ACTION_WIFI_SETTINGS)

fun Context.openSettingDataMobile() = openSettings(Settings.ACTION_SETTINGS)


fun String.isValidEmail(): Boolean =
    android.util.Patterns.EMAIL_ADDRESS
        .matcher(this)
        .matches()

fun View.isVisible(
    isShownLoading: Boolean,
    container: View,
) {
    if (isShownLoading) {
        this.isVisible = true
        container.isVisible = false
    } else {
        this.isVisible = false
        container.isVisible = true
    }
}

/**
 * Extension method to get ClickableSpan.
 * e.g.
 * val loginLink = getClickableSpan(context.getColorCompat(R.color.colorAccent), { })
 */
fun getClickableSpan(
    color: Int,
    action: (view: View) -> Unit,
): ClickableSpan =
    object : ClickableSpan() {
        override fun onClick(view: View) {
            action(view)
        }

        override fun updateDrawState(ds: TextPaint) {
            super.updateDrawState(ds)
            ds.color = color
        }
    }



fun Double.formatToCurrency(locale: Locale = Locale.US): String {
    val currency = Currency.getInstance(locale)
    val symbol = currency.symbol
    return "$symbol$this"
}

fun ImageView.setTint(
    @ColorRes color: Int,
) {
    ImageViewCompat.setImageTintList(
        this,
        ColorStateList.valueOf(ContextCompat.getColor(context, color)),
    )
}

fun ImageView.clear() {
    setImageDrawable(null)
}

@SuppressLint("ClickableViewAccessibility")
fun View.implementSpringAnimationTrait() {
    val scaleXAnim = SpringAnimation(this, DynamicAnimation.SCALE_X, 0.90f)
    val scaleYAnim = SpringAnimation(this, DynamicAnimation.SCALE_Y, 0.90f)

    setOnTouchListener { v, event ->
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                scaleXAnim.spring.stiffness = SpringForce.STIFFNESS_LOW
                scaleXAnim.spring.dampingRatio = SpringForce.DAMPING_RATIO_LOW_BOUNCY
                scaleXAnim.start()

                scaleYAnim.spring.stiffness = SpringForce.STIFFNESS_LOW
                scaleYAnim.spring.dampingRatio = SpringForce.DAMPING_RATIO_LOW_BOUNCY
                scaleYAnim.start()
            }

            MotionEvent.ACTION_UP,
            MotionEvent.ACTION_CANCEL,
            -> {
                scaleXAnim.cancel()
                scaleYAnim.cancel()
                val reverseScaleXAnim = SpringAnimation(this, DynamicAnimation.SCALE_X, 1f)
                reverseScaleXAnim.spring.stiffness = SpringForce.STIFFNESS_LOW
                reverseScaleXAnim.spring.dampingRatio = SpringForce.DAMPING_RATIO_LOW_BOUNCY
                reverseScaleXAnim.start()

                val reverseScaleYAnim = SpringAnimation(this, DynamicAnimation.SCALE_Y, 1f)
                reverseScaleYAnim.spring.stiffness = SpringForce.STIFFNESS_LOW
                reverseScaleYAnim.spring.dampingRatio = SpringForce.DAMPING_RATIO_LOW_BOUNCY
                reverseScaleYAnim.start()
            }
        }

        false
    }
}

fun TextView.setDynamicallyColor(color: Int) {
    // Start - Left = 0 || Top = 1 || End - Right = 2 || Bottom = 3
    this.compoundDrawables[0].setTint(ContextCompat.getColor(context, color))
    this.setTextColor(ContextCompat.getColor(context, color))
}

@SuppressLint("Range")
fun getRealFileFromUri(context: Context, uri: Uri): String? {
    var result: String? = null
    val resolver = context.contentResolver.query(uri, null, null, null, null)
    if (resolver ==null){
        result = uri.path
    }else{
        if (resolver.moveToFirst()){
            result = resolver.getString(resolver.getColumnIndex(MediaStore.Images.ImageColumns.DATA))

        }
        resolver.close()

    }
    return result
}

fun MaterialAlertDialogBuilder.createAndShow() {
    val dialog = create()
    dialog.show()
}

fun Context.alert(dialogBuilder: MaterialAlertDialogBuilder.() -> Unit) {
    MaterialAlertDialogBuilder(this)
        .apply {
            setCancelable(false)
            dialogBuilder()
        }
        .createAndShow()
}
