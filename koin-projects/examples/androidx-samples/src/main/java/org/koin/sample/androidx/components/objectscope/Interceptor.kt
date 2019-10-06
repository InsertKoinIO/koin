package org.koin.sample.androidx.components.objectscope

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity

abstract class Interceptor
class InterceptorA(val activity: FragmentActivity): Interceptor()
class InterceptorB(val fragment: Fragment): Interceptor()
class InterceptorC(val otherService: OtherService): Interceptor()