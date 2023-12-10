package com.hyeeyoung.wishboard.presentation.howtouse

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.hyeeyoung.wishboard.R
import com.hyeeyoung.wishboard.presentation.howtouse.screens.HowToUseFragment

class HowToUseAdapter(fm: FragmentActivity) : FragmentStateAdapter(fm) {
    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> HowToUseFragment.newInstance(
                titleRes = R.string.how_to_use_link_sharing_title,
                descriptionRes = R.string.how_to_use_link_sharing_description,
                imageRes = R.drawable.background_link_sharing
            )

            1 -> HowToUseFragment.newInstance(
                titleRes = R.string.how_to_use_folder_setting,
                descriptionRes = R.string.how_to_use_folder_setting_description,
                imageRes = R.drawable.backgound_folder_setting
            )

            else -> HowToUseFragment.newInstance(
                titleRes = R.string.noti_setting,
                descriptionRes = R.string.how_to_use_noti_setting_description,
                imageRes = R.drawable.background_noti_setting
            )
        }
    }
}
