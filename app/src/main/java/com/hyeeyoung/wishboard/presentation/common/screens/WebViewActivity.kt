package com.hyeeyoung.wishboard.presentation.common.screens

import android.os.Bundle
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.hyeeyoung.wishboard.R
import com.hyeeyoung.wishboard.databinding.ActivityWebviewBinding

class WebViewActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWebviewBinding
    private lateinit var webView: WebView
    private var link: String? = null
    private var title: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_webview)

        link = intent.getStringExtra(ARG_WEB_VIEW_LINK)
        title = intent.getStringExtra(ARG_WEB_VIEW_TITLE)

        initializeView()
    }

    private fun initializeView() {
        binding.title.text = title
        webView = binding.webview.apply {
            // 새 창 띄우지 않기
            webViewClient = WebViewClient()
            webChromeClient = WebChromeClient() // 크롬환경에 맞는 세팅을 해줌

            settings.javaScriptEnabled = true
            settings.javaScriptCanOpenWindowsAutomatically = true
            settings.setSupportZoom(true)

            // WebView 화면크기에 맞추도록 설정
            settings.loadWithOverviewMode = true
            settings.useWideViewPort = true

            link?.let {
                loadUrl(it)
            }
        }

        binding.back.setOnClickListener {
            finish()
        }
    }

    companion object {
        private const val TAG = "WebViewActivity"
        private const val ARG_WEB_VIEW_LINK = "link"
        private const val ARG_WEB_VIEW_TITLE = "title"
    }
}