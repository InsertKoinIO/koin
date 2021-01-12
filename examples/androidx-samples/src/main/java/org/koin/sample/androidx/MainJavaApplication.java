package org.koin.sample.androidx;

import android.app.Application;
import java.util.Collections;
import org.koin.android.java.KoinAndroidApplication;
import org.koin.core.KoinApplication;
import org.koin.core.context.GlobalContext;
import org.koin.core.module.Module;

import static org.koin.core.context.GlobalContextExtKt.startKoin;

public class MainJavaApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        KoinApplication koin =
            KoinAndroidApplication.create(this)
                .printLogger()
                .modules(Collections.<Module>emptyList());
        startKoin(GlobalContext.INSTANCE, koin);
    }
}
