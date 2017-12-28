package org.koin.sampleapp.util.mvp

interface BaseView<out T : BasePresenter<*>> {

    val presenter: T

}