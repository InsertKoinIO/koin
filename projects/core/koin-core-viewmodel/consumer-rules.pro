# Koin Core ViewModel — consumer R8/ProGuard rules
# Shipped with the koin-core-viewmodel AAR. Auto-applied to consumer apps.

# Silence R8 warnings about Koin internals (Kotlin reflection metadata, optional interop).
-dontwarn org.koin.**

# NOTE: Koin's ViewModel resolution (viewModelOf / get<T>()) is resolved at COMPILE TIME
# (reified inline get(), JVM Class.getName() keying) — it uses no runtime reflection over your
# ViewModel constructors. You do NOT need to keep your ViewModels on Koin's behalf.
# SavedStateHandle is supplied by androidx CreationExtras at creation time, not by Koin reflection.
# For what you DO need to keep (your @Parcelize/Serializable saved-state classes for process death),
# see the R8 / ProGuard guide: https://insert-koin.io/docs/reference/koin-android/r8-proguard
