package com.hyeeyoung.wishboard.view.home

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.google.android.material.tabs.TabLayoutMediator
import com.hyeeyoung.wishboard.R
import com.hyeeyoung.wishboard.databinding.ActivityHowToUseBinding
import com.hyeeyoung.wishboard.view.wish.adapter.HowToUseAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HowToUseActivity() : AppCompatActivity() {
    private lateinit var binding: ActivityHowToUseBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_how_to_use)

        initializeView()
        addListeners()
    }

    private fun initializeView() {
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

    companion object {
        private const val TAG = "HowToUseActivity"
    }
}