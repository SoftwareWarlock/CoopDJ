name := "CoopDJ"

import android.Keys._
android.Plugin.androidBuild
javacOptions ++= Seq("-source", "1.7", "-target", "1.7")
scalaVersion := "2.11.7"
scalacOptions in Compile += "-feature"

proguardCache in Android ++= Seq("org.scaloid")

proguardOptions in Android ++= Seq("-dontobfuscate", "-dontoptimize", "-keepattributes Signature", "-printseeds target/seeds.txt", "-printusage target/usage.txt",
	"-dontwarn scala.collection.**", // required from Scala 2.11.4
	"-dontwarn org.scaloid.**", // this can be omitted if current Android Build target is android-16
    "-keepclasseswithmembernames,includedescriptorclasses class * { native <methods>; }"
)

libraryDependencies += "org.scaloid" %% "scaloid" % "4.0"

localAars in Android += baseDirectory.value / "lib" / "spotify-auth-1.0.0-beta10.aar"
localAars in Android += baseDirectory.value / "lib" / "spotify-player-1.0.0-beta10.aar"
resolvers += "jitpack" at "https://jitpack.io"
libraryDependencies += "com.github.kaaes" % "spotify-web-api-android" % "b0a4c2dad3"

run <<= run in Android
install <<= install in Android

retrolambdaEnable in Android := false // turning it on significantly increases the build time

libraryDependencies += "org.scalatest" %% "scalatest" % "2.2.4"
libraryDependencies += "org.scalamock" %% "scalamock-scalatest-support" % "3.2"
