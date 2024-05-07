lazy val functionsFrameworkVersion = "1.1.0"
lazy val openTelemetryVersion = "1.37.0"
lazy val scalaTestVersion = "3.2.18"

lazy val commonSettings = Seq(
  version := "0.1.0-SNAPSHOT",
  scalaVersion := "2.13.11",
  scalacOptions ++= Seq("-target", "11"),
  //  resolvers ++= repositories,
  //  resolvers += Resolver.sonatypeRepo("releases"),
  //  credentials += creds,
  assembly / assemblyMergeStrategy := {
    case PathList("scala", xs@_*) => MergeStrategy.first
    case PathList("org", "apache", xs@_*) => MergeStrategy.first
    case PathList(ps@_*) if ps.last endsWith ".html" => MergeStrategy.first
    case PathList(ps@_*) if ps.last endsWith ".jar" => MergeStrategy.first
    case "application.conf" => MergeStrategy.concat
    case value => {
      val mergeStrategy = (assembly / assemblyMergeStrategy).value

      mergeStrategy(value) match {
        case MergeStrategy.deduplicate => MergeStrategy.first
        case strategy => strategy
      }
    }
  },
  //  testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework")
)


lazy val root = (project in file("."))
  .settings(
    name := "opentelemetry-intro-gcf-scala",
    commonSettings,
    libraryDependencies ++= Seq(
      "org.scalatest" %% "scalatest" % scalaTestVersion % Test,
      "com.google.cloud.functions" % "functions-framework-api" % functionsFrameworkVersion,
      "com.google.cloud" % "google-cloud-monitoring" % "2.0.0",
      "com.google.cloud.opentelemetry" % "exporter-metrics" % "0.28.0",
      "io.opentelemetry" % "opentelemetry-api" % openTelemetryVersion,
      "io.opentelemetry" % "opentelemetry-sdk-metrics" % openTelemetryVersion,
      "io.opentelemetry" % "opentelemetry-sdk-extension-autoconfigure-spi" % openTelemetryVersion,
      "io.opentelemetry.contrib" % "opentelemetry-gcp-resources" % "1.35.0-alpha",
    ),
    assembly / assemblyOutputPath := new File("target/opentelemetry-intro-gcf-scala.jar"),
    ZipTask.settings
  )

