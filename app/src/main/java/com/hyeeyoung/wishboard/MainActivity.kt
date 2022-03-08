package com.hyeeyoung.wishboard

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.hyeeyoung.wishboard.databinding.ActivityMainBinding
import com.hyeeyoung.wishboard.util.NetworkConnection
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
                    R.id.wish_fragment,
                    R.id.wish_item_detail_fragment,
                    R.id.folder_list_fragment,
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
            if (isConnected == false) {
                Toast.makeText(
                    this,
                    getString(R.string.check_network_connection_toast_text),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}