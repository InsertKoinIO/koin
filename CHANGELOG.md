# Change Log

## [0.3.0]()

**Added**

**Removed**

## [0.2.2]()

**Added**
- [Android] android-support has extensions now on FragmentActivity
- [Core] KoinContext.provide() & KoinContext.provideAt()
- Kotlin 1.1.4-2

**Removed**
- [Android] android-support extensions for AppCompatSupport
- [Core] KoinContext.provideDefinition()

Also Integrated last pull requests (core code clean & android resource clean) 

## [0.2.1]()

**Added**

- [Core] Cycle dependency protection 
- [Android] android-support module - unlock Koin for AppCompatActivity & Fragment (support)
- Kotlin 1.1.3-2

**Fixes**

- [Core] Koin context call stack


## [0.2.0]()

**Added**

- [Core] Scopes
- [Core] bind & scope function for DSL
- [Android] `by inject` functions


**Fixes**

- [Core] Module DSL
- [Core] Type binding
- [Core] Context -> reworked as KoinContext
- [Core] Context is now for internal DSL declaration

**Removed**

- [Core][Android] Any link with introspection/reflect
- [Core] factory, stack => replace with Scope
- [Core] import => replace with load multi modules
- [Core] inject => no need anymore (see inject by in Koin-Android)

## [0.1.3]()

**Added**
- KoinContextAware, to help you define your Koin context
- inject & optional delegates function to inject your components

**Fixes**
- bean linking
- inject() - revisited introspection code

## [0.1.2]()

**Fixes**
- Missing property throws MissingPropertyException

## [0.1.1]()

**Fixes**
- Factory bean does not keep instances any more
- Bean definition overwrite when providing new definition
- Missing property throws MissingPropertyException

**Added**
- Koin build with vararg of Module classes
- Context delete functions
- Context removeInstance with list of classes
- Kotlin 1.1.2-5

## [0.1.0]()

First release
