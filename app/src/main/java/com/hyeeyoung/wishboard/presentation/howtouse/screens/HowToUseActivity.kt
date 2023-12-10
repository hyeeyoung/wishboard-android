package com.hyeeyoung.wishboard.presentation.howtouse.screens

import android.os.Bundle
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.google.android.material.tabs.TabLayoutMediator
import com.hyeeyoung.wishboard.R
import com.hyeeyoung.wishboard.databinding.ActivityHowToUseBinding
import com.hyeeyoung.wishboard.presentation.howtouse.HowToUseAdapter
import com.hyeeyoung.wishboard.util.BaseActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HowToUseActivity : BaseActivity<ActivityHowToUseBinding>(R.layout.activity_how_to_use) {
    private val howToUseAdapter = HowToUseAdapter(this@HowToUseActivity)
    private val pageChangeCallback = object : OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            super.onPageSelected(position)
            binding.yes.isEnabled = howToUseAdapter.itemCount - 1 == position
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initializeView()
        addListeners()
    }

    override fun onStart() {
        super.onStart()
        binding.howToUse.registerOnPageChangeCallback(pageChangeCallback)
    }

    private fun initializeView() {
        binding.howToUseContainer.clipToOutline = true
        binding.howToUse.adapter = howToUseAdapter

        TabLayoutMediator(binding.indicator, binding.howToUse) { tab, _ ->
            binding.howToUse.setCurrentItem(tab.position, false)
        }.attach()
    }

    private fun addListeners() {
        binding.yes.setOnClickListener {
            setResult(RESULT_OK, intent)
            finish()
        }
    }

    override fun onStop() {
        super.onStop()
        binding.howToUse.unregisterOnPageChangeCallback(pageChangeCallback)
    }

    override fun onPause() {
        super.onPause()
        overridePendingTransition(0, 0)
    }
}