package org.koin.sampleapp.mvp

interface BasePresenter<T> {

    fun start()

    fun stop()

    var view: T
}