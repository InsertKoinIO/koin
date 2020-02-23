//package org.koin.sample.androidx;
//
//import android.app.Application;
//import org.koin.android.java.KoinAndroidApplication;
//import org.koin.core.KoinApplication;
//
//import java.util.ArrayList;
//
//import static org.koin.core.context.GlobalContext.start;
//
//public class MainJavaApplication extends Application {
//
//    @Override
//    public void onCreate() {
//        super.onCreate();
//
//        KoinApplication koin = KoinAndroidApplication
//                .create(this)
//                .printLogger()
//                .modules(new ArrayList());
//        start(koin);
//    }
//}
