# Koin Compose ViewModel — consumer R8/ProGuard rules
# Shipped with the koin-compose-viewmodel AAR. Auto-applied to consumer apps.

# Silence R8 warnings about Koin internals (Kotlin reflection metadata, optional interop).
-dontwarn org.koin.**

# NOTE: koinViewModel() / koinNavViewModel() resolution is compile-time (reified inline get(),
# JVM Class.getName() keying) — no runtime reflection over your ViewModel constructors, so you do
# NOT need to keep your ViewModels on Koin's behalf. SavedStateHandle comes from androidx
# CreationExtras at creation time, not from Koin reflection.
# For what you DO need to keep (your saved-state classes for process death), see the
# R8 / ProGuard guide: https://insert-koin.io/docs/reference/koin-android/r8-proguard
