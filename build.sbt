import sbt.Keys.scalacOptions

lazy val commonSettings = Seq(
  name := "ServiceBusLibrary",
  organization := "com.github.emd",
  version := "0.1-SNAPSHOT",
  scalaVersion := "2.12.8",
  scalacOptions in Compile ++= Seq(
    "-encoding", "UTF-8",
    "-target:jvm-1.8",
    "-feature",
    "-deprecation",
    "-unchecked",
    "-Xlint",
    "-Xfuture",
    "-Ywarn-dead-code",
    "-Ywarn-value-discard",
    "-Ywarn-unused-import",
    "-Ywarn-unused",
    "-Ywarn-nullary-unit"
  ),
  scalacOptions in (Compile, doc) ++= Seq("-groups", "-implicits"),
  javacOptions ++= Seq("-source", "1.8", "-target", "1.8"),
  javacOptions in (Compile, doc) ++= Seq("-notimestamp", "-linksource")
  )

lazy val versions = new {
  val scalatest         = "3.0.8"
  val scalactic         = "3.0.8"
  val mockito_core      = "3.0.0"
  val enumeratum        = "1.5.13"
  val azure_servicebus  = "2.0.0"
  val pprint            = "0.5.5"
  val scala_xml         = "1.2.0"
  val scribe            = "2.7.9"
  val scalamockVersion  = "4.3.0"
  // Disables Microsoft ServiceBus library logging and prevents
  // the "Failed to load class org.slf4j.impl.StaticLoggerBinder" warning
  val sl4j_nop          = "1.7.27"
}

lazy val root = (project in file("."))
  .settings(
    commonSettings,

    publishTo := Some(Resolver.file("file",  new File(Path.userHome.absolutePath+"/.m2/repository"))),

    resolvers ++= Seq(
      // "wfm_public"  at "http://usatl-s-ssvm022.ventyx.us.abb.com:8081/nexus/content/groups/public/",
      "Bintray sbt plugin releases" at "http://dl.bintray.com/sbt/sbt-plugin-releases/"
    ),

    libraryDependencies ++= Seq(
      "com.microsoft.azure" % "azure-servicebus" % versions.azure_servicebus,
      "com.beachape" %% "enumeratum" % versions.enumeratum,
      "com.lihaoyi" %% "pprint" % versions.pprint,
      "org.scalatest" %% "scalatest" % versions.scalatest % Test,
      "org.scalactic" %% "scalactic" % versions.scalactic % Test,
      "org.mockito" % "mockito-core" % versions.mockito_core % Test,
      "org.scalamock" %% "scalamock" % versions.scalamockVersion % Test,
      "com.outr" %% "scribe" % versions.scribe,
      "org.scala-lang.modules" %% "scala-xml" % versions.scala_xml,
      "org.slf4j" % "slf4j-nop" % versions.sl4j_nop)
  )
