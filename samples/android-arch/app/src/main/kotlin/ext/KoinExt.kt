package ext

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelStores
import android.support.v4.app.FragmentActivity
import org.koin.KoinContext
import org.koin.dsl.context.Context
import org.koin.error.NoBeanDefFoundException
import org.koin.standalone.KoinComponent
import org.koin.standalone.StandAloneContext

/**
 * Provide a bean definition - alias to provide
 * But do not return Bean definition
 * @param name
 */
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


inline fun <reified T : ViewModel> FragmentActivity.getViewModel(): T {
    val vm = ViewModelProvider(ViewModelStores.of(this), KoinFactory)
    return vm.get(T::class.java)
}

object KoinFactory : ViewModelProvider.Factory, KoinComponent {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        //TODO check is ViewModel
        //TODO assign android instance if AndroidViewModel
        return get(modelClass)
    }
}