package com.hyeeyoung.wishboard.view

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.hyeeyoung.wishboard.R
import com.hyeeyoung.wishboard.databinding.ActivityMainBinding
import com.hyeeyoung.wishboard.model.common.DialogButtonReplyType
import com.hyeeyoung.wishboard.util.NetworkConnection
import com.hyeeyoung.wishboard.view.common.screens.DialogListener
import com.hyeeyoung.wishboard.view.common.screens.TwoButtonDialogFragment
import com.hyeeyoung.wishboard.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        viewModel.initFCMToken()

        // 회원가입 성공 시 알림 설정 다이얼로그 띄우기
        intent.getBooleanExtra(ARG_SUCCESS_SIGN_UP, false).let {
            if (it) {
                showPushStateSettingDialog()
            }
        }

        initializeView()
        addObservers()
    }

    private fun initializeView() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_container) as NavHostFragment
        navHostFragment.let {
            binding.bottomNav.setupWithNavController(it.navController)
            it.navController.addOnDestinationChangedListener { _, destination, _ ->
                when (destination.id) {
                    R.id.cart_fragment,
                    R.id.noti_calendar_fragment,
                    R.id.wish_fragment,
                    R.id.wish_item_detail_fragment,
                    R.id.gallery_image_fragment,
                    R.id.profile_edit_fragment
                    -> binding.bottomNav.visibility = View.GONE
                    else -> binding.bottomNav.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun addObservers() {
        NetworkConnection(this).observe(this) { isConnected ->
            binding.networkView.visibility =
                if (isConnected == true) {
                    View.GONE
                } else {
                    View.VISIBLE
                }
        }
    }

    /** 푸시 알림 설정 다이얼로그 */
    private fun showPushStateSettingDialog() {
        val dialog = TwoButtonDialogFragment(
            getString(R.string.noti_push_state_allow),
            getString(R.string.noti_push_state_setting_dialog_description),
            getString(R.string.allow),
            getString(R.string.later)
        ).apply {
            setListener(object : DialogListener {
                override fun onButtonClicked(clicked: String) {
                    if (clicked == DialogButtonReplyType.YES.name) {
                        viewModel.updatePushState()
                    }
                    dismiss()
                }
            })
        }
        dialog.show(supportFragmentManager, "FolderDeleteDialog")
    }

    companion object {
        private const val TAG = "MainActivity"
        private const val ARG_SUCCESS_SIGN_UP = "successSignup"
    }
}