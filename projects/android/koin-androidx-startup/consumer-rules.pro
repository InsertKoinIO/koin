# Koin AndroidX Startup — consumer R8/ProGuard rules
# Shipped with koin-androidx-startup AAR. Auto-applied to consumer apps.

# AndroidManifest.xml references KoinInitializer by name (see AndroidManifest.xml).
# AGP's manifest-merge step normally keeps manifest-referenced classes, but explicit
# keep is safer under aggressive shrink configs and against AGP behavior changes.
-keep class org.koin.androix.startup.KoinInitializer { *; }
-keep class * implements org.koin.androix.startup.KoinStartup
