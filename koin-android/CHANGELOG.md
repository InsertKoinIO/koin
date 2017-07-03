# Change Log

## [0.2.0]()

**Added**
- Scopes
- bind & scope function for DSL

**Fixes**
- Module DSL
- Context -> reworked as KoinContext
- Context is now for internal DSL declaration

**Removed**
- Any link with introspection/reflect
- factory, stack => replace with Scope
- import => replace with load multi modules
- inject => no need anymore (see `by inject<>()` in Koin-Android)


## [0.1.3]()

Based on koin-core 0.1.3

**Fixes**

**Added**
- KoinContextAware, to help you define your Koin context
- inject & optional delegates function to inject your components

**Removed**
- KoinApplication & KoinMultidexApplication classes - avoid you to use properties. Now use KoinContextAware interface instead

## [0.1.2]()

Based on koin-core 0.1.2

**Fixes**
- Module loading & properties check

**Added**
- First tests

## [0.1.1]()

Based on koin-core 0.1.1

**Fixes**
- Android Koin application check & Exception

**Added**
- KoinApplication & KoinMultidexApplication has vararg module - load several Android module
- Koin build with multiple modules in entry
- Kotlin 1.1.2-5

## [0.1.0]()

First release


