# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in D:\Professional\android\androidsdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript imanager
# class:
#-keepclassmembers class fqcn.of.javascript.imanager.for.webview {
#   public *;
#}
#表示混淆时不使用大小写混合类名
-dontusemixedcaseclassnames
#表示不跳过library中的非public的类
-dontskipnonpubliclibraryclasses
#打印混淆的详细信息
-verbose

# Optimization is turned off by default. Dex does not like code run
# through the ProGuard optimize and preverify steps (and performs some
# of these optimizations on its own).
-dontoptimize
##表示不进行校验,这个校验作用 在java平台上的
-dontpreverify
# Note that if you want to enable optimization, you cannot just
# include optimization flags in your own project configuration file;
# instead you will need to point to the
# "proguard-android-optimize.txt" file instead of this one from your
# project.properties file.

-keepattributes *Annotation*
-keep public class com.google.vending.licensing.ILicensingService
-keep public class com.android.vending.licensing.ILicensingService

-keep public class * extends android.app.Fragment
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class com.android.vending.licensing.ILicensingService
-keep public class * extends android.support.v4.app.Fragment


# For native methods, see http://proguard.sourceforge.net/manual/examples.html#native
-keepclasseswithmembernames class * {
    native <methods>;
}
# For enumeration classes, see http://proguard.sourceforge.net/manual/examples.html#enumerations
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keepclassmembers class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator CREATOR;
}

-keepclassmembers class **.R$* {
    public static <fields>;
}

# The support library contains references to newer platform versions.
# Don't warn about those in case this app is linking against an older
# platform version.  We know about them, and they are safe.
-dontwarn android.support.**

# Understand the @Keep support annotation.
-keep class android.support.annotation.Keep

-keep @android.support.annotation.Keep class * {*;}

-keepclasseswithmembers class * {
    @android.support.annotation.Keep <methods>;
}

-keepclasseswithmembers class * {
    @android.support.annotation.Keep <fields>;
}

-keepclasseswithmembers class * {
    @android.support.annotation.Keep <init>(...);
}

-keep public class * {
    public protected <fields>;
    public protected <methods>;
}


#忽略警告
-ignorewarnings
#保证是独立的jar,没有任何项目引用,如果不写就会认为我们所有的代码是无用的,从而把所有的代码压缩掉,导出一个空的jar
-dontshrink


-keep class com.google.**
-keepclassmembernames  class com.google.**
-keep  class com.google.** {
  *;
}
-keep class com.squareup.okhttp.**
-keepclassmembernames class com.squareup.okhttp.**
-keep  class com.squareup.okhttp.** {
  *;
}
-keep class io.grpc.**
-keepclassmembernames class io.grpc.**
-keep  class io.grpc.** {
  *;
}
-keep class okio.**
-keepclassmembernames class okio.**
-keep  class okio.** {
  *;
}
-keep class de.greenrobot.dao.**
-keepclassmembernames class  de.greenrobot.dao.**
-keep  class de.greenrobot.dao.** {
  *;
}
-keep class com.littlec.sdk.grpcserver.**
-keepclassmembernames class  com.littlec.sdk.grpcserver.**
-keep  class com.littlec.sdk.grpcserver.** {
  *;
}
-keep class com.littlec.sdk.database.dao.**
-keepclassmembernames class  com.littlec.sdk.database.dao.**
-keep  class com.littlec.sdk.database.dao.**{
 *;
}
-keep class com.littlec.sdk.database.entity.**
-keepclassmembernames class  com.littlec.sdk.database.entity.**
-keep  class com.littlec.sdk.database.entity.**{
 *;
}
#-keep class com.littlec.sdk.network.**
#-keepclassmembernames class  com.littlec.sdk.network.**
#-keep  class com.littlec.sdk.network.** {
# *;
#}


#内部类的方法名称不能被混淆  broadcastreceiver  将sdk里面含有内部方法的全部忽略
-keep class com.littlec.sdk.utils.NetworkMonitor$*{
     public <fields>;
      public <methods>;
}
-keep class ExcWatchDog$*{
      public <fields>;
      public <methods>;
}
-keep class com.littlec.sdk.common.DispatchController$*{
      public <fields>;
      public <methods>;
}
-keep class com.littlec.sdk.chat.core.launcher.impl.LCPacketWriter
-keep class com.littlec.sdk.chat.core.launcher.impl.LCPacketWriter$*{
      public <fields>;
      public <methods>;
}

-keep class * implements  io.grpc.stub.StreamObserver{
    public <fields>;
    public <methods>;
 public void onError(Throwable);
 public void onCompleted();
}



-keepattributes Exceptions, InnerClasses, Signature, Deprecated,SourceFile, LineNumberTable, *Annotation*, EnclosingMethod


-keepclassmembers class * extends java.lang.Enum {
    <fields>;
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keep public enum LCMessage {
   *;
}

