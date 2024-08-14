//package org.koin.androidx.workmanager
//
//import android.content.Context
//import androidx.work.ListenableWorker
//import androidx.work.WorkerParameters
//import com.google.common.util.concurrent.ListenableFuture
//
///**
// * @author Fabio de Matos
// * @since 21/08/2020.
// */
//open class MyListenableWorker1(context: Context, val workerParams: WorkerParameters) :
//    ListenableWorker(context, workerParams) {
//    override fun startWork(): ListenableFuture<Result> {
//        TODO("Not yet implemented")
//    }
//
//}
//
//
//class MyListenableWorker2(
//    context: Context,
//    workerParams: WorkerParameters,
//    val dummyPayload: DummyPayload
//) :
//    MyListenableWorker1(context, workerParams) {
//    override fun startWork(): ListenableFuture<Result> {
//        TODO("Not yet implemented")
//    }
//
//}
//
//
//data class DummyPayload(val id: String)
//
//
