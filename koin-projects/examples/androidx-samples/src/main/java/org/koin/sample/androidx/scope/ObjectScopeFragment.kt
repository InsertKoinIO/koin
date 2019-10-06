package org.koin.sample.androidx.scope

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.koin.androidx.scope.currentScopeInject
import org.koin.sample.android.R
import org.koin.sample.androidx.components.objectscope.Consumer
import org.koin.sample.androidx.components.objectscope.Interceptor
import org.koin.sample.androidx.components.objectscope.InterceptorA
import org.koin.sample.androidx.components.objectscope.InterceptorB
import org.koin.sample.androidx.components.objectscope.InterceptorC

class ObjectScopeFragment: Fragment() {

    /**
     * Just a request for the instance of a Consumer, no parameters
     */
    private val consumer: Consumer by currentScopeInject()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.scope_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val activity = this.activity as ObjectScopeActivity
        assertNotNull(activity.someService)
        assertNotNull(activity.otherService)

        assertNotNull(consumer)
        assertEquals(activity.someService, consumer.someService)
        assertEquals(
                activity,
                consumer.interceptors.find<InterceptorA>().activity
        )
        assertEquals(
                this,
                consumer.interceptors.find<InterceptorB>().fragment
        )
        assertEquals(
                activity.otherService,
                consumer.interceptors.find<InterceptorC>().otherService
        )
    }

}

inline fun <reified T: Interceptor> List<*>.find() = (this.first { it is T } as T)