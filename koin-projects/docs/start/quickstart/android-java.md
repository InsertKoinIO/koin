# Getting Started with Android Java application {docsify-ignore-all}

> This tutorial lets you write an Android Java application and use Koin inject and retrieve your components.

## Get the code

Checkout the project directly on Github or download the zip file

> 🚀 Go to [Github](https://github.com/InsertKoinIO/getting-started-koin-android) or [download Zip](https://github.com/InsertKoinIO/getting-started-koin-android/archive/master.zip)

## Gradle Setup

Add the Koin Android dependency like below:

```groovy
// Add Jcenter to your repositories if needed
repositories {
    jcenter()    
}
dependencies {
    // Koin for Android
    compile 'org.koin:koin-android:$koin_version'
}
```

## Our components (Java & Kotlin)

Let's create a HelloRepository to provide some data:

```kotlin
interface HelloRepository {
    fun giveHello(): String
}

class HelloRepositoryImpl() : HelloRepository {
    override fun giveHello() = "Hello Koin"
}
```

Let's create a Java presenter class, for consuming this data:

```java
public class MyJavaPresenter {

    private HelloRepository repository;

    public MyJavaPresenter(HelloRepository repository) {
        this.repository = repository;
    }

    public String sayHello(){
        String hello = repository.giveHello();

        return hello+" from "+this;
    }

}
```

## Writing the Koin module

Use the `module` function to declare a module. Let's declare our first component:

```kotlin
val appModule = module {

    // single instance of HelloRepository
    single<HelloRepository> { HelloRepositoryImpl() }

    // Simple Presenter Factory
    factory { MyJavaPresenter(get()) }
}
```

?> we declare our MySimplePresenter class as `factory` to have a create a new instance each time our Activity will need one.

## Start Koin

Now that we have a module, let's start it with Koin. Open your application class, or make one (don't forget to declare it in your manifest.xml). Just call the `startKoin()` function:

```java
class MyApplication extends Application {
    
     @Override
    public void onCreate() {
        super.onCreate();
        // Start Koin
        KoinApplication koin = KoinAndroidApplication.create(this)
            .modules(appModule);
        startKoin(new GlobalContext(), koin);
    }
}
```

## Injecting dependencies into Java Activity

The `MyJavaPresenter` component will be created with `HelloRepository` instance. To get it into our Activity, let's inject it with the static `inject()` function: 

```java
// import inject
import static org.koin.java.standalone.KoinJavaComponent.inject;

public class JavaActivity extends AppCompatActivity {

    private Lazy<MySimplePresenter> presenter = inject(MySimplePresenter.class);
    private Lazy<MyJavaPresenter> javaPresenter = inject(MyJavaPresenter.class);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple);

        //...
    }
}
```

?> The `inject()` function allows us to retrieve a lazy Koin instances, in Android components runtime (Activity, fragment, Service...)

?> The `get()`function is here to retrieve directly an instance (non lazy)