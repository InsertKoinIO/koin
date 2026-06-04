# Koin Android — consumer R8/ProGuard rules
# Shipped with koin-android AAR. Auto-applied to consumer apps.

# Silence R8 warnings about Koin internals. Koin doesn't ship JVM-only stubs into
# Android consumers, but some transitive paths (Kotlin reflection metadata, optional
# Java interop in org.koin.java) can produce spurious "missing class" warnings.
-dontwarn org.koin.**

# User's Fragment subclasses are loaded via Class.forName in KoinFragmentFactory.
# Users must keep their own Fragment classes (typically via @Keep, layout references,
# or app-side -keep rules). Documented in the R8 guide.
