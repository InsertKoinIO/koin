import android.arch.lifecycle.ViewModel
import org.koin.KoinContext
import org.koin.dsl.context.Context
import org.koin.error.NoBeanDefFoundException
import org.koin.standalone.KoinComponent
import org.koin.standalone.StandAloneContext


inline fun <reified T : ViewModel> Context.viewModel(name: String = "", noinline definition: () -> T) {
    val bean = provide(name, false, definition)
    bean.bind(ViewModel::class)
}

fun <T> KoinContext.getByTypeName(canonicalName: String): T {
    val foundDefinitions = beanRegistry.definitions.keys.filter { it.clazz.java.canonicalName == canonicalName }
    return when (foundDefinitions.size) {
        0 -> throw NoBeanDefFoundException("No bean definition found for class name '$canonicalName'")
        1 -> {
            val def = foundDefinitions.first()
            resolveInstance(def.clazz, { def })
        }
        else -> throw NoBeanDefFoundException("Multiple bean definitions found for class name '$canonicalName'")
    }
}

fun <T> KoinComponent.get(modelClass: Class<T>): T = (StandAloneContext.koinContext as KoinContext).getByTypeName(modelClass.canonicalName)