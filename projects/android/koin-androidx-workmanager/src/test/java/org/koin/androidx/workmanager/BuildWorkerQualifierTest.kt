package org.koin.androidx.workmanager

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerParameters
import com.google.common.util.concurrent.ListenableFuture
import org.junit.Assert.assertEquals
import org.junit.Test
import org.koin.androidx.workmanager.dsl.worker
import org.koin.core.annotation.KoinInternalApi
import org.koin.core.qualifier.named
import org.koin.dsl.module
import org.koin.plugin.module.dsl.buildWorker

// Compiler plugin emits buildWorker(T::class, null). That null must become the class qualifier,
// matching worker { }, so KoinWorkerFactory can look the worker up by class name.
@OptIn(KoinInternalApi::class)
class BuildWorkerQualifierTest {

    class ScopeOwner

    class SyncWorker(context: Context, params: WorkerParameters) : ListenableWorker(context, params) {
        override fun startWork(): ListenableFuture<Result> = error("not used")
    }

    @Test
    fun build_worker_is_indexed_like_classic_worker() {
        val plugin = module { buildWorker(SyncWorker::class, null) { error("not used") } }
        val classic = module { worker<SyncWorker> { error("not used") } }

        assertEquals(classic.mappings.keys, plugin.mappings.keys)
    }

    @Test
    fun explicit_qualifier_is_kept() {
        val plugin = module { buildWorker(SyncWorker::class, named("custom")) { error("not used") } }
        val classic = module { worker<SyncWorker>(named("custom")) { error("not used") } }

        assertEquals(classic.mappings.keys, plugin.mappings.keys)
    }

    @Test
    fun scoped_build_worker_is_indexed_like_classic_worker() {
        val plugin = module { scope<ScopeOwner> { buildWorker(SyncWorker::class, null) { error("not used") } } }
        val classic = module { scope<ScopeOwner> { worker<SyncWorker> { error("not used") } } }

        assertEquals(classic.mappings.keys, plugin.mappings.keys)
    }
}
