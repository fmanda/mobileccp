# ProGuard rules for your Android project

# Keep all classes and methods from your application package
-keep class com.ts.mobileccp.** { *; }

# Keep Retrofit and OkHttp classes
-keep class retrofit2.** { *; }
-keep class okhttp3.** { *; }
-keepattributes Signature
-keepattributes *Annotation

# Keep the annotations for Room
-keep @androidx.room.* class * { *; }
-keep class androidx.room.** { *; }

# Keep Kotlin metadata
-keep class kotlin.Metadata { *; }

# Keep ViewModel and LiveData
-keep class androidx.lifecycle.ViewModel { *; }
-keep class androidx.lifecycle.LiveData { *; }
-keep class androidx.lifecycle.MutableLiveData { *; }

# Keep serialization classes
-keep class kotlinx.serialization.** { *; }

# Keep CircleImageView
-keep class de.hdodenhof.circleimageview.** { *; }

# Suppress warnings for missing classes
-dontwarn com.oracle.svm.core.annotate.Delete
-dontwarn com.oracle.svm.core.annotate.Substitute
-dontwarn com.oracle.svm.core.annotate.TargetClass

# Keep any other classes you want to retain
# Add your additional rules below as needed
