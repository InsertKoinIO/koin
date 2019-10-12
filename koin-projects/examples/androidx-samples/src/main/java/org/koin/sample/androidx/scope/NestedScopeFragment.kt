package org.koin.sample.androidx.scope

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.nestedscope_fragment.*
import org.koin.androidx.scope.currentScope
import org.koin.androidx.scope.currentScopeInject
import org.koin.androidx.viewmodel.ext.android.currentScopeViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.sample.android.R
import org.koin.sample.androidx.components.scope.FragmentViewModel

class NestedScopeFragment: Fragment() {

    private val webClient: WebViewClient by currentScopeInject()
    private val chromeClient: WebChromeClient by currentScopeInject()

    private val viewModel: FragmentViewModel by currentScopeViewModel()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        currentScope.declare(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.nestedscope_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        with(nestedscope_webview) {
            webViewClient = webClient
            webChromeClient = chromeClient
            settings.javaScriptEnabled = true
            loadUrl("https://insert-koin.io/")
        }

        viewModel.title.observe(viewLifecycleOwner, Observer { title ->
            nestedscope_title.text = "Current Title is:\n${title}"
        })
        viewModel.isLoading.observe(viewLifecycleOwner, Observer { show ->
            loading.visibility = if(show) View.VISIBLE else View.INVISIBLE
        })
    }
}