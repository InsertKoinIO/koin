package org.koin.sampleapp.mvp

interface BaseView<out T : BasePresenter<*>> {

    val presenter: T

}