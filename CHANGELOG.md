# Change Log

Badges: `[UPDATED]`, `[FIXED]`, `[NEW]`, `[DEPRECATED]`, `[REMOVED]`,  `[BREAKING]`


## [3.1.5]()
* `[FIXED]` `[android]` - #1240 - ViewModel scope instance creation fixed
* `[FIXED]` `[android]` - #1232 & #1207 - Android minimum dependencies is clean up and should avoid indirect library crash
* `[ADDED]` `[android]` - #1250 - Android instrumented test doc
* `[FIXED]` `[core]` - #1213 - withInstance() mock instance fixed
* `[FIXED]` `[core]` - #1248 - fix createeagerInstances() to be seperated back of modules()
* `[FIXED]` `[core]` - checkKoinModules API is fixed / deprecate old signatures
* `[FIXED]` `[androidx-compose]` - ViewModelStoreOwner property fixed for Compose
* `[UPDATED]` `[androidx-compose]` - Compose 1.0.5
* `[FIXED]` `[android]` - fixed scope delegate property to help create scope later for fragment, and bind with parent activity scope 

## [3.1.4]()

* `[FIXED]` `[core]` - #1149 - Nullable parameter resolution
* `[FIXED]` `[docs]` - Documentation fixes #1170 #1160 #1152 #1155 #1169 #1231 #1234 #1222 #1079
* `[FIXED]` `[core]` - Bean scope error message fix #1166
* `[FIXED]` `[core]` - Java Compat nullable API fix #1175
* `[FIXED]` `[core]` - String quotes fix #1199
* `[FIXED]` `[android]` - Koin Graph ViewModel added parameters API #1202
* `[FIXED]` `[core]` - Dynamic feature module loading #1095
* `[FIXED]` `[kmm]` - Apple Silicon Support #1192
* `[FIXED]` `[core]` - backport getSource scope API as deprecated fix #1211
* `[FIXED]` `[test]` - CheckKoinModules & CheckModules API cleaned up & fixed with right parameters #1197 #1194
* `[FIXED]` `[android]` - stateViewModel API rolledback #1214


## [3.1.3]()

* `[UPDATED]` `[core]` - Update to Kotlin 1.5.30
* `[UPDATED]` `[android]` - Update to latest android ktx
* `[UPDATED]` `[android-compose]` - update to jetpack compose 1.0.3
* `[FIXED]` `[android]` - ViewModel instances are now reinjected into Koin Android scopes
* `[FIXED]` `[android]` - Activity/Fragment are now available in their own scopes (no need of explicit inject from the source)
* `[ADDED]` `[android]` - new koin-androidx-navigation package, offering `by koinNavGraphViewModel()` to allow scope a ViewModel for a given navigation graph - https://insert-koin.io/docs/reference/koin-android/viewmodel#navigation-graph-viewmodel-updated-in-313
* `[DEPRECATED]` `[android]` - `by stateViewModel()` is deprecated. `state` parameter is not needed anymore. It's now merged into `viewModel()`, to inject SavedStateHandle
* `[FIXED]` `[android]` - Fix from Koin 2.x. ViewModel API get back the `owner` property, to allow specify from where we are instanciating ViewModel.
* `[ADDED]` `[test]` - Introduce new `checkKoinModules` and DSL to help verify modules - https://insert-koin.io/docs/reference/koin-test/checkmodules
* `[DEPRECATED]` `[test]` - deprecate old `checkModules` function, in favor of new API `checkKoinModules`

## [3.1.2]()

* `[FIXED]` `[core]` - `createeagerInstances()` is fixed back. It's seperated from module loading process.
* `[FIXED]` `[core]` - `ParameterHolder.getOrNull` is now checking assignable type


## [3.1.1]()

* `[FIXED]` `[core]` - injection parameter resolved in graph
* `[FIXED]` `[core]` - parameter injection is not deprecated
* `[FIXED]` `[test]` - checkModules API is reverted
* `[UPDATED]` `[core]` - Lazy eval logger operator Logger.log(lvl, message)
* `[UPDATED]` `[core]` - ParametersHolder getOrNull<T> added
* `[UPDATED]` `[androidx-compose]` - update to Beta-09

## [3.1.0]()

* `[NEW]` `[core]` - Improved resolution engine with new Module/DSL/Index creation to make faster Index/InstanceFactory allocation at start. Reworked internal architecture for Factory & scope allocations. Should improve greatly startup performances!
* `[NEW]` `[core-jvm]` - Smarter DSL is now stable, no more `get()` needed. Unlock `single<MyType>()` builder, for any kind of definition.
* `[BREAKING]` `[-ext]` - builder extension are now part of core API
* `[UPDATED]` `[core]` - Kotlin 1.5.10
* `[UPDATED]` `[ktor]` - Ktor 1.6.0
* `[UPDATED]` `[android-compose]` - Compose 1.0.0-Beta08
* `[FIXED]` `[core]` - String property import - https://github.com/InsertKoinIO/koin/issues/1099
* `[FIXED]` `[test]` - KoinTestRule close existing Koin isntance before trying to start a new one
* `[FIXED]` `[core]` - ScopeJVM.kt bad package - https://github.com/InsertKoinIO/koin/issues/1094
* `[FIXED]` `[android]` - Fix Fragment Scope Lifecycle delegate - https://github.com/InsertKoinIO/koin/issues/1101
* `[DEPRECATED]` `[core]` - Module `override` is now a global option into `KoinApplication`, not a local option to a module. Override strategy is now a global option.
* `[DEPRECATED]` `[core]` - injection parameters as `destructured` declaration are deprecated. Instead of writing ``single { (myParam) -> MyClass(param) }`` now use injected parameters with get: `single { params -> MyClass(params.get()) }`


## [2.2.3]()

* Update Kotlin 1.5.0
* Clean up Time API

## [3.0.2]()

* Update Kotlin 1.5.0
* Update Ktor 1.5.4
* Clean up Time API - 64ceaae94f1084eb708142a6b9fd029390e20714
* Fix Property import quote replace - 94ceed397b10d08e17880a9a0e2ac87d0a272a18
* Fix Definition Param internal values - 067b9d95847b74a6c9b88058a649b27266053013
* KClass for new instance builder - #1093 Erik@ErikSchouten.com
* WatchOS64 native target - #1091 email@robertdewilde.nl
* Enable IR for JS - #1067 subroh.0508@gmail.com

## [3.0.1]()

### [3.0.1-beta-2]()

* Koin Gradle Plugin is back
* Fix back Ktor feature starter

### [3.0.1-beta-1]()

* Update Platform tools for default Lazy Mode - https://github.com/InsertKoinIO/koin/pull/1031
* Relax Typing resolution constraints - https://github.com/InsertKoinIO/koin/pull/1032
* Update Ktor 1.5.2
* Injection parameters doc update
* Koin Native KClass in hashcode naming - https://github.com/InsertKoinIO/koin/pull/1036
* Jetpack compose Beta01 - https://github.com/InsertKoinIO/koin/pull/1037
* Fix getAll operator, to look in linked scopes - https://github.com/InsertKoinIO/koin/pull/1038
* Ktor check for existing KoinApplication ready - https://github.com/InsertKoinIO/koin/pull/1023
* Work manager resolution fix - https://github.com/InsertKoinIO/koin/pull/1018 
* Koin Gradle Plugin drop for now (many issues for Android side)

### [3.0.1-beta-6]()

* Maven central publication scripts

### [3.0.1-alpha-3]()

* Merge branch 2.2.x & 3.0.0
* New folder modules/strategy
* Merge of main Android modules (koin-androidx-scope, koin-androidx-viewmodel are merged in koin-android)
* Update Gradle dependencies to use proper `api` / `implementation`
* Refresh Scope API

## [3.0.0]()

* first attempt of Koin Multiplatform version

## [2.2.2]()

* `[ADDED]` `[android]` FragmentScope class is now linked to its parent ActivityScope class
* `[FIXED]` `[core]` fixed back to handle any type of property (not only String). File properties are saved as Strings. 
* `[FIXED]` `[android]` ViewModel factory use DefaultViewModelFactory by default, StateViewModelFactory if `state` parameter is used.


## [2.2.1]()

* `[FIXED]` `[android]` activityScope & fragmentScope are not injecting source to avoid leak through ViewModel support handler - https://github.com/InsertKoinIO/koin/issues/953
* `[UPDATE]` `[koin-androidx-compose]` version 1.0.0-alpha07


## [2.2.0]()

* `[FIXED]` `[core]` GetOrNull hide exceptions - https://github.com/InsertKoinIO/koin/issues/913
* `[FIXED]` `[core]` SingleInstanceFactory concurrency fixed - https://github.com/InsertKoinIO/koin/pull/914
* `[ADDED]` `[core]` add `createEagerInstances` parameter to `Koin.loadModule` , `Koin.loadModules` 
* `[FIXED]` `[core]` @KoinApiExtension is using Warning compiler message, not error
* `[FIXED]` `[androidx-compose]` - Remove lazy API, as things are not sure for now about resolving Lazy<T> in a remember block (to be foloowed later). Best is to keep it with `get()` & `getViewModel()` in @Composable
* `[FIXED]` `[core]` Replace back Lazy(None) to standard Lazy delegate - https://github.com/InsertKoinIO/koin/issues/797
* `[ADDED]` `[core]` @KoinInternal to help protect internal components, without reusing @KoinApiExtension - dedicated to KoinComponent
* `[FIXED]` `[core]` Scope to drop extra declared definitions - https://github.com/InsertKoinIO/koin/issues/758 
* `[FIXED]` `[core]` Allow empty Scope declaration. Allow redeclaration of same scope, to complete it. 
* `[FIXED]` `[core, android, androidx, test]` Inject is ny default is using `LazyThreadSafetyMode.SYNCHRONIZED` by default
* `[FIXED]` `[android, androidx]` by viewModel is using `LazyThreadSafetyMode.NONE` by default
* `[ADDED]` `[core, android, androidx, test]` Inject allow to specify `mode`, to specify `LazyThreadSafetyMode`
* `[FIXED]` `[core]` hide to internal GlobalContext functions, to avoid redundancy with context.* functions: org.koin.core.context.GlobalContext.startKoin -> org.koin.core.context.startKoin
* `[FIXED]` `[androidx]` bring back ViewModel compat for androidx


### [2.2.0-rc-3]()

* `[FIXED]` Fix Scoped Components (ScopeActivity, ScopeFragment & ScopeService) to keep `scope` lazy and avoid forced initialisation

### [2.2.0-rc-2]()

* `[FIXED]` AndroidX ViewModel API merge for stateViewModel. No need anymore to pass Bundle as injected parameters, jsut use `get()`
* `[FIXED]` Resolution API to precise generic type to `Any`


### [2.2.0-rc-1]()

* `[ADDED]` JetNews Compose app example 

### [2.2.0-beta-2]()

Updated to kotlin 1.4.10

_koin-androidx-scope_

* `[FIXED]` added `scopeActivity` & `requireScopeActivity()` API on ScopeFragment

_koin-androidx-compose_

* `[ADDED]` New API to inject Jetpack Compose: `get()`, `by inject()`, `getKoin()`
* `[ADDED]` New API to inject Jetpack Compose with ViewModel: `getViewModel()`, `by viewModel()`

_koin-androidx-workmanager_

* `[ADDED]` New API to declare your `ListenableWorker` with `worker { }` DSL
* `[ADDED]` KoinWorkerFactory wired to the standard WorkManagerFactory, to let build declared component. Use `workManagerFactory()` in your `KoinApplication` DSL to setup the WorkManagerFactory. (nb: tedious to checkModules - as it depends on android internals)


_koin-core_

* `[ADDED]` KoinExtensionAPI: annotation for tagging Koin API usage. i.e: KoinComponent requires OptIn, as it's part of Koin API extension
* `[FIXED]` turn all API to internal as possible

_koin-test_

* `[FIXED]` revert `AutoCloseKoinTest` to class, add `ClosingKoinTest` interface

### [2.2.0-beta-1]()

_koin-androidx-scope_

* `[ADDED]` New `ScopeActivity`. `ScopeFragment`, `ScopeService` to enable Scope API direclty into Android components. Offers injection directly from tied Scope.
* `[BREAKING]` Old Scope API extensions are deprecated: `scope`, `lifecycleScope`

_koin-android-scope_

* `[ADDED]` New `ScopeActivity`. `ScopeFragment`, `ScopeService` to enable Scope API direclty into Android components. Offers injection directly from tied Scope.
* `[BREAKING]` Old Scope API extensions are deprecated: `scope`, `lifecycleScope`

_koin-androidx-viewmodel_

* `[ADDED]` New `ViewModelOwnerDefinition` API definition for lazy define ViewModelStore & SavedStateRegistryOwner
* `[BREAKING]` rewrite ViewModel API to better use StateViewModel factory and allow to use `ViewModelOwnerDefinition = () -> ViewModelOwner` ViewModelOwner lazy definition. Bundle is now used as `state` lazy attributes, `BundleDefinition = () -> Bundle`
* `[ADDED]` verification to help check parameter injection for SavedStateRegistryOwner, as the right argument (misplaced injection param)
* `[ADDED]` New `ViewModelOwnerDefinition` API definition for lazy define ViewModelStore & SavedStateRegistryOwner
* `[BREAKING]` rewrite ViewModel API to better use StateViewModel factory and allow to use `ViewModelOwnerDefinition = () -> ViewModelOwner` ViewModelOwner lazy definition. Bundle is now used as `state` lazy attributes, `BundleDefinition = () -> Bundle`
* `[ADDED]` verification to help check parameter injection for SavedStateRegistryOwner, as the right argument (misplaced injection param)

_koin-android-viewmodel_

* `[ADDED]` New `ViewModelOwnerDefinition` API definition for lazy define ViewModelStore
* `[BREAKING]` rewrite ViewModel API and allow to use `ViewModelOwnerDefinition = () -> ViewModelOwner` ViewModelOwner lazy definition
* `[ADDED]` New `ViewModelOwnerDefinition` API definition for lazy define ViewModelStore
* `[BREAKING]` rewrite ViewModel API and allow to use `ViewModelOwnerDefinition = () -> ViewModelOwner` ViewModelOwner lazy definition

_koin-core_

* `[FIXED]` fixed time API - Kotlin 1.4
* `[ADDED]` New `KoinScopeComponent` to help build component with Koin scopes
* `[REMOVED]` removed generic Scope API extensions, was preivously on any class. Please use now `KoinScopeComponent`
* `[DEPRECATED]` KoinContextHandler in favor of GlobalContext
* `[ADDED]` Better Definition to help further Kotlin Compiler Plugin, simplify Module loading process (API to help declare definition without DSL)
* `[REMOVED]` ScopeDefinition internal from module, and prefer qualifier to avoid create stucture outside of registry
* `[REMOVED]` removed inconsistent synchronized calls
* `[ADDED]` cleaned synchronized API - better call from GlobalContext to ensure synced call
* `[BREAKING]` empty scope is not taken in account anymore, a scope definition is created only if there is at least one scoped definition
* `[ADDED]` Injection parameters can be used directly as a dependency and can be resolved as `get()` or directly with builder API
* `[FIXED]` fixed starting context effect (deprecating KoinContextHanlder for GlobalContext)

_koin-test_

* `[FIXED]` fixed checkModules to use Mock for injected parameters or default origin value of a Scope
* `[ADDED]` setup detault values for injected parameters, for checkModules
* `[BREAKING]` `AutoCloseKoinTest` is now an interface, please remove any constructor
* `[UPDATED]` fixed `DefinitionParameters` to add the ability to know the injected type value

_koin-test-junit5_

* `[ADDED]` JUnit5 test module

## [2.1.6]()

_koin-core_

* `[BREAKING]` Disable property type cast https://github.com/InsertKoinIO/koin/pull/781
* `[FIXED]` Scope issue - Breaking looping linked scopes when first got instance https://github.com/InsertKoinIO/koin/pull/775
* `[FIXED]` On the fly declaration with primary type fix - https://github.com/InsertKoinIO/koin/pull/773

_koin-androidx-viewmodel_

* `[FIXED]` stateViewModel bundle argument fixed - https://github.com/InsertKoinIO/koin/pull/795/files
* `[ADDED]` added `stateSharedViewModel` extensions for `Fragment` https://github.com/InsertKoinIO/koin/pull/768/files

_koin-androidx-fragment_

* `[FIXED]` Add fallback for instantiate function in FragmentFactory - https://github.com/InsertKoinIO/koin/pull/742

_koin-test_

* `[FIXED]` KoinTestRule test exception handling - https://github.com/InsertKoinIO/koin/pull/808

_koin-gradle-plugin_

* `[FIXED]` Fixed Koin gradle plugin task for Kotlin & Android project. Added `checkAndroidModules` task  - https://github.com/InsertKoinIO/koin/pull/817


## [2.1.5]()

_Core_

* `[FIXED]` - declare to use reified type
* `[FIXED]` - Qualifier type as pure string
* `[FIXED]` - docs contribution
* `[UPDATED]` - Kotlin 1.3.71


_Ktor_

* `[ADDED]` - Contributions about modules and events

_AndroidX-Fragment_

* `[FIXED]` - contribution to help fallback on empty constructor instance for FragmentFactory

## [2.1.4]()

_Core_

* `[ADDED]` - Scope's source value to return the object instance, source of the scope 

## [2.1.3]()

* `[FIXED]` - maven metadata config :(

## [2.1.2]()

* `[UPDATED]` - maven metadata config

_Core_

* `[FIXED]` - fixed integration for kotlin.time.* API with 1.3.70


## [2.1.1]()

### [alpha-1]()

_AndroidX-ViewModel_

* `[FIXED]` - shared ViewModel API to get instance from Activity's ViewModelStore + clean of API to use directly ViewModelStoreOwner instead of LifecycleOwner


_Android-ViewModel_

* `[FIXED]` - shared ViewModel API to get instance from Activity's ViewModelStore + clean of API to use directly ViewModelStoreOwner instead of LifecycleOwner


## [2.1.0]()

_Core_

* `[UPDATED]` - introduce the `KoinContextHandler` component that is responsible to manage `GlobalContext` from startKoin. This will allow us to unlock new kind of context for Koin MP & better isolation (not directly a object that we pass around). To get your Koin instance, now use `KoinContextHandler.get()`, once you have started it. `koinApplication { }` users have to register manually to `KoinContextHandler` if needed


### [beta-3]()

* doc updates

_Test_

* `[ADDED]` - CheckModule category

_Gradle_PLugin_

* `[ADDED]` - CheckModule Gradle Plugin

### [beta-1]()

_Core_

* `[ADDED]` - Enum class can be used as Qualifier: `named(MyEnum.MyValue)`


### [alpha-11]()

_Core_

* `[FIXED]` - stopKoin closes scopes #702

_AndroidX-ViewModel_

* `[FIXED]` - added/fixed for better State ViewModel `getStateViewModel` and `by stateViewModel()` API

_Android_

* `[ADDED]` - `KoinAndroidApplication` to let you create a `KoinApplication` instance with Android context, and let you use KoinApplication DSL


### [alpha-10]()

_Core_

* `[UPDATED]` - updated `+` oprator for modules
* `[ADDED]` - Scope Links, to link scope to another and help resolve shared instances


### [alpha-8]()


_Docs_

* `[UPDATED]` - updated `koin-core` `Scope` section
* `[ADDED]` - inject on a setter property with `inject()`

_Android-Scope_

* `[UPDATED]` - updated `currentScope` to use `lifecycleScope` instead
* `[FIXED]` - `ScopeCompat` for Java compat function

_AndroidX-Scope_

* `[UPDATED]` - updated `currentScope` to use `lifecycleScope` instead
* `[FIXED]` - `ScopeCompat` for Java compat function

_AndroidX-Factory_

* `[FIXED]` - `Fragment` declaration in a scope

_Core_

* `[ADDED]` - DSL declare a scope with type directly with `scope<MyType> { ... }`
* `[ADDED]` - smarter better API to use scope from an object instance (`getOrCreateScope` ...)
* `[ADDED]` - `scope` property to any instance, to get tied declared scope
* `[ADDED]` - inject on a setter property with `inject()`

_Core-Ext_

* `[ADDED]` - inject all setter property with `inject()` on an instance


### [alpha-7]()

_Android-ViewModel_

* `[UPDATED]` - updated `ViewModelParameter` API around to help integrate it more easily with 3rd party access

_AndroidX-ViewModel_

* `[UPDATED]` - updated `ViewModelParameter` API around to help integrate it more easily with 3rd party access

_AndroidX-Factory_

* `[ADDED]` - `KoinFragmentFactory` API to setup `Fragment` injection

_Core_

* `[UPDATED]` - Reworked all resolution engine to use immutable BeanDefinition & base the resolution on `Scope` & `ScopeDefinition`
* `[UPDATED]` - Locking Strategy to avoid usage of ConcurrentHashMap
* `[UPDATED]` - Replace BeanRegistry with InstanceRegistry & ScopeRegistry
* `[UPDATED]` - added `closed` status to Scope
* `[FIXED]` - Fixed bugs related to closed scopes
* `[FIXED]` - Can now allow to resolve different types with same Qualifer
* `[ADDED]` - Module `loaded` property in order to allow later "reloading"
* `[ADDED]` - Java helpers are now part of the `koin-core` project
* `[ADDED]` - bind<T>() oeprator on a definition, that use reified Type
* `[ADDED]` - _q()_ operator to declare a String or a Type 

_Java_

* `[REMOVED]` - project is now part of `koin-core`

_Test_

* `[UPDATED]` - Check modules with `checkModules { }` that open an KoinApplication declaration
* `[ADDED]` - `MockProviderRule` & `MockProvider` to manually provide mocking capacity, absed of the desired mocing framework
* `[REMOVED]` - Link to `Mockito` library

_Documentation_

* `[UPDATED]` - New documentation system based on docisfy, to help deploy easily markdown doc. Documentation is now in `/docs` folder


## [2.0.1]()

_Android-ViewModel_

* `[ADDED]` - debug logging for VM provider

_AndroidX-ViewModel_

* `[ADDED]` - debug logging for VM provider

_Core_

* `[FIXED]` - performances update - modules list loading & class naming
* `[BREAKING]` - `modules(vararg modules: Module)` in `KoinApplication` has been removed for performance reasons. Please use `modules(modules: List<Module>)`
* `[BREAKING]` - `modules(modules: Iterable<Module>)` in `KoinApplication` has been removed for performance reasons. Please use `modules(modules: List<Module>)`
* `[BREAKING]` - `loadKoinModules(vararg modules: Module)` in `GlobalContext` has been removed for performance reasons. Please use `loadKoinModules(module: Module)` or `loadKoinModules(modules: List<Module>)`
* `[BREAKING]` - `unloadKoinModules(vararg modules: Module)` in `GlobalContext` has been removed for performance reasons. Please use `unloadKoinModules(module: Module)`

## [2.0.0]()

_Android_

* `[UPDATED]` - rework startking DSL to add extension in startKoin (`androidContext`, `androidLogger`)

_Android-Scope_

* `[UPDATED]` - rework according to new Scope API (manage complete Scope Lifecycle)
* `[ADDED]` - `currentScope` property scope tied to current Activity or Fragment
* `[ADDED]` - `currentScope` is aware of any `KoinComponent` & currentScope override
* `[UPDATED]` - ScopeID generation

_Android-ViewModel_

* `[ADDED]` - support for new Scope API
* `[REMOVED]` - removed koin-ext builder API
* `[UPDATED]` - make the API open to Koin instance isolation
* `[FIXED]` - https://github.com/InsertKoinIO/koin/issues/452 - Named qualifier does not work with view models - Now take qualifier as ViewModel's Tag
* `[FIXED]` - by viewModel & getViewModel with clazz version
* `[FIXED]` - ViewModel definition in scope

_AndroidX-Scope_

* `[UPDATED]` - update API regarding the changes in `koin-android-scope`
* `[UPDATED]` - ScopeID generation

_AndroidX-ViewModel_

* `[UPDATED]` - update API regarding the changes in `koin-android-viewmodel`
* `[FIXED]` - https://github.com/InsertKoinIO/koin/issues/452 - Named qualifier does not work with view models - Now take qualifier as ViewModel's Tag
* `[FIXED]` - by viewModel & getViewModel with clazz version
* `[FIXED]` - ViewModel definition in scope

_Core_

* `[UPDATED]` - startKoin replaced with startKoin DSL and koin in global context
* `[UPDATED]` - complete internals rewritten for performances optimisation (startup & injection)
* `[UPDATED]` - `KoinComponent` now can override `getKoin()` to target a custom Koin instance & `currentScope()` to target a Scope that is used for all injections
* `[ADDED]` - koinApplication function to help declare an instance  for a local context, in order to help isolated Koin instances
* `[UPDATED]` - rework Scope API (multiple instances definitions, properties, release, callback ...)
* `[UPDATED]` - rework Scope DSL (scope/scoped) & lock single/factory
* `[UPDATED]` - rework internals to use root Scope & separate Scope instances, with different bean registry
* `[ADDED]` - onClose, onRelease DSL on single/factory/scoped to execute code when releasing instance or stopping container 
* `[ADDED]` - getProperty with default value
* `[UPDATED]` - Kotlin 1.3.21
* `[ADDED]` - Qualifiers with `named()` function to replace old string names. Allow to use Types
* `[ADDED]` - `getOrNull()` and `injectOrNull()` to safely resolve potential components and get null of not present
* `[UPDATED]` - additional binding API, with `getAll<S>()` & `bind<P,S>()` operator to look for instances regarding secondary type definition
* `[ADDED]` - `declare()` on Koin & Scope, to help declare an instance on the fly
* `[FIXED]` - Factory declaration in scope

_Test_

* `[UPDATED]` - rework testing API, codebase detached from internal core tests
* `[UPDATED]` - checkModules now rely on pure Kotlin & with `create` parameters DSL

_Java_

* `[UPDATED]` - updated `KoinJavaComponent` for the new API
* `[FIXED]` - https://github.com/InsertKoinIO/koin/issues/451 - No longer possible to get objects from a scope in Java code - `scope` parameter to allow resolve a dependency from this scope instance
* `[FIXED]` - Get access to scope instance

_Ktor_

* `[UPDATED]` - rework startking DSL to add extension in startKoin
* `[ADDED]` - Koin as a Ktor feature

_Spark_

* `[REMOVED]` project non ported to Koin 2.0


## [1.0.2]()

_Android_

* `[FIXED]` Fixed bug with appCompat v26 https://github.com/InsertKoinIO/koin/pull/268
* `[ADDED]` Android debug logs shut off https://github.com/InsertKoinIO/koin/issues/251

_Android-Scope_

* `[UPDATED]` Better scope management when associated to a lifecycle https://github.com/InsertKoinIO/koin/issues/235

_Android-ViewModel_

* `[FIXED]` Proguard Warning https://github.com/InsertKoinIO/koin/pull/267

_Core_

* `[UPDATED]` - Updated KoinComponent interface to allow easily add a custom Koin instance https://github.com/InsertKoinIO/koin/issues/224
* `[UPDATED]` - Allow nullable injection params https://github.com/InsertKoinIO/koin/pull/242
* `[UPDATED]` - Add instance for a dedicated scope https://github.com/InsertKoinIO/koin/issues/277
* `[FIXED]` Documentation Typo fixed https://github.com/InsertKoinIO/koin/pull/263
* `[FIXED]` Fixed Scope toString in debug https://github.com/InsertKoinIO/koin/issues/240 

_Core-Ext_

* `[FIXED]` Add scopeId param in experimental API builder https://github.com/InsertKoinIO/koin/pull/252


_Test_

* `[UPDATED]` Review declareMock to add mock lambda expression https://github.com/InsertKoinIO/koin/issues/243
* `[FIXED]` Fixed params injection with checkModules https://github.com/InsertKoinIO/koin/issues/274



## [1.0.1]()

_Android_

* `[FIXED]` - `koin-android-viewmodel` & `koin-androidx-viewmodel` parameter & factory review - https://github.com/InsertKoinIO/koin/issues/236


## [1.0.0]()

Core_

* `[UPDATED]` - compilation with Kotlin 1.2.70
* `[ADDED]` - new Scope API
* `[FIXED]` - fixed scope visibility resolution - https://github.com/InsertKoinIO/koin/issues/228
* `[FIXED]` - `koin-android-viewmodel` parameters leak

_Core-Ext_

* `[ADDED]` - Renamed `koin-reflect` to `koin-core-ext`

_Android_

* `[FIXED]` - `koin-android-viewmodel` parameters leak fixed - https://github.com/InsertKoinIO/koin/issues/220
* `[ADDED]` - `koin-android-viewmodel` added `viewModel` automatic builder keyword
* `[FIXED]` - `koin-android` added back `androidApplication()`
* `[ADDED]` - `koin-android-scope` updated `bindScope` function now bind a `Scope`

_Test_

* `[FIXED]` - `check()` renamed to `checkModules()`


## [1.0.0-beta3]()

_Scripts_

* `[UPDATED]` - rework all gradle structure and scripts - [#126](https://github.com/InsertKoinIO/koin/issues/126)
* `[ADDED]` - asciidoc integration - [#127](https://github.com/InsertKoinIO/koin/issues/127)
* `[ADDED]` - CI integration with CircleCI


_Core_

* `[UPDATED]` - compilation with Kotlin 1.2.50
* `[UPDATED]` - Update DSL keywords: `module` & `single` (replaces `applicationContext`, `context` & `bean`) - [#131](https://github.com/InsertKoinIO/koin/issues/131)
* `[FIXED]` - Fixed Scope API, aka module visibility resolution
* `[ADDED]` - SLF4J Logger implementation (`koin-logger-slf4j`), used by `koin-spark` & `koin-ktor` - [#130](https://github.com/InsertKoinIO/koin/issues/130)
* `[FIXED]` - Resolution & error check/catch for display - [#110](https://github.com/InsertKoinIO/koin/issues/110)
* `[FIXED]` - BeanDefinition equals()
* `[FIXED]` - Instance creation error display - [#124](https://github.com/InsertKoinIO/koin/issues/124)
* `[UPDATED]` - logging tree resolution (with debug data if needed)
* `[ADDED]` - asciidoc doc updated - [#121](https://github.com/InsertKoinIO/koin/issues/121) - [#100](https://github.com/InsertKoinIO/koin/issues/100) - [#102](https://github.com/InsertKoinIO/koin/issues/102) - [#103](https://github.com/InsertKoinIO/koin/issues/103)
* `[UPDATED]` - Injection parameter API with destructured declaration - [#133](https://github.com/InsertKoinIO/koin/issues/133)
* `[ADDED]` - Preload instances with `createAtStart` - [#141](https://github.com/InsertKoinIO/koin/issues/141)
* `[UPDATED]` - Explicit bean/module override - [#123](https://github.com/InsertKoinIO/koin/issues/123)
* `[FIXED]` - bind operator check assignable types


_Test_

* `[ADDED]` - asciidoc doc
* `[ADDED]` - check() feature to check all configuration (does not create instances like dryRun)
* `[ADDED]` - declare mock or other definition out of the box with `declareMock` & `declare`- [#151](https://github.com/InsertKoinIO/koin/issues/151) - [#119](https://github.com/InsertKoinIO/koin/issues/119)


_Android_

* `[BREAKING]` - Package & project renaming -> `koin-android`,`koin-android-scope` & `koin-android-viewmodel` - [#144](https://github.com/InsertKoinIO/koin/issues/144)
* `[REMOVED]` - bindString(), bindBool() & bindInt()
* `[ADDED]` - asciidoc doc
* `[ADDED]` - support AndroidX packages with `koin-androidx-scope` & `koin-androidx-viewmodel` - [#138](https://github.com/InsertKoinIO/koin/issues/138) - [#122](https://github.com/InsertKoinIO/koin/issues/122) - [#154](https://github.com/InsertKoinIO/koin/issues/154)
* `[ADDED]` - scoping feature with Android lifecycle with `koin-android-scope` - [#142](https://github.com/InsertKoinIO/koin/issues/142)
* `[UPDATED]`- Open startKoin() for other Android context & use `Context` instead of `Application` - [#156](https://github.com/InsertKoinIO/koin/issues/156)
* `[FIXED]` - ViewModel factory instance creation bugfix - [#145](https://github.com/InsertKoinIO/koin/issues/145)
* `[FIXED]` - ViewModel factory injection params leak - [#149](https://github.com/InsertKoinIO/koin/issues/149)
* `[UPDATED]`- androix version to `2.0.0-beta01`

_Ktor_

* `[ADDED]` - asciidoc doc
* `[ADDED]` - use `koin-logger-slf4j`
* `[UPDATED]` - ktor `0.9.2`
* `[ADDED]`- add `Route` & `Routing` class extensions - [#128](https://github.com/InsertKoinIO/koin/issues/128)

_Spark_

* `[ADDED]` - asciidoc doc
* `[ADDED]` - use `koin-logger-slf4j`
* `[UPDATED]` - `controller` keyword now need your class to extend `SparkController`


_Java_

* `[ADDED]` - add `koin-java` project - [#106](https://github.com/InsertKoinIO/koin/issues/106)
* `[ADDED]` - asciidoc doc

_Reflect_

* `[ADDED]` - add `koin-reflect` project
* `[ADDED]` - add `build()` DSL function to allow smarter way of building definition
* `[ADDED]` - asciidoc doc

_Website_

* `[ADDED]` - Doc integration
* `[ADDED]` - Getting started integration
* `[FIXED]` - Kotlin slack URL - [#125](https://github.com/InsertKoinIO/koin/issues/125)


_Samples_

* `[ADDED]` - Added `examples` folder, gathering examples application 
* `[ADDED]` - Thermosiphon example - [#116](https://github.com/InsertKoinIO/koin/issues/116)


## [0.9.3]()

Gradle & Continuous Integration

*version published with new build chain*

## [0.9.2]()

_Core_

* `[UPDATED]` build updated to Kotlin 1.2.31
* `[UPDATED]` dissociate overload with dynamic parameters on `get()` and `inject()` for better autocomplete suggestions
* `[FIXED]` fix `startKoin()` property loading from boolean `loadProperties`
* `[FIXED]` inject by name take now care of injected type [#92](https://github.com/Ekito/koin/issues/92)

_Android_

* `[FIXED]` `startKoin()` doesn't break RestrictMode anymore and you can avoid properties load directly from koin.properties [#96](https://github.com/Ekito/koin/issues/96)
* `[UPDATED]` dissociate overload with dynamic parameters on `get()` and `inject()` for better autocomplete suggestions

_Android Architecture_

* `[UPDATED]` dissociate overload with dynamic parameters on `viewModel(), `getViewModel()` and `getSharedViewModel()` for better autocomplete suggestions
* `[UPDATED]` now exclude gradle dependency against `LiveData`

_Samples_

* `FIXED` Ktor hello appp sample

## [0.9.1]()

_DSL_

* `[FIXED]` Parameter propagation - You can now reuse params in `get()` to propagate it [#66](https://github.com/Ekito/koin/issues/66)

_Core_

* `[ADDED]` Parameters are now expressed via Kotlin function to allow lazy evaluation [#67](https://github.com/Ekito/koin/issues/67)
* `[UPDATED]` Parameter propagation [#66](https://github.com/Ekito/koin/issues/66)
* `[FIXED]` Stack resolution engine fixed [#72](https://github.com/Ekito/koin/issues/72)
* `[FIXED]` Better Logs, Error & Stacktraces
* `[FIXED]` Properties loading from external `properties` file can read values as String/Integer/Float

_Android_

* `[ADDED]` `get()` direct dependency eager fetching function for Activity/Fragment/Service [#78](https://github.com/Ekito/koin/issues/78)


_Android Architecture_

* `[ADDED]` To help misuse the Koin ViewModel API, we introduce the `getSharedViewModel()` and `sharedViewModel()` from Fragment, to reuse `ViewModel` from parent `Activity`. `getViewModel()` and `viewModel()` now creates `ViewModel` instance (no `FromActivity` parameter anymore)
* `[UPDATED]` ViewModel factory logs to see what Activity/Fragment is getting `ViewModel` instance


## [0.9.0]()

_DSL_

* `[DEPRECATED]` - `provide` - can be directly replace with `bean` 
* `[UPDATED]` - `bean` and `factory` can now use `bind`
* `[ADDED]` - defintion function inside `bean` or `factory` can now have parameters

_Core_

* `[FIXED]` Context resolution and modulePath isolation reworked - now fully functionnal
* `[FIXED]` Stack resolution
* `[ADDED]` `StandAloneContext` function `loadKoinModules` to load Koin modules whether Koin is already started
* `[ADDED]` `StandAloneContext` function `loadProperties` to load Koin properties whether Koin is already started
* `[ADDED]` `by inject()` function handle parameters to definition ([#59](https://github.com/Ekito/koin/issues/59))
* `[FIXED]` Logging with class name need introspection
* `[ADDED]` Context lifecycle notification callback - to allow a callback when `releaseContext()` is called on a context. You can register with `StandAloneContext.registerContextCallBack()` ([#58](https://github.com/Ekito/koin/pull/58))

_Android_

* `[FIXED]` StartKoin have a `logger` parameter, with default as AndroidLogger(), to allow specify logging implementation to use with Koin ([#50](https://github.com/Ekito/koin/issues/50)) - allow no logging at startKoin of logger is set to `EmptyLogger()`
* `[ADDED]` `by inject()` function handle parameters to definition ([#49](https://github.com/Ekito/koin/issues/49))


_Android Architecture_

* `[FIXED]` fix viewModel extensions rewritten from `LifecycleOwner` interface
* `[FIXED]` fix `by viewModel()` API to allow use KClass object argument instead of type inference - offers now `getViewModelByclass()` and `viewModelByClass()` ([#56](https://github.com/Ekito/koin/issues/56))
* `[ADDED]` `by viewModel()` function handle parameters to definition ([#49](https://github.com/Ekito/koin/issues/49))
* `[ADDED]` `by viewModel()` function handle key instance for Android ViewModelFactory ([#49](https://github.com/Ekito/koin/issues/49))


## [0.8.2]()

_Core_

* fix internal instance registry due to compat error with Kolin `1.1.61` & `1.2.21` - broke  Koin API for app using jdk 7 under the hood - [issue #43](https://github.com/Ekito/koin/issues/43)

## [0.8.1]()

_Android Architecture_

<div class="alert alert-primary" role="alert">
  Now declare your ViewModel lazily in attributes, with <b>by viewModel()</b> like <b>by inject()</b>
</div>

* `by viewModel()` lazy function call the `getViewModel()` function and allow `val` attribute declaration of your `ViewModel` (like with `by inject()`) - ([issue #37](https://github.com/Ekito/koin/issues/37))
* Better support to share `ViewModel` between Activity and Fragments
* `android.arch.lifecycle:extensions` updated to `1.1.0`

_Spark_

<div class="alert alert-primary" role="alert">
    <b>start</b> and <b>startKoin</b> functions have been merged 
</div>

* `startSpark {}` has been renamed to `start {}` and have a `modules` arguments to list all of your modules
* `start()` and `startKoin` has been merged to `start( modules = <List of modules>)`
* `stopSpark{}` has been renamed to `stop {}` and include `closeKoin()`

_Core_

* Kotlin `1.2.21`
* Internal fixes around Bean definition lookup / Duplicated defintion ([issue #39](https://github.com/Ekito/koin/issues/39))
* Fixed loading from koin.properties (for embedded jar)


## [0.8.0]()

<div class="alert alert-secondary" role="alert">
  Introducing new modules: koin-spark and koin-android-architecture
</div>

_Android Architecture_

* `viewModel` DSL keyword (specialized `provide` alias for ViewModel), to declare an Architecture Components ViewModel
* `getViewModel()` function in Activities and Fragments, to get and bind ViewModel components

_Spark_

* `controller {}` DSL keyword (specialized `provide` alias), to declare Spark controllers classes
* `start() {}` function to start Spark server (optional port number) - lambda expression to allow the start of Koin and any controller instantiation 
* `runControllers()` function to instantiate all spark controllers, declared with `controller` keyword DSL

_Core_

* Better logging and error display
* Some fixes about internals resolution for bound types

<br/>

## [0.7.1]()

<div class="alert alert-warning" role="alert">
This 0.7.x branch brings great simplifications in DSL and API.  Users from Koin 0.6.x may check the <a href="{{ site.baseurl }}/docs/{{ site.docs_version }}/updates/whats-new">What's new in 0.7.x</a> guide 👍
</div>

Kotlin compilation has been updated to **1.1.61** 

_Core_

* Simplified DSL modules: no more need of `Module` class. Now use directly the `applicationContext`function
* Default Koin Logger to `PrintLogger` instead of `EmptyLogger`
* a definition can be overridden (with a definition same name and type)
* DSL `provide` aliases with `bean` and `factory`
* direct interface binding writing style (avoid to use `bind` keyword)
* Koin instances resolution is now thread safe and compatible with coroutines
* starter chain reviewed to allow better extension of `startKoin()`
* better logs to display how instance and resolution are made
* Context isolation disabled by default. Can be activated later

_Android_

<div class="alert alert-warning" role="alert">
  Android ContextAware components have been dropped. Please, check the <a href="{{ site.baseurl }}/docs/{{ site.docs_version }}/updates/whats-new">migration</a> guide
</div>

* Default Koin Logger to `AndroidLogger` instead of `EmptyLogger`
* No need anymore of `AndroidModule`, just use `applicationContext` to declare a module
* Android extensions have been reworked to avoid need of support Library
* `androidApplication()` is a DSL extension to provide Android `Application` resolution (can also be done with `get<Application>()`)

_Samples_

* Updated Android weather app, with multiple Activity and better demo for property usage
* Simple Kotlin app sample added

<br/>
## [0.6.1]()

_Core_

* Added context/sub-contexts visibility checks
* `startKoin()` is now part of standAloenContext
* `closeKoin()` now close actual Koin context
* `KoinComponent` has `get()` and `getProperty()`

_Android_

* reworked `starKoin()` function
* reviewed android base extensions to find high-level extension point

_Samples_

* added kotlin-simpleapp - sample application for pure Kotlin 
* added ktor-starter - for demoing Koin for Ktor web app development

_Ktor_

* koin-ktor module has been started


## [0.6.0]()

The target of this release was to simplify and make clearer the syntax of Koin.

_DSL_

* `getProperties` can now have a default value

_Core_

* Stand alone complex & KoinComponent have been reviewed
* renamed startContext functions to `startKoin()`
* KoinComponent
  - inject/property
  - setProperty
  - release context or properties
  - startKoin

* Koin can load properties from koin.properties file or system properties
* `startKoin()` have now a `properties` parameter to give additional properties at startup

#### No more `getKoin()` !

KoinTest
* directly extends `KoinComponent`
* context assert/test tools & extensions
* `dryRun()` is usable in a Kointest component, after a `startKoin`

Android
* fix/better extensions for Android
* `bindProperty()` renamed to `setProperty()`
* `startAndroidContext()` has been renamed to `startKoin()`
* ContextAware Components can be configured for drop strategy (onDestroy or onPause). Default method is onPause
* load assets/koin.properties if present

## [0.5.2]()

_Core_

* `getProperty()` can provide a default value if key property is missing 
* `provide()` can be use on a KoinContext directly, to declare on component

_Android_

* `applicationContext` has been renamed `androidApplication`


## [0.5.1]()

_Core_

* property value set does not allow Any? value
* starters builder have been moved to [test] & [android]
* removed base starter 

_Android_

* starter builder extension for Application class only

_Tests_

* starter builder extension for KoinTest class only

javadoc is now generated with Dokka


## [0.5.0]()

_Core_ 

* Koin now provide a **stand alone context** support to provide koin context in an agnostic way
* `startContext` function to start a Koin context
* property injection/resolution is now strict. Property injection is not nullable anymore
* `koinContext.removeProperties()` remove properties by their keys
* Simple logger to help logging/debugging

_Android_
* `startAndroidContext` function to start an Android Koin context
* `KoinContextAware` interfaces have been removed
* Koin/Android extensions greatly simplified
* packages have been moved - need reimport for AndroidModule, `by inject` and `by property`
* `ContextAwareActivity` & `ContextAwareFragment` are components to help you automatically release contexts - don't hesitate to do it manually

_Tests_

* Creation of 'org.koin:koin-test:' module, provide testing feature about Koin


## [0.4.0]()

Koin DSL has been reviewed to be more easy to understand, and allow easy writing of modules. No need anymore of several module classes, you can now include subcontexts in one module. You can now provide factory components, and bind is simpler to write.

The **dry run** feature, allows to run all of you modules in order to check if dependency graph is complete.

_DSL_

* replaced `declareContext{}` has been renamed `applicationContext{}`, and behind gives a better idea that you are describing your application context (the root context of your app)
* Updated `modulePath(){}`has been dropped for `context(){}` - *context* describes a sub context of your application, has a a name and can have also sub context itself (sub contexts are hierarchical)
* Updated `bind{}` doesn't need lambda anymore to declare your bound class, but just the class in argument: `bind()`
* Added `provideFactory` is a DSL keyword to provide a factory definition instead of singleton


_Core_

* Updated `KoinContext.release()` is now called with context's name, no owner class instance
* Added `KoinContext.dryRun()` help to run your modules and check if every dependency can be injected

_Sample App_ & _Android_

* updated for the new core update

## [0.3.1]()

_Core_
* Kotlin 1.1.51
* Removed all loggers - rethinking of better Log/API

_Android_
* KoinContext function builders : newKoinContext & lazyKoinContext
* KoinContextAware interface now has a property, instead of a getter - better to overload

_Sample App_
* moved to folder koin-sample-app

## [0.3.0]()

_Core_
* Kotlin 1.1.50
* KoinContext / Context - simplified operator injection (removed all nullable inject/get)
* KoinContext.get() can make Injection by name
* Koin.build() can now build a list of *Module*

_DSL_
* Context.get() can now provide a definition with a name
* Context.get() operator have a bind property, to specify list of compatible interfaces to bind - is an alternative to bind {} operator

_Android_
* Merge android & android-support modules
* inject() can't be null anymore
* inject() can inject component by its name
* property() can do property injection - can be nullable data

_Sample App_
* Sample app (Weather demo application) has been reviewed to be more complete : Full MVP example, RxJava Scheduling, Unit Tests with partial modules loading & mocks 

## [0.2.x]()

**Koin & Koin-Android project has meen merged**

_DSL_

* Module/AndroidModule class must now give a `context()` function implementation, to return a Context object. The `declareContext` function unlock the Koin DSL to describe dependencies and injection:

```Kotlin
class MykModule : Module() {
    override fun context() = declareContext { ... }
}
```
* You can now [bind](#type-binding) additional types for provided definitions
```Kotlin
class MykModule : Module() {
    override fun context() = declareContext { 
        provide { Processor() } bind { ProcessorInterface::class}
    }
}
```

_Scope_

* You can declare/reuse paths in your modules, with `modulePath {}` operator. See paths section
* Release modulePath instances with `release()` on a KoinContext

```kotlin
class MainActivityModule : Module() {
    override fun context() =
            declareContext {
                // Scope MainActivity
                modulePath { MainActivity::class }
                // provided WeatherService
                provide { WeatherService(get()) }
            }
}
```

_Android_

* `KoinAwareContext` interface used to setup Android application (KoinApplication & KoinMultidexApplication are removed)

```Kotlin
class MainApplication : Application(), KoinContextAware {

     /**
     * Koin context
     */
    lateinit var context: KoinContext

    /**
     * KoinContextAware - Retrieve Koin Context
     */
    override fun getKoin(): KoinContext = context

    override fun onCreate() {
        super.onCreate()
        // insert Koin !
        context = Koin().init(this).build(MyModule()) 
        // ...
    }
}
```

* `by inject<>()` function operator to inject any dependency in any Activity or Fragment

```Kotlin
class MainActivity : AppCompatActivity() {

    // inject my WeatherService 
    val weatherService by inject<WeatherService>()
}
```

_Koin_

* Koin builder takes module instances (instead of module classes):

```Kotlin
// fill applicationContext for Koin context
val ctx = Koin().init(applicationContext).build(Module1(),Module2()...)
```
Internal rework for simpler use with Scopes:
* `Koin().build()` return KoinContext
* factory, stack operators have been removed, for the modulePath fatures
* delete/remove replaced with `release()` Scope operation
* import is replaced with module instances load
* All reflection & kotlin-reflect code have been removed



