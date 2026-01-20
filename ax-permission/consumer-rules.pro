# ax-permission library ProGuard rules
# These rules are automatically applied to apps using this library

# Keep Parcelize-annotated classes and their CREATOR fields
-keep @kotlinx.parcelize.Parcelize class com.ax.library.ax_permission.** { *; }
-keepclassmembers class com.ax.library.ax_permission.** implements android.os.Parcelable {
    public static final ** CREATOR;
}
