package com.hyeeyoung.wishboard.presentation.main

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.hyeeyoung.wishboard.R
import com.hyeeyoung.wishboard.databinding.ActivityMainBinding
import com.hyeeyoung.wishboard.presentation.base.screen.NetworkActivity
import com.hyeeyoung.wishboard.presentation.common.screens.TwoButtonDialogFragment
import com.hyeeyoung.wishboard.presentation.common.types.DialogButtonReplyType
import com.hyeeyoung.wishboard.presentation.howtouse.screens.HowToUseActivity
import com.hyeeyoung.wishboard.util.DialogListener
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : NetworkActivity<ActivityMainBinding>(R.layout.activity_main) {
    private val viewModel: MainViewModel by viewModels()
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setActivityResultLauncher()

        // 회원가입 성공 시 알림 설정 다이얼로그 띄우기
        intent.getBooleanExtra(ARG_SUCCESS_SIGN_UP, false).let {
            if (it) showHowToUseDialog()
        }

        initializeView()
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
                    R.id.profile_edit_fragment,
                    R.id.password_change_fragment
                    -> binding.bottomNav.visibility = View.GONE
                    else -> binding.bottomNav.visibility = View.VISIBLE
                }
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

    /** 앱 이용방법 다이얼로그 */
    private fun showHowToUseDialog() {
        activityResultLauncher.launch(Intent(this, HowToUseActivity::class.java))
    }

    private fun setActivityResultLauncher() {
        activityResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode != RESULT_OK) return@registerForActivityResult
                showPushStateSettingDialog()
            }
    }

    companion object {
        private const val ARG_SUCCESS_SIGN_UP = "successSignup"
    }
}