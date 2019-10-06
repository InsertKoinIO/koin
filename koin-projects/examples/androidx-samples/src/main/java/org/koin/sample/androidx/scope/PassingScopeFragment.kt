package org.koin.sample.androidx.scope

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.koin.androidx.scope.currentScope
import org.koin.core.parameter.parametersOf
import org.koin.core.scope.HasParentScope
import org.koin.core.scope.ScopeID
import org.koin.sample.android.R
import org.koin.sample.androidx.components.objectscope.Consumer
import org.koin.sample.androidx.components.objectscope.InterceptorA
import org.koin.sample.androidx.components.objectscope.InterceptorB
import org.koin.sample.androidx.components.objectscope.InterceptorC
import org.koin.sample.androidx.components.objectscope.SomeService
import org.koin.sample.androidx.components.objectscope.OtherService

class PassingScopeFragment: Fragment(), HasParentScope {
    /**
     * Simulate behavior of koin 2.0.1
     */
    override val parentScopeId: ScopeID
        get() = "-Root-"

    /**
     * The injection site has to provide everything as parameters as Koin can not resolve
     * the activity scoped dependencies on its own.
     */
    private val consumer: Consumer by lazy { currentScope.get<Consumer>(parameters = {
        parametersOf(
                this,
                activity!!.currentScope.get<SomeService>(parameters = { parametersOf(this.activity!!) }),
                activity!!.currentScope.get<OtherService>(parameters = { parametersOf(this.activity!!) })
        )
    })}

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.scope_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val activity = this.activity as PassingScopeActivity
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