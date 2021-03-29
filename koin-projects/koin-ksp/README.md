# Setup
## How to develop it it?
./gradlew :koin-ksp:build examples:coffee-maker:build --continuous

apply processor to your project by adding these 

- See source code files generated at `examples/coffee-maker/build/generated/ksp/main/java/`
- See logs at `examples/coffee-maker/build/generated/ksp/main/resources/`


## How to apply it in a koin module

```kotlin
dependencies {
    ...
        implementation(project(":koin-ksp"))
        ksp(project(":koin-ksp"))
    ...
}
```

