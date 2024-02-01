package org.koin.sample.sandbox.main

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import androidx.lifecycle.findViewTreeViewModelStoreOwner
import org.koin.androidx.viewmodel.ext.android.lazyViewModelForClass
import org.koin.sample.sandbox.components.mvvm.SimpleViewModel

class MainCustomView(
    context: Context,
    attrs: AttributeSet? = null,
) : AppCompatTextView(context, attrs) {

    private val viewModel: SimpleViewModel by lazyViewModelForClass(viewModelStoreOwnerLazy = { findViewTreeViewModelStoreOwner()!! })

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        text = viewModel.id
    }
}