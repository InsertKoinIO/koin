package org.koin.sampleapp.util.mvp

interface BasePresenter<T> {

    fun stop()

    var view: T
}