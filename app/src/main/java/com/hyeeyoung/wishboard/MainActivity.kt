package com.hyeeyoung.wishboard

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.hyeeyoung.wishboard.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        init()
    }

    private fun init() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_container) as NavHostFragment
        navHostFragment.let {
            binding.bottomNav.setupWithNavController(it.navController)
            it.navController.addOnDestinationChangedListener { _, destination, _ ->
                when (destination.id) {
                    R.id.cartFragment,
                    R.id.wishFragment,
                    R.id.wishItemDetailFragment,
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