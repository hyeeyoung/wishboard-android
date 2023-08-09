package com.hyeeyoung.wishboard.presentation.howtouse.screens

import android.os.Bundle
import android.view.View
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.hyeeyoung.wishboard.R
import com.hyeeyoung.wishboard.databinding.FragmentHowToUseBinding
import com.hyeeyoung.wishboard.util.BaseFragment

class HowToUseFragment :
    BaseFragment<FragmentHowToUseBinding>(R.layout.fragment_how_to_use) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.run {
            binding.title = getInt(ARG_TITLE)
            binding.description = getInt(ARG_DESCRIPTION)
            binding.drawableRes = getInt(ARG_IMAGE)
        }
    }

    companion object {
        private const val ARG_TITLE = "title"
        private const val ARG_DESCRIPTION = "description"
        private const val ARG_IMAGE = "drawableRes"

        fun newInstance(
            @StringRes titleRes: Int,
            @StringRes descriptionRes: Int,
            @DrawableRes imageRes: Int
        ) =
            HowToUseFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_TITLE, titleRes)
                    putInt(ARG_DESCRIPTION, descriptionRes)
                    putInt(ARG_IMAGE, imageRes)
                }
            }
    }
}