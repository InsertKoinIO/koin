package koin.sampleapp

interface BasePresenter<T> {

    fun start()

    fun stop()

    var view: T
}