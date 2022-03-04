package com.hyeeyoung.wishboard.view.wish.item.screens

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.hyeeyoung.wishboard.R
import com.hyeeyoung.wishboard.databinding.ActivityWishLinkSharingBinding
import com.hyeeyoung.wishboard.model.common.ProcessStatus
import com.hyeeyoung.wishboard.util.*
import com.hyeeyoung.wishboard.viewmodel.WishItemRegistrationViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class WishLinkSharingActivity : AppCompatActivity() {
    private val viewModel: WishItemRegistrationViewModel by viewModels()
    private lateinit var binding: ActivityWishLinkSharingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_wish_link_sharing)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this@WishLinkSharingActivity

        // 링크 공유로 데이터 받기
        val intent = intent
        val action = intent.action
        val type = intent.type

        if (Intent.ACTION_SEND == action && type != null) {
            when (type) {
                "text/plain" -> {
                    val url = intent.getStringExtra(Intent.EXTRA_TEXT) ?: return
                    viewModel.setItemUrl(url)
                    lifecycleScope.launch(Dispatchers.IO) {
                        viewModel.getWishItemInfo(url)
                    }
                }
            }
        }

        initializeView()
        addListeners()
        addObservers()
    }

    private fun initializeView() {
        // 알림 유형 및 날짜 넘버피커 설정
        setTypePicker(binding.typePicker)
        setDatePicker(binding.datePicker)
        setHourPicker(binding.hourPicker)
        setMinutePicker(binding.minutePicker)

        binding.itemImage.clipToOutline = true
    }

    private fun addListeners() {
        binding.add.setOnClickListener {
            lifecycleScope.launch {
                viewModel.uploadWishItemByLinkSharing()
            }
        }
        binding.cancel.setOnClickListener {
            finish()
        }
        binding.complete.setOnClickListener {
            viewModel.setVisibilityNotiSettingDialog(false)
            val type = getTypePickerValue(binding.typePicker.value)
            val date = getDatePickerValue(
                binding.datePicker.value,
                binding.hourPicker.value,
                binding.minutePicker.value
            )
            viewModel.setNotiInfo(true, type, date)
        }
    }

    private fun addObservers() {
        viewModel.getItemImage().observe(this) { image ->
            if (image == null) return@observe
            Glide.with(this).load(image).into(binding.itemImage)
        }
        viewModel.isCompleteUpload().observe(this) { isComplete ->
            if (isComplete == true) {
                Toast.makeText(
                    this,
                    getString(R.string.wish_item_registration_toast_text),
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
        viewModel.getRegistrationStatus().observe(this) {
            when (it) {
                ProcessStatus.IDLE -> {
                    binding.loadingLottie.visibility = View.GONE
                }
                ProcessStatus.IN_PROGRESS -> {
                    binding.loadingLottie.visibility = View.VISIBLE
                    binding.loadingLottie.playAnimation()
                }
            }
        }
    }

    companion object {
        private const val TAG = "WishLinkSharingActivity"
    }
}