package org.koin.core.definition

data class Callbacks<T>(val onClose: OnCloseCallback<T>? = null)

typealias OnCloseCallback<T> = (T?) -> Unit