package com.hyeeyoung.wishboard

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.hyeeyoung.wishboard.databinding.ActivityMainBinding
import com.hyeeyoung.wishboard.util.prefs
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        initFCMToken()
        initializeView()
    }

    private fun initializeView() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_container) as NavHostFragment
        navHostFragment.let {
            binding.bottomNav.setupWithNavController(it.navController)
            it.navController.addOnDestinationChangedListener { _, destination, _ ->
                when (destination.id) {
                    R.id.cartFragment,
                    R.id.wishFragment,
                    R.id.wishItemDetailFragment,
                    R.id.folderListFragment,
                    R.id.galleryImageFragment
                    -> binding.bottomNav.visibility = View.GONE
                    else -> binding.bottomNav.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun initFCMToken() {
        if (prefs?.getFCMToken() == null) {
            FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                    return@OnCompleteListener
                }
                val token = task.result
                Log.d(TAG, token)
                prefs?.setFCMToken(token)
            })
        }
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}