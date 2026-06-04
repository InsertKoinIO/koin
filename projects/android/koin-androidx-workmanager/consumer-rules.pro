# Koin AndroidX WorkManager — consumer R8/ProGuard rules
# Shipped with koin-androidx-workmanager AAR. Auto-applied to consumer apps.

# Silence R8 warnings about Koin internals. See koin-android/consumer-rules.pro for context.
-dontwarn org.koin.**

# User's ListenableWorker subclasses are constructed reflectively by WorkManager.
# androidx.work ships its own consumer rules that keep Worker subclasses. Users do
# NOT need additional Koin-specific rules for their Workers.
