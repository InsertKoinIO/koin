package org.koin.sample.androidx.components.scope

import android.graphics.Bitmap
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient

class NestedScopeWebViewClient(private val viewModel: FragmentViewModel, private val interceptors: List<WebViewInterceptor>): WebViewClient() {

    override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
        for (interceptor in interceptors) {
            if (interceptor.shouldOverrideUrlLoading(view, request)) {
                return true
            }
        }
        return super.shouldOverrideUrlLoading(view, request)
    }

    override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
        for (interceptor in interceptors) {
            if (interceptor.shouldOverrideUrlLoading(view, url)) {
                return true
            }
        }
        return super.shouldOverrideUrlLoading(view, url)
    }

    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
        super.onPageStarted(view, url, favicon)
        viewModel.isLoading.value = true
    }

    override fun onPageFinished(view: WebView?, url: String?) {
        super.onPageFinished(view, url)
        viewModel.isLoading.value = false
    }
}

class NestedScopeChromeClient(private val viewModel: FragmentViewModel): WebChromeClient() {
    override fun onReceivedTitle(view: WebView?, title: String?) {
        super.onReceivedTitle(view, title)
        viewModel.currentTitle.value = title
    }
}