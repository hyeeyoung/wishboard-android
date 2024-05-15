package com.hyeeyoung.wishboard.presentation.howtouse.screens

import android.os.Bundle
import android.view.View
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import coil.load
import coil.transform.RoundedCornersTransformation
import com.hyeeyoung.wishboard.R
import com.hyeeyoung.wishboard.databinding.FragmentHowToUseBinding
import com.hyeeyoung.wishboard.util.BaseFragment
import com.hyeeyoung.wishboard.util.extension.px

class HowToUseFragment :
    BaseFragment<FragmentHowToUseBinding>(R.layout.fragment_how_to_use) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.run {
            binding.howToUserTitle.text = requireContext().getString(getInt(ARG_TITLE))
            binding.howToUserDescription.text = requireContext().getString(getInt(ARG_DESCRIPTION))
            binding.howToUseImage.load(getInt(ARG_IMAGE)) {
                transformations(RoundedCornersTransformation(20.px, 20.px))
            }
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