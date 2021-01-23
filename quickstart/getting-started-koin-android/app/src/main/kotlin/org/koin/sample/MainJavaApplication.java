package org.koin.sample;

import android.app.Application;

import org.koin.android.java.KoinAndroidApplication;
import org.koin.core.KoinApplication;
import org.koin.core.logger.Level;

import static org.koin.core.context.DefaultContextExtKt.startKoin;
import static org.koin.sample.AppModuleKt.appModule;

public class MainJavaApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        KoinApplication koinApplication = KoinAndroidApplication
                .create(this, Level.INFO)
                .modules(appModule);

        startKoin(koinApplication);
    }
}
