package org.koin.sample.androidx.components.scope

import android.webkit.WebResourceRequest
import android.webkit.WebView

interface WebViewInterceptor {
    fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest): Boolean
    fun shouldOverrideUrlLoading(view: WebView?, url: String): Boolean

}