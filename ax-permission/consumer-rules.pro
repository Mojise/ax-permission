# ax-permission library ProGuard rules
# These rules are automatically applied to apps using this library

# Keep all Parcelable classes in the library
-keep class com.ax.library.ax_permission.ax.AxPermissionGlobalConfigurations { *; }
-keep class com.ax.library.ax_permission.ax.AxPermissionGlobalConfigurations$Creator { *; }

# Keep Parcelize-generated CREATOR fields
-keepclassmembers class com.ax.library.ax_permission.** implements android.os.Parcelable {
    public static final ** CREATOR;
}

# Keep all classes that implement Parcelable
-keep class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}

# Keep Kotlin Parcelize annotations
-keep @kotlinx.parcelize.Parcelize class *

# Keep class members annotated with @Parcelize
-keepclassmembers class * {
    @kotlinx.parcelize.Parcelize <fields>;
}
