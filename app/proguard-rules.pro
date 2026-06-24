# Keep line numbers in stack traces for easier debugging
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# Room — keep entity classes and DAOs so Room's generated code can find them
-keep class com.sadxlab.notescompose.data.local.** { *; }

# Domain models — keep field names for serialisation/mapping
-keep class com.sadxlab.notescompose.domain.model.** { *; }

# Hilt — generated components; Hilt ships its own rules but this is a safety net
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }

# Kotlin coroutines
-keepclassmembernames class kotlinx.** {
    volatile <fields>;
}

# Lottie
-dontwarn com.airbnb.lottie.**
-keep class com.airbnb.lottie.** { *; }

# Google Play In-App Update
-keep class com.google.android.play.core.** { *; }
