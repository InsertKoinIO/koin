# Change Log

## [0.1.1]()

First release

**Fixes**
- Bean definition overwrite when providing new definition

**Added**
- Koin build with multiple modules
- Context delete functions
- Context remove with list


## [0.1.0]()

First release

**Added**
- Koin Builder
- Basic context functions : module, provide, import & lazy linking, bean & instances registry
- Provide by class (1st constructor)
- Advanced context functions : Factory & Stack bean instance strategies
- Can remove instance/definition from context
- Properties injection
- Retrieve by interface
- @Inject injection

## Installation

### Maven

```xml
<dependency>
    <groupId>org.koin</groupId>
    <artifactId>koin-core</artifactId>
    <version>0.1.1</version>
</dependency>
```

### Gradle

```gradle
compile 'org.koin:koin-core:0.1.1'
```
