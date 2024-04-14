package org.koin.decompose.navigation

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.arkivanov.decompose.extensions.compose.stack.animation.StackAnimation
import com.arkivanov.decompose.extensions.compose.stack.animation.fade
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.value.Value
import kotlinx.serialization.KSerializer
import kotlinx.serialization.serializer
import org.koin.decompose.context.LocalComponentContext
import kotlin.random.Random

val defaultAnimation = fade(
    animationSpec = tween(
        durationMillis = 500,
        easing = FastOutSlowInEasing
    )
)

/**
 * Creates a navigation stack from a [ComponentContext]
 *
 * @param initialConfiguration the initial configuration of the stack
 * @param toChildRenderable a lambda that converts a configuration to a [Child]
 * @param animation the animator to be used for the stack
 *
 * @author Antonio Vicente
 */
inline fun <reified T : Any> ComponentContext.navigationStack(
    initialConfiguration: T,
    noinline toChildRenderable: T.(StackNavigation<T>) -> Child,
    animation: StackAnimation<T, Child>?
) : ChildStackComponentContext<T> = ChildStackComponentContext(
    parent = this,
    initialConfiguration = initialConfiguration,
    serializer = serializer(),
    toChildRenderable = { navigation -> toChildRenderable(navigation) },
    animation = animation
)

/**
 * A [Child] implementation that represents a stack of children
 *
 * @param parent the parent [ComponentContext]
 * @param initialConfiguration the initial configuration of the stack
 * @param serializer the serializer to be used for the configuration
 * @param toChildRenderable a lambda that converts a configuration to a [Child]
 * @param animation the animator to be used for the stack
 *
 * @author Antonio Vicente
 */
open class ChildStackComponentContext<T : Any>(
    parent: ComponentContext,
    initialConfiguration : T,
    serializer: KSerializer<T>,
    toChildRenderable : T.(StackNavigation<T>) -> Child,
    private val animation: StackAnimation<T, Child>?
) : Child, ComponentContext by parent {

    val navigation : StackNavigation<T> = StackNavigation()

    private val stack: Value<ChildStack<T, Child>> by lazy {
        childStack(
            source = navigation,
            key = Random.nextInt().toString(),
            serializer = serializer,
            handleBackButton = true,
            initialConfiguration = initialConfiguration,
            childFactory = { config, cc -> config.toChildRenderable(navigation) }
        )
    }

    @Composable
    override fun render() = Children(
        stack = stack,
        animation = animation,
        content = {
            CompositionLocalProvider(
                LocalComponentContext provides this,
            ) {
                it.instance.render()
            }
        }
    )

}