//package org.koin.core.instance
//
//import org.koin.core.Koin
//import org.koin.core.parameter.ParameterDefinition
//import org.koin.dsl.definition.BeanDefinition
//import java.lang.ref.ReferenceQueue
//import java.lang.ref.SoftReference
//import java.lang.ref.WeakReference
//
//class SharedInstanceHolder<T>(override val bean: BeanDefinition<T>) : InstanceHolder<T> {
//
//    private var reference: SoftReference<T>? = null
//
//    @Suppress("UNCHECKED_CAST")
//    override fun <T> get(parameters: ParameterDefinition): Instance<T> {
//        val checkReference = reference
//        val needCreation =
//            (checkReference == null) || checkReference.isEnqueued || checkReference.get() == null
//        if (needCreation) {
//            Koin.logger.debug("[Shared] create $bean instance")
//            reference = SoftReference(create(parameters), ReferenceQueue())
//        }
//        return Instance(
//            reference?.get() as? T ?: error("SharedInstanceHolder should have instance"),
//            needCreation
//        )
//    }
//
//    override fun release() {
//        Koin.logger.debug("[Shared] clear $bean instance")
//        reference?.enqueue()
//    }
//}