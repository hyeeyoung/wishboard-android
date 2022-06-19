package com.hyeeyoung.wishboard.presentation.howtouse

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.hyeeyoung.wishboard.presentation.howtouse.screens.HowToFolderSettingFragment
import com.hyeeyoung.wishboard.presentation.howtouse.screens.HowToLinkSharingFragment
import com.hyeeyoung.wishboard.presentation.howtouse.screens.HowToNotiSettingFragment

class HowToUseAdapter(fm: FragmentActivity) : FragmentStateAdapter(fm) {
    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> HowToLinkSharingFragment()
            1 -> HowToFolderSettingFragment()
            else -> HowToNotiSettingFragment()
        }
    }

    companion object {
        private const val TAG = "HowToUseAdapter"
    }
}
