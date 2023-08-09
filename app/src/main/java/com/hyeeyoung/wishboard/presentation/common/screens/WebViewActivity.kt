package com.hyeeyoung.wishboard.presentation.common.screens

import android.annotation.SuppressLint
import android.os.Bundle
import android.webkit.WebChromeClient
import android.webkit.WebViewClient
import com.hyeeyoung.wishboard.R
import com.hyeeyoung.wishboard.databinding.ActivityWebviewBinding
import com.hyeeyoung.wishboard.util.BaseActivity

class WebViewActivity : BaseActivity<ActivityWebviewBinding>(R.layout.activity_webview) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initializeView()
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initializeView() {
        binding.topAppBar.title = intent.getStringExtra(ARG_WEB_VIEW_TITLE)

        binding.webview.apply {
            webViewClient = WebViewClient()
            webChromeClient = WebChromeClient()

            settings.apply {
                javaScriptEnabled = true
                javaScriptCanOpenWindowsAutomatically = true
                loadWithOverviewMode = true
                useWideViewPort = true
                domStorageEnabled = true
                builtInZoomControls= true
                displayZoomControls = false
                setSupportZoom(true)
            }

            intent.getStringExtra(ARG_WEB_VIEW_LINK)?.let { loadUrl(it) }
        }

        binding.topAppBar.back.setOnClickListener {
            finish()
        }
    }

    companion object {
        private const val ARG_WEB_VIEW_LINK = "link"
        private const val ARG_WEB_VIEW_TITLE = "title"
    }
}