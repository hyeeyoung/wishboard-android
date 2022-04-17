package com.hyeeyoung.wishboard.view.wish.adapter

import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.hyeeyoung.wishboard.model.HowToUseType
import com.hyeeyoung.wishboard.view.home.HowToUseFragment

class HowToUseAdapter(fm: FragmentActivity) : FragmentStateAdapter(fm) {

    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): HowToUseFragment {
        val howToUseType = when (position) {
            0 -> HowToUseType.HOW_TO_SAVE_AN_ITEM
            1 -> HowToUseType.HOW_TO_SET_FOLDER
            2 -> HowToUseType.HOW_TO_SET_NOTI
            else -> HowToUseType.HOW_TO_SAVE_AN_ITEM
        }
        return HowToUseFragment.newInstance(howToUseType)
    }

    companion object {
        private const val TAG = "HowToUseAdapter"
        const val START_POSITION = 0
    }
}
