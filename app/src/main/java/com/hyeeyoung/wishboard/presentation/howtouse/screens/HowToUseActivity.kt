package com.hyeeyoung.wishboard.presentation.howtouse.screens

import android.os.Bundle
import com.google.android.material.tabs.TabLayoutMediator
import com.hyeeyoung.wishboard.R
import com.hyeeyoung.wishboard.databinding.ActivityHowToUseBinding
import com.hyeeyoung.wishboard.presentation.howtouse.HowToUseAdapter
import com.hyeeyoung.wishboard.util.BaseActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HowToUseActivity : BaseActivity<ActivityHowToUseBinding>(R.layout.activity_how_to_use) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initializeView()
        addListeners()
    }

    private fun initializeView() {
        binding.howToUseContainer.clipToOutline = true
        binding.howToUse.adapter = HowToUseAdapter(this@HowToUseActivity)

        TabLayoutMediator(binding.indicator, binding.howToUse) { tab, position ->
            binding.howToUse.setCurrentItem(tab.position, false)
        }.attach()
    }

    private fun addListeners() {
        binding.yes.setOnClickListener {
            setResult(RESULT_OK, intent)
            finish()
        }
    }

    override fun onPause() {
        super.onPause()
        overridePendingTransition(0, 0)
    }
}