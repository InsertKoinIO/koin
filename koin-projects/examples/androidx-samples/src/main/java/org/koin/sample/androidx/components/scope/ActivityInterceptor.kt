package org.koin.sample.androidx.components.scope

import android.graphics.Color
import android.webkit.WebResourceRequest
import android.webkit.WebView

class ActivityInterceptor(private val viewModel: ActivityViewModel): WebViewInterceptor {

    override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest): Boolean {
        val url = request.url.toString()
        decideOnColor(url)
        return false
    }

    override fun shouldOverrideUrlLoading(view: WebView?, url: String): Boolean {
        decideOnColor(url)
        return false
    }

    private fun decideOnColor(url: String) {
        if (url.contains("/docs/")) {
            viewModel.toolbarColorLiveData.value = Color.MAGENTA
        } else {
            viewModel.toolbarColorLiveData.value = Color.BLUE
        }
    }

}