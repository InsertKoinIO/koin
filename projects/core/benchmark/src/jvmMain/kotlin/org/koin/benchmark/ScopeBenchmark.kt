package org.koin.benchmark

import org.koin.core.Koin
import org.koin.core.component.KoinScopeComponent
import org.koin.core.component.getScopeId
import org.koin.core.component.inject
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.module.Module
import org.koin.dsl.koinApplication
import org.koin.dsl.module
import org.openjdk.jmh.annotations.*
import org.openjdk.jmh.runner.Defaults.WARMUP_ITERATIONS
import java.util.concurrent.*
import java.util.concurrent.TimeUnit
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@State(Scope.Benchmark)
@Fork(1)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
// jvmBenchmark can access declarations from jvmMain!
@Warmup(iterations = WARMUP_ITERATIONS, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 3, time = 1, timeUnit = TimeUnit.SECONDS)
@OptIn(ExperimentalUuidApi::class)
class ScopeBenchmark {

    class FakeActivity()
    class ActivityFactoryClass(val s : MySingle){
        val id : String = Uuid.random().toString()
    }
    class ActivityScopedClass(){
        val id : String = Uuid.random().toString()
    }
    class FakeFragment()
    class FragmentFactoryClass(val act : ActivityFactoryClass){
        val id : String = Uuid.random().toString()
    }
    class MySingle() {
        val id : String = Uuid.random().toString()
    }

    var koin :Koin? = null
    var activityScope: org.koin.core.scope.Scope? = null
    var fragmentScope: org.koin.core.scope.Scope? = null

    @Setup
    fun setUp() {
        koin = koinApplication {
            modules(module {
                single { MySingle() }
                scope<FakeActivity> {
                    factory { ActivityFactoryClass(get()) }
                    factory { ActivityScopedClass() }
                }
                scope<FakeFragment> {
                    factory { FragmentFactoryClass(get()) }
                }
            })
        }.koin

        val activity = FakeActivity()
        val fragment = FakeFragment()

        activityScope = koin!!.createScope<FakeActivity>(activity.getScopeId())
        fragmentScope = koin!!.createScope<FakeFragment>(fragment.getScopeId())
        // fake by fragmentScope -> activityScope
        fragmentScope!!.linkTo(activityScope!!)
    }

    @Benchmark
    fun rootScope() {
        koin!!.get<MySingle>()
    }

    @Benchmark
    fun activityScope() {
        activityScope!!.get<ActivityScopedClass>()
    }

    @Benchmark
    fun activityScope_cascade_root() {
        activityScope!!.get<ActivityFactoryClass>()
    }

    @Benchmark
    fun fragmentScope_cascade_activity_root() {
        fragmentScope!!.get<FragmentFactoryClass>()
    }
}