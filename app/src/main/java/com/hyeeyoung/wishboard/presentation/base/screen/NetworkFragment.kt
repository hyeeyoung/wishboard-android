package com.hyeeyoung.wishboard.presentation.base.screen

import android.os.Bundle
import android.view.View
import androidx.annotation.LayoutRes
import androidx.databinding.ViewDataBinding
import com.hyeeyoung.wishboard.presentation.base.viewmodel.NetworkViewModel
import com.hyeeyoung.wishboard.util.BaseActivity
import com.hyeeyoung.wishboard.util.BaseFragment
import com.hyeeyoung.wishboard.util.extension.collectFlow

abstract class NetworkFragment<B : ViewDataBinding>(@LayoutRes private val layoutResId: Int) :
    BaseFragment<B>(layoutResId) {
    abstract val viewModel: NetworkViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        collectData()
    }

    private fun collectData() {
        val activity = requireActivity() as? BaseActivity<*> ?: return
        collectFlow(activity.isConnected) {
            viewModel.setConnected(it)
        }
    }
}
