package org.koin.sample.view.simple

import org.koin.sample.HelloRepository

/**
 * Simple presenter - use the HelloRepository to "say" hello
 */
class MySimplePresenter(val repo: HelloRepository) {


    fun sayHello() = "${repo.giveHello()} from MySimplePresenter"
}