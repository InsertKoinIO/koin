# Change Log

## [0.2.0]()

**Added**
- Scopes
- bind & scope function for DSL

**Fixes**
- Module DSL
- Context -> reworked as KoinContext
- Context is now for internal DSL delcaration

**Removed**
- Any link with introspection/reflect
- factory, stack => replace with Scope
- import => replace with load multi modules
- inject => no need anymore (see inject by in Koin-Android)

## [0.1.3]()

**Added**

**Fixes**
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
