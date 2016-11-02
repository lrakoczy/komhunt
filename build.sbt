organization := "lrakoczy"
version := "0.1"
scalaVersion := "2.11.6"
scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8")

enablePlugins(sbtdocker.DockerPlugin)

val akkaV = "2.3.9"
val sprayV = "1.3.3"

lazy val app = crossProject.settings(
  unmanagedSourceDirectories in Compile +=
    baseDirectory.value / "shared" / "main" / "scala",
  libraryDependencies ++= Seq(
    "com.lihaoyi" %%% "scalatags" % "0.6.1",
    "com.lihaoyi" %%% "upickle" % "0.4.3",
    "com.lihaoyi" %%% "autowire" % "0.2.5"
  ),
  scalaVersion := "2.11.5"
).jsSettings(
  libraryDependencies ++= Seq(
    "org.scala-js" %%% "scalajs-dom" % "0.9.0",
    "com.lihaoyi" %%% "autowire" % "0.2.5",
    "com.github.karasiq" %%% "scalajs-highcharts" % "1.1.2"
  )
).jvmSettings(
  libraryDependencies ++= Seq(
    "io.spray" %% "spray-can" % sprayV,
    "io.spray" %% "spray-routing" % sprayV,
    "io.spray" %% "spray-json" % "1.3.2",
    "io.spray" %% "spray-client" % sprayV,
    "io.spray" %% "spray-testkit" % sprayV % "test",
    "com.typesafe.akka" %% "akka-actor" % akkaV,
    "com.typesafe.akka" %% "akka-testkit" % akkaV % "test",
    "org.specs2" %% "specs2-core" % "2.3.11" % "test",
    "com.lihaoyi" %% "autowire" % "0.2.5",
    "com.lihaoyi" %% "autowire" % "0.2.5"
  )
)

lazy val appJS = app.js.
  settings(assemblyMergeStrategy in assembly := {
    case PathList("META-INF", xs@_*) => MergeStrategy.discard
    case x => MergeStrategy.first
  })
lazy val appJVM = app.jvm.
  settings(name := "komhunt").
  settings(
    (resources in Compile) += (fastOptJS in(appJS, Compile)).value.data).
  settings(assemblyMergeStrategy in assembly := {
    case PathList("META-INF", xs@_*) => MergeStrategy.discard
    case "application.conf" | "reference.conf" => MergeStrategy.concat
    case x => MergeStrategy.first
  })

Revolver.settings

assembly := {
  (assembly in appJVM in Compile).value
}

assemblyOutputPath in assembly := {
  (target in appJVM in assembly).value / (assemblyJarName in appJVM in assembly).value
}

dockerfile in docker := {
  // The assembly task generates a fat JAR file
  val artifact: File = assembly.value
  val artifactTargetPath = s"/app/${artifact.name}"

  new Dockerfile {
    from("java")
    add(artifact, artifactTargetPath)
    expose(8080)
    entryPoint("java", "-jar", artifactTargetPath)
  }
}