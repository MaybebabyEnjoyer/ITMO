import Dependencies.{quill, *}

ThisBuild / scalaVersion     := "2.13.11"
ThisBuild / version          := "0.1.0-SNAPSHOT"

assembly / mainClass := Some("ru.app.Application")
assembly / assemblyMergeStrategy := {
  case PathList("META-INF", _*) => MergeStrategy.discard
  case _                        => MergeStrategy.first
}

assembly / assemblyMergeStrategy := {
  case PathList("META-INF", "maven", "org.webjars", "swagger-ui", "pom.properties") =>
    MergeStrategy.singleOrError
  case PathList("META-INF", "resources", "webjars", "swagger-ui", _*)               =>
    MergeStrategy.singleOrError
  case PathList("META-INF", _*)                                                     => MergeStrategy.discard // Optional, but usually required
  case x                                                                            =>
    val oldStrategy = (assembly / assemblyMergeStrategy).value
    oldStrategy(x)
}

Compile / compile / scalacOptions ++= Seq(
  "-Werror",
  "-Wdead-code",
  "-Wextra-implicit",
  "-Wnumeric-widen",
  "-Wunused",
  "-Wvalue-discard",
  "-Xlint",
  "-Xlint:-byname-implicit",
  "-Xlint:-implicit-recursion",
  "-unchecked",
  "-feature"
)

lazy val root = (project in file("."))
  .settings(
    name := "banekParser",
    libraryDependencies ++= List(
      scalaTest,
      jsoup,
      newtype,
      `cats-effect`,
      h2,
      logback,
      liquibase,
      quill,
      purecfg,
      mock,
      mockito
    ) ++ circe.modules ++ tapir.modules ++ doobie.modules ++ sttp.modules ++ tethys.modules ++ tofu.modules,
    scalacOptions += "-Ymacro-annotations",
    addCompilerPlugin("org.typelevel" % "kind-projector" % "0.13.2" cross CrossVersion.full),
    addCompilerPlugin("com.olegpy" %% "better-monadic-for" % "0.3.1")
  )
