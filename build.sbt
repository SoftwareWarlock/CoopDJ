name := "CoopDJ"

import android.Keys._
android.Plugin.androidBuild
javacOptions ++= Seq("-source", "1.7", "-target", "1.7")
fork in run := true
javaOptions in run ++= Seq(
    "-Xms256M", "-Xmx2G", "-XX:MaxPermSize=1024M", "-XX:+UseConcMarkSweepGC")
scalaVersion := "2.11.7"
scalacOptions in Compile += "-feature"

resolvers += bintray.Opts.resolver.jcenter
libraryDependencies += aar("com.google.android" % "multidex" % "0.1")

proguardCache in Android ++= Seq("org.scaloid")

dexMulti in Android := true
dexMaxHeap in Android := "2G"

dexMinimizeMainFile in Android := true

dexMainFileClasses in Android := Seq(
  "emptyflash/coopdj/CoopDJApplication.class",
  "android/support/multidex/BuildConfig.class",
  "android/support/multidex/MultiDex$V14.class",
  "android/support/multidex/MultiDex$V19.class",
  "android/support/multidex/MultiDex$V4.class",
  "android/support/multidex/MultiDex.class",
  "android/support/multidex/MultiDexApplication.class",
  "android/support/multidex/MultiDexExtractor$1.class",
  "android/support/multidex/MultiDexExtractor.class",
  "android/support/multidex/ZipUtil$CentralDirectory.class",
  "android/support/multidex/ZipUtil.class"
)


apkbuildExcludes in Android ++= Seq(
  "META-INF/MANIFEST.MF",
  "META-INF/LICENSE.txt",
  "META-INF/LICENSE",
  "META-INF/NOTICE.txt",
  "META-INF/NOTICE"
)

libraryDependencies += "org.scaloid" %% "scaloid" % "4.0"

localAars in Android += baseDirectory.value / "lib" / "spotify-auth-1.0.0-beta10.aar"
localAars in Android += baseDirectory.value / "lib" / "spotify-player-1.0.0-beta10.aar"
resolvers += "jitpack" at "https://jitpack.io"
libraryDependencies += "com.github.kaaes" % "spotify-web-api-android" % "9df5689fd0"

libraryDependencies += "com.github.SoftwareWarlock" % "hbc" % "hbc-2.2.3"
libraryDependencies += "com.github.SoftwareWarlock.hbc" % "hbc-twitter4j" % "hbc-2.2.3"
libraryDependencies += "com.github.SoftwareWarlock.hbc" % "hbc-core" % "hbc-2.2.3"

proguardOptions in Android ++= Seq("-dontobfuscate", "-dontoptimize", "-printseeds target/seeds.txt", "-printusage target/usage.txt",
	"-dontwarn scala.collection.**", // required from Scala 2.11.4
	"-dontwarn org.scaloid.**", // this can be omitted if current Android Build target is android-16
    //"-keepclasseswithmembernames,includedescriptorclasses class * { native <methods>; }",
	"-keep,includedescriptorclasses class com.twitter.hbc.ClientBuilder { *; }",
	"-keep,includedescriptorclasses class com.twitter.hbc.core.Constants { *; }",
	"-keep,includedescriptorclasses class com.twitter.hbc.core.endpoint.StatusesFilterEndpoint { *; }",
	"-keep,includedescriptorclasses class com.twitter.hbc.httpclient.BasicClient { *; }",
	"-keep,includedescriptorclasses class com.twitter.hbc.core.processor.StringDelimitedProcessor { *; }",
	"-keep,includedescriptorclasses class com.twitter.hbc.httpclient.auth.Authentication { *; }",
	"-keep,includedescriptorclasses class com.twitter.hbc.httpclient.auth.OAuth1 { *; }",
	"-keep,includedescriptorclasses class com.twitter.hbc.twitter4j.Twitter4jStatusClient { *; }",
	"-keep,includedescriptorclasses class com.twitter.hbc.twitter4j.handler.StatusStreamHandler { *; }",
	"-keep,includedescriptorclasses class com.twitter.hbc.twitter4j.message.DisconnectMessage { *; }",
	"-keep,includedescriptorclasses class com.twitter.hbc.twitter4j.message.StallWarningMessage { *; }",
	"-keep,includedescriptorclasses class twitter4j.StallWarning { *; }",
	"-keep,includedescriptorclasses class twitter4j.Status { *; }",
	"-keep,includedescriptorclasses class twitter4j.StatusDeletionNotice { *; }",
	"-keep,includedescriptorclasses class twitter4j.StatusListener { *; }",
	"-keep,includedescriptorclasses class twitter4j.conf.* { *; }",
	"-keep class com.spotify.sdk.android.** { *; }",
	"-keep,includedescriptorclasses class com.spotify.sdk.android.** { native <methods>; }",
	"-keepattributes Exceptions,InnerClasses,Signature,Deprecated, SourceFile,LineNumberTable,*Annotation*,EnclosingMethod",
	"-dontskipnonpubliclibraryclasses",
	"-dontwarn **",
	"-dontnote **"
)

run <<= run in Android
install <<= install in Android

retrolambdaEnable in Android := false // turning it on significantly increases the build time

libraryDependencies += "org.scalatest" %% "scalatest" % "2.2.4"
libraryDependencies += "org.scalamock" %% "scalamock-scalatest-support" % "3.2"
dependencyOverrides += "org.scalatest" %% "scalatest" % "2.2.4"
