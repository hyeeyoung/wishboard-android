package com.hyeeyoung.wishboard.view.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.hyeeyoung.wishboard.R
import com.hyeeyoung.wishboard.databinding.FragmentHowToUseBinding
import com.hyeeyoung.wishboard.model.HowToUseType

class HowToUseFragment : Fragment() {
    private lateinit var binding: FragmentHowToUseBinding
    private var howToUseType: String = HowToUseType.HOW_TO_SAVE_AN_ITEM.name

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            it.getString(ARG_HOW_TO_USE_TYPE).let { type ->
                if (type == null) return
                howToUseType = type
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHowToUseBinding.inflate(inflater, container, false)

        initializeView()

        return binding.root
    }

    private fun initializeView() {
        when (howToUseType) {
            HowToUseType.HOW_TO_SAVE_AN_ITEM.name -> {
                setHowToUseResource(
                    R.drawable.background_link_sharing,
                    R.string.how_to_use_link_sharing_title,
                    R.string.how_to_use_link_sharing_description
                )
            }
            HowToUseType.HOW_TO_SET_FOLDER.name -> {
                setHowToUseResource(
                    R.drawable.backgound_folder_setting,
                    R.string.folder_list_title,
                    R.string.how_to_use_folder_setting_description
                )
            }
            HowToUseType.HOW_TO_SET_NOTI.name -> {
                setHowToUseResource(
                    R.drawable.background_noti_setting,
                    R.string.noti_setting,
                    R.string.how_to_use_noti_setting_description
                )
            }
        }
    }

    private fun setHowToUseResource(imageRes: Int, titleRes: Int, descriptionRes: Int) {
        Glide.with(binding.howToUseImage).load(imageRes).into(binding.howToUseImage)
        binding.howToUserTitle.text = getString(titleRes)
        binding.howToUserDescription.text = getString(descriptionRes)
    }

    companion object {
        private const val TAG = "HowToUseFragment"
        private const val ARG_HOW_TO_USE_TYPE = "howToUse"

        fun newInstance(howToUseType: HowToUseType) = HowToUseFragment().apply {
            arguments = Bundle().apply {
                putString(ARG_HOW_TO_USE_TYPE, howToUseType.name)
            }
        }
    }
}
