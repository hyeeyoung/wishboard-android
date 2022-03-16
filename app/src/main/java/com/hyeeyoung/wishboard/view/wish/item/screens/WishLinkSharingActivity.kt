package com.hyeeyoung.wishboard.view.wish.item.screens

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.hyeeyoung.wishboard.R
import com.hyeeyoung.wishboard.databinding.ActivityWishLinkSharingBinding
import com.hyeeyoung.wishboard.model.common.ProcessStatus
import com.hyeeyoung.wishboard.util.NetworkConnection
import com.hyeeyoung.wishboard.util.custom.CustomSnackbar
import com.hyeeyoung.wishboard.view.folder.screens.FolderUploadBottomDialogFragment
import com.hyeeyoung.wishboard.view.noti.screens.NotiSettingBottomDialogFragment
import com.hyeeyoung.wishboard.viewmodel.WishItemRegistrationViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class WishLinkSharingActivity : AppCompatActivity() {
    private val viewModel: WishItemRegistrationViewModel by viewModels()
    private lateinit var binding: ActivityWishLinkSharingBinding
    private var folderAddDialog: FolderUploadBottomDialogFragment? = null

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
        binding.notiInfoContainer.setOnClickListener {
            NotiSettingBottomDialogFragment().show(supportFragmentManager, "NotiSettingDialog")
        }
        binding.newFolder.setOnClickListener {
            folderAddDialog = FolderUploadBottomDialogFragment()
            folderAddDialog?.show(supportFragmentManager, "NewFolderAddDialog")
        }
    }

    private fun addObservers() {
        viewModel.getItemImage().observe(this) { image ->
            if (image == null) return@observe
            Glide.with(this).load(image).into(binding.itemImage)
        }
        // TODO 폴더 추가 완료 observer 추가 및 dialog dismiss()
        viewModel.isCompleteUpload().observe(this) { isComplete ->
            if (isComplete == true) {
                binding.layout.visibility = View.INVISIBLE
                showUploadCompleteSnackbar()
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
        NetworkConnection(this).observe(this) { isConnected ->
            binding.networkView.visibility =
                if (isConnected == true) {
                    View.GONE
                } else {
                    View.VISIBLE
                }
        }
    }

    private fun showUploadCompleteSnackbar() {
        /* callback을 달아준 이유
        문제 : snackbar 띄우고 바로 activity를 finish()하면 activity가 종료되면서 스낵바가 보이지 않음
        해결 : 스낵바가 종료된 후 finish()하도록 callback 추가 */
        CustomSnackbar.make(
            binding.layout,
            getString(R.string.wish_item_registration_snackbar_text),
            false,
            object : BaseTransientBottomBar.BaseCallback<Snackbar>() {
                override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                    super.onDismissed(transientBottomBar, event)
                    finish()
                }
            }
        ).show()
    }

    companion object {
        private const val TAG = "WishLinkSharingActivity"
    }
}