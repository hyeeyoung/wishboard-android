package com.hyeeyoung.wishboard

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.hyeeyoung.wishboard.databinding.ActivityMainBinding
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

    companion object {
        private const val TAG = "MainActivity"
    }
}