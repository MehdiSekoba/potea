package com.mehdisekoba.potea.ui.detail.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.mehdisekoba.potea.ui.detail_chart.DetailChartFragment
import com.mehdisekoba.potea.ui.detail_comments.DetailCommentsFragment
import com.mehdisekoba.potea.ui.detail_features.DetailFeaturesFragment

class PagerAdapter(manager: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(manager, lifecycle) {
    override fun getItemCount() = 3

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> DetailFeaturesFragment()
            1 -> DetailChartFragment()
            else -> DetailCommentsFragment()
        }

    }
}