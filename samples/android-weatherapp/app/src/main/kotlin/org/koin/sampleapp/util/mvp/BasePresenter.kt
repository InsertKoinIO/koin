package org.koin.sampleapp.util.mvp

interface BasePresenter<T> {

    fun start()

    fun stop()

    var view: T
}