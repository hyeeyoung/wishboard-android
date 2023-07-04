package com.hyeeyoung.wishboard.presentation.base.screen

import android.os.Bundle
import androidx.annotation.LayoutRes
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import com.hyeeyoung.wishboard.R
import com.hyeeyoung.wishboard.util.BaseActivity
import com.hyeeyoung.wishboard.util.NetworkMonitor
import com.hyeeyoung.wishboard.designsystem.component.CustomSnackbar
import com.hyeeyoung.wishboard.util.extension.collectFlow
import kotlinx.coroutines.flow.MutableStateFlow

abstract class NetworkActivity<B : ViewDataBinding>(@LayoutRes private val layoutRes: Int) :
    BaseActivity<B>(layoutRes) {
    private var snackbar: CustomSnackbar? = null
    val isConnected = MutableStateFlow(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        snackbar = CustomSnackbar.make(
            binding.root,
            getString(R.string.check_network_connection_snackbar_text),
            duration = Snackbar.LENGTH_INDEFINITE
        )

        collectNetworkState()
    }

    private fun collectNetworkState() {
        collectFlow(NetworkMonitor(this, lifecycleScope).isConnected) { isConnected ->
            if (isConnected) snackbar?.dismiss()
            else snackbar?.show()
            this.isConnected.value = isConnected
        }
    }
}
