import sbt.Keys.scalacOptions

lazy val commonSettings = Seq(
  organization := "com.github.emd",
  version := "0.0.1",
  scalaVersion := "2.12.8",
  //scalacOptions ++= Seq("-deprecation", "-unchecked", "-feature"),
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
  val scalatest         = "3.0.5"
  val scalactic         = "3.0.5"
  val mockito_core      = "2.24.0"
  val enumeratum        = "1.5.13"
  val azure_servicebus  = "2.0.0"
  //val scala_arm         = "2.0"
  val pprint            = "0.5.3"
  val scala_xml         = "1.1.1"
  val pureconfig        = "0.10.2"
  val scribe            = "2.7.2"
  val upickle           = "0.7.1"
  val scalafxmlVersion  = "0.4"
  val scalamockVersion  = "4.1.0"
  val scalafxExtras     = "0.3.0"
  val scalafxmlCoreSfx8 = "0.4"
  // TODO: may want to use 8.0.102.R11 because scalafxml depend on it (sbt> evicted)
  val scalafxVersion    = "11-R16"
  // https://ymasory.github.io/OrangeExtensions/
  val orange_extensions = "1.3.0"
  // Disables Microsoft ServiceBus library logging and prevents
  // the "Failed to load class org.slf4j.impl.StaticLoggerBinder" warning
  val sl4j_nop          = "1.7.25"
}


// Determine OS version of JavaFX binaries to use Java 11
// Reference: https://groups.google.com/forum/#!topic/scalafx-users/DmV7dx83ogc
lazy val osName = System.getProperty("os.name") match {
  case n if n.startsWith("Linux")   => "linux"
  case n if n.startsWith("Mac")     => "mac"
  case n if n.startsWith("Windows") => "win"
  case _ => throw new Exception("Unknown platform!")
}

lazy val javaFXModules = Seq( "base", "controls", "fxml", "graphics", "media", "swing", "web" )

// Add dependency on ScalaFX library
libraryDependencies += "org.scalafx" %% "scalafx" % versions.scalafxVersion

libraryDependencies ++= javaFXModules.map( m => "org.openjfx" % s"javafx-$m" % "11" classifier osName )

lazy val root = (project in file("."))
  .settings(
    commonSettings,
    name := "ServiceBusLibrary",

    // mainClass in assembly := Some("com.abb.servicebuslib.ServiceBusHelper"),
    // assemblyJarName in assembly := "ServiceBusHelper.jar",

    // set the main class for packaging the main jar
    // 'run' will still auto-detect and prompt
    // change Compile to Test to set it for the test jar
    // mainClass in (Compile, packageBin) := Some("com.abb.servicebuslib.ServiceBusHelper"),

    // set the main class for the main 'run' task
    // change Compile to Test to set it for 'test:run'
    // mainClass in (Compile, run) := Some("com.abb.servicebuslib.ServiceBusHelper"),

    resolvers ++= Seq(
      "wfm_public"  at "http://usatl-s-ssvm022.ventyx.us.abb.com:8081/nexus/content/groups/public/",
      "Bintray sbt plugin releases" at "http://dl.bintray.com/sbt/sbt-plugin-releases/",
      "Bintray JMetro" at "https://dl.bintray.com/dukke/maven/"
    ),

    addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.1" cross CrossVersion.full),

    libraryDependencies ++= Seq(
      "com.yuvimasory" % "orange-extensions" % versions.orange_extensions,
      "com.microsoft.azure" % "azure-servicebus" % versions.azure_servicebus,
      "com.beachape" %% "enumeratum" % versions.enumeratum,
      "com.lihaoyi" %% "pprint" % versions.pprint,
      "org.scalatest" %% "scalatest" % versions.scalatest % Test,
      "org.scalactic" %% "scalactic" % versions.scalactic % Test,
      "org.mockito" % "mockito-core" % versions.mockito_core % Test,
      "org.scalamock" %% "scalamock" % versions.scalamockVersion % Test,
      //"com.jsuereth" %% "scala-arm" % versions.scala_arm,
      "com.lihaoyi" %% "upickle" % versions.upickle,
      "com.github.pureconfig" %% "pureconfig" % versions.pureconfig,
      "com.outr" %% "scribe" % versions.scribe,
      "org.scala-lang.modules" %% "scala-xml" % versions.scala_xml,
      "org.slf4j" % "slf4j-nop" % versions.sl4j_nop,
      "org.scalafx" %% "scalafxml-core-sfx8" % versions.scalafxmlCoreSfx8,
      "org.scalafx" %% "scalafx-extras" % versions.scalafxExtras)
  )
