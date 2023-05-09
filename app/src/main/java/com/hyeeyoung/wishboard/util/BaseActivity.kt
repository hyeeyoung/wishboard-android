package com.hyeeyoung.wishboard.util

import android.os.Bundle
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import com.hyeeyoung.wishboard.R
import com.hyeeyoung.wishboard.util.custom.CustomSnackbar
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

abstract class BaseActivity<B : ViewDataBinding>(@LayoutRes private val layoutRes: Int) :
    AppCompatActivity() {
    lateinit var binding: B
    private var snackbar: CustomSnackbar? = null
    val isConnected = MutableStateFlow(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, layoutRes)
        snackbar = CustomSnackbar.make(
            binding.root,
            getString(R.string.check_network_connection_snackbar_text),
            duration = Snackbar.LENGTH_INDEFINITE
        )

        collectNetworkState()
    }

    private fun collectNetworkState() {
        NetworkMonitor(this, lifecycleScope).isConnected.flowWithLifecycle(lifecycle)
            .onEach { isConnected ->
                if (isConnected) snackbar?.dismiss()
                else snackbar?.show()
                this.isConnected.value = isConnected
            }.launchIn(lifecycleScope)
    }
}
