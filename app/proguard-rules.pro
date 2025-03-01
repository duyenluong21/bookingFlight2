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
# Giữ nguyên các model
-keep class com.example.bookingflight.model.** { *; }

# Giữ nguyên các interface
-keep interface com.example.bookingflight.inteface.** { *; }

# Giữ nguyên các lớp liên quan đến Retrofit
-keep class com.example.bookingflight.retrofit.** { *; }

# Làm rối các package còn lại
-keep class com.example.bookingflight.** { *; }

# Thêm các quy tắc để bỏ qua cảnh báo không cần thiết
-dontwarn com.example.bookingflight.model.**
-dontwarn com.example.bookingflight.inteface.**
-dontwarn com.example.bookingflight.retrofit.**

# Quy tắc để không báo lỗi về thiếu các lớp của thư viện bên ngoài
# Bỏ qua cảnh báo cho các lớp không tìm thấy từ thư viện BouncyCastle
# Please add these rules to your existing keep rules in order to suppress warnings.
# This is generated automatically by the Android Gradle plugin.
-dontwarn org.bouncycastle.jsse.BCSSLParameters
-dontwarn org.bouncycastle.jsse.BCSSLSocket
-dontwarn org.bouncycastle.jsse.provider.BouncyCastleJsseProvider
-dontwarn org.conscrypt.Conscrypt$Version
-dontwarn org.conscrypt.Conscrypt
-dontwarn org.conscrypt.ConscryptHostnameVerifier
-dontwarn org.openjsse.javax.net.ssl.SSLParameters
-dontwarn org.openjsse.javax.net.ssl.SSLSocket
-dontwarn org.openjsse.net.ssl.OpenJSSE