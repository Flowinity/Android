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
-dontwarn kotlinx.serialization.DeserializationStrategy
-dontwarn kotlinx.serialization.KSerializer
-dontwarn kotlinx.serialization.Serializable
-dontwarn kotlinx.serialization.builtins.BuiltinSerializersKt
-dontwarn kotlinx.serialization.internal.ArrayListSerializer
-dontwarn kotlinx.serialization.internal.GeneratedSerializer
-dontwarn kotlinx.serialization.internal.PluginGeneratedSerialDescriptor
-dontwarn kotlinx.serialization.internal.StringSerializer
-dontwarn kotlinx.serialization.json.Json
-dontwarn kotlinx.serialization.json.JsonBuilder
-dontwarn kotlinx.serialization.json.JsonKt
-dontwarn kotlinx.serialization.json.JvmStreamsKt
-keepclassmembers,allowobfuscation class * {
  @com.google.gson.annotations.SerializedName <fields>;
}

-keep class * {
  @com.google.gson.annotations.SerializedName <fields>;
}
-keepnames class com.fasterxml.jackson.databind.** { *; }
-dontwarn com.fasterxml.jackson.databind.**