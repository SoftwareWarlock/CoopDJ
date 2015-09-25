resolvers += Resolver.url(
  "bintray-sbt-plugin-releases",
  url("http://dl.bintray.com/content/sbt/sbt-plugin-releases"))(
      Resolver.ivyStylePatterns)

addSbtPlugin("me.lessis" % "bintray-sbt" % "0.1.2")
addSbtPlugin("com.hanhuy.sbt" % "android-sdk-plugin" % "1.4.14")

libraryDependencies += "net.sf.proguard" % "proguard-base" % "5.1"
