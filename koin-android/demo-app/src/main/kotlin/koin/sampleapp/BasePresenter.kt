package koin.sampleapp

interface BasePresenter<T> {

    fun start()

    var view: T
}