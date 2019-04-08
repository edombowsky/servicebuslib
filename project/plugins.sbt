addSbtPlugin("org.scoverage" % "sbt-scoverage" % "1.5.1")

addSbtPlugin("com.timushev.sbt" % "sbt-updates" % "0.3.4")

// https://stackoverflow.com/questions/26213298/unable-to-resolve-scalajs-sbt-plugin-dependency#26215209
resolvers += Resolver.url(
  "bintray-sbt-plugin-releases",
  url("https://dl.bintray.com/content/sbt/sbt-plugin-releases")
)(Resolver.ivyStylePatterns)
