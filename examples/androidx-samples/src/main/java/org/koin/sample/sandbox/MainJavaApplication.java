package org.koin.sample.sandbox;

import android.app.Application;

import org.koin.android.java.KoinAndroidApplication;
import org.koin.core.KoinApplication;
import org.koin.core.logger.Level;
import org.koin.core.module.Module;

import java.util.Collections;

import static org.koin.core.context.DefaultContextExtKt.startKoin;


public class MainJavaApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        KoinApplication koin =
                KoinAndroidApplication.create(this)
                        .printLogger(Level.INFO)
                        .modules(Collections.<Module>emptyList());
        startKoin(koin);
    }
}
