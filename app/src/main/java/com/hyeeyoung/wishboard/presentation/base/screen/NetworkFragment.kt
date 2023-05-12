package com.hyeeyoung.wishboard.presentation.base.screen

import android.os.Bundle
import android.view.View
import androidx.annotation.LayoutRes
import androidx.databinding.ViewDataBinding
import com.hyeeyoung.wishboard.util.BaseFragment
import com.hyeeyoung.wishboard.util.extension.collectFlow
import kotlinx.coroutines.flow.MutableStateFlow

abstract class NetworkFragment<B : ViewDataBinding>(@LayoutRes private val layoutResId: Int) :
    BaseFragment<B>(layoutResId) {
    val isConnected = MutableStateFlow(false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        collectData()
    }

    private fun collectData() {
        val activity = requireActivity() as? NetworkActivity<*> ?: return
        collectFlow(activity.isConnected) {
            isConnected.value = it
        }
    }
}
