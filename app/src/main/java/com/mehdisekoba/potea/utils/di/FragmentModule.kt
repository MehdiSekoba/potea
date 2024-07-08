package com.mehdisekoba.potea.utils.di

import androidx.fragment.app.Fragment
import com.mehdisekoba.potea.data.model.profile.BodySubmitAddress
import com.mehdisekoba.potea.ui.detail.adapters.PagerAdapter
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent

@Module
@InstallIn(FragmentComponent::class)
object FragmentModule {
    @Provides
    fun provideFragment(fragment: Fragment) =
        PagerAdapter(fragment.parentFragmentManager, fragment.lifecycle)

    @Provides
    fun bodySubmitAddress() = BodySubmitAddress()
}