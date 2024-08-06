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

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile
-dontshrink
-dontoptimize
-keep class  com.ruthal.live.cricket.app.models.** { *; }
-keep class  com.ruthal.live.cricket.app.fixtures.models.** { *; }

-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-dontskipnonpubliclibraryclassmembers
-verbose

-keepattributes Annotation,Signature,EnclosingMethod

-keepclassmembers class com.ruthal.live.cricket.app.constants.ApplicationConstants {
public static final *;
}

-keepclassmembers class com.ruthal.live.cricket.app.constants.ApplicationConstants {
final *;
}

-keepclassmembers class com.ruthal.live.cricket.app.constants.UnchangedConstants {
public static final *;
}

-keepclassmembers class com.ruthal.live.cricket.app.constants.UnchangedConstants {
final *;
}