package ext

import android.arch.lifecycle.ViewModel
import org.koin.dsl.context.Context

/**
 * Provide a bean definition - alias to provide
 * But do not return Bean definition
 * @param name
 */
inline fun <reified T : Any> Context.viewModel(name: String = "", noinline definition: () -> T) {
    val bean = provide(name, true, definition)
    bean.bind(ViewModel::class)
}