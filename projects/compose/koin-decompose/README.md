## Koin SubComponents

Provide the mechanism to create subcomponents in Koin, similar to `dagger-android` approach. 

- Similar approach to `dagger-android` based on subcomponent
- Load/Unload modules automatically when composable component is created/disposed
- Link `Scopes` from leaf children to root navigation stack

`KoinSubComponent` is the composable that will take care of linking Scopes to parent components. Every time each of them is instantiated a new scope is created, and their related modules are loaded. Exactly the same concept as `dagger-android` did with `@ContributesAndroidInjector`, `Activities` and `Fragments` declared a subcomponent where all the  installed dependencies' modules would be provided from. Here, the modules will be installed when calling `KoinSubComponent<ScopeName>(subComponentModules)`

## Koin Decompose

The goal is to provide an easy integration with Decompose for complex navigation hierarchies and large amount of dependencies.
For that, `NavigationStackComponentContext` will declare at runtime a `StackNavigation` instance with the given qualifier, which is the `StackConfig` type.  

### Components hierarchy
This is an example of the hierarchy. In the left part, there is a tree that represents the composables components that holds the scopes containers of the dependencies. In the right part there is the relationship between each scope.

Everytime a Stack is instantiated, a new Scope is created within that stack. It also takes the parent scope, and links this new Scope to the parent Scope, so if a dependency is needed, it's module definition will be searched from this Scope to root scope.

#### Example
``` kotlin
                                           ┌─────────────┐
        AppStackComponent -----------------│  AppScope   │ <- modules { AppViewModel, AppRouter } 
      __________|__________                └──────^──────┘
      |         |         |                ┌──────┴──────┐
SplashComponent |---------|----------------│ SplashScope │ <- modules { SplashViewModel, AppRouter }
        HomeComponent ----|----------------│ HomeScope   │ <- modules { HomeViewModel, AppRouter }
                AuthStackComponent --------│ AuthScope   │ <- modules { AuthRouter, AppRouter }
                    ______|________        └──────^──────┘
                    |     |       |        ┌──────┴──────┐
        SignInComponent --|----------------│ SignInScope │ <- modules { SignInViewModel, AuthRouter }
                SignUpComponent --|--------│ SignUpScope │ <- modules { SignUpViewModel, AuthRouter }
                        SuccessComponent --│ SuccessScope│ <- modules { SuccessViewModel, AppRouter }
                                           └─────────────┘
```

### Usage with an example
The following example is just a small part of the diagram above.

#### Main Activity on Android
``` kotlin
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setContentComponentContext {
            App()
        }
    }
}
```

#### App Components
``` kotlin
@Composable
fun App(
    modifier: Modifier = Modifier,
) = KoinApplicationComponent(
    application = { modules(appModule) },
) {
    AppStackComponent(
        initialState = AppStackConfig.Splash,
    )
}

```

##### Navigation Stack

``` kotlin
@Composable
fun AppStackComponent(
    initialState: AppStackConfig
    // Declares at runtime a [StackNavigation] intance with
    // 'AppStackConfig' qualifier, in 'AppScope' scope
) = NavigationStackComponentContext<AppStackConfig, AppScope>(
    initialConfiguration = initialState,
    childProvider = { _ -> this.toChild(send) }
)

private fun AppStackConfig.toChild() : Child = when (this) {
    Splash -> DefaultChild { SplashComponent() }
    Home -> DefaultChild { Home() }
}
```
##### Navigation Configuration
``` kotlin
@Serializable
sealed class AppStackConfig {
    @Serializable
    data object Splash : AppStackConfig()
    @Serializable
    data object Home : AppStackConfig()
}
```
##### Koin 
``` kotlin
val appModule = module {
    includes(
        ...
    )
}
```

##### Koin Scope
``` kotlin
class AppScope
```
---------------

#### Splash Component
``` kotlin
@Composable
fun SplashComponent() = KoinSubComponent<SplashScope>(
    // Load dependencieds when needed in current scope
    subComponentModule = splashModule
) {
    val splashRT : SplashRT = inject()

    SplashScreen(
        splashVM = inject(),
        onAnimationCompleted = { splashRT.goToHome() }
    )
}
```

##### Splash Module 
``` kotlin
val splashModule = module {
    includes(
        splashViewModelModule,
        splashRouterModule
    )
}
```

##### Splash View Model
``` kotlin
class SplashVM
```

##### Splash View Model Module
``` kotlin
val splashViewModelModule = module {
    scope<AppScope> {
        // Viewmodel is scoped intentionally
        scoped { SplashViewModel() }
    }
}
```

##### Splash Router
``` kotlin
class SplashRouter(
    private val appStackNavigator: StackNavigation<AppStackConfig>
) {

    fun goToHome() {
        appStackNavigator.replaceCurrent(AppStackConfig.Home)
    }
}
```


##### Splash Router Module
``` kotlin
val splashRouterModule = module {
    scope<AppScope> { 
        factory {
            SplashRouter(
                // [appStackNavigator : StackNavigator] is available in this 'AppScope'
                // (thanks to NavigationStackComponentContext)
                // If a navigator from a different parent stack is needed, just use 'named<ParentStackConfig>'
                appStackNavigator = get(named<AppStackConfig>())
            )
        }
    }
}
```