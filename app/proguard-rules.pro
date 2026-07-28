# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Preserve line number information for readable Crashlytics stack traces.
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# jaudiotagger references java.awt / javax.imageio classes that don't exist on Android.
# These are never used at runtime on Android; suppress the R8 missing-class warnings.
-dontwarn java.awt.image.BufferedImage
-dontwarn javax.imageio.ImageIO
-dontwarn javax.imageio.stream.ImageInputStream

# Navigation Safe Args resolves custom argType classes via Class.forName() at runtime
# using the literal class name from the nav XML. R8 sees no static reference to these
# classes, so it renames/strips them, breaking NavInflater with ClassNotFoundException.
# Keep any Parcelable class used as a nav-graph argument, and its generated CREATOR.
-keep class mazentas.playme.music.model.Genre { *; }
-keepclassmembers class mazentas.playme.music.model.Genre {
    public static final android.os.Parcelable$Creator CREATOR;
}