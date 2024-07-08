package com.mehdisekoba.potea.utils.views

import android.content.Context
import android.os.Parcelable
import android.util.AttributeSet
import androidx.core.widget.NestedScrollView

class SaveLastStateNestedScrollView : NestedScrollView {
    constructor(context: Context) : super(context)
    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet)
    constructor(context: Context, attributeSet: AttributeSet, defStyleAttr: Int) : super(context, attributeSet, defStyleAttr)

    public override fun onSaveInstanceState(): Parcelable {
        return super.onSaveInstanceState()
    }

    public override fun onRestoreInstanceState(state: Parcelable?) {
        super.onRestoreInstanceState(state)
    }
}