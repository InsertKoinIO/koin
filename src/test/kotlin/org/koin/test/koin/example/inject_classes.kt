package org.koin.test.koin.example

import org.koin.test.ServiceB
import org.koin.test.ServiceC
import javax.inject.Inject

/**
 * Created by arnaud on 31/05/2017.
 */

class MyClass {
    @Inject
    lateinit var serviceB: ServiceB
}

class MyOtherClass {
    @Inject
    lateinit var serviceB: ServiceB
    @Inject
    lateinit var serviceC: ServiceC
}