addSbtPlugin("org.scoverage" % "sbt-scoverage" % "1.6.0")

// https://stackoverflow.com/questions/26213298/unable-to-resolve-scalajs-sbt-plugin-dependency#26215209
resolvers += Resolver.url(
  "bintray-sbt-plugin-releases",
  url("https://dl.bintray.com/content/sbt/sbt-plugin-releases")
)(Resolver.ivyStylePatterns)
