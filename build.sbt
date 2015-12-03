import play.PlayImport._
import sbt.Project.projectToRef

lazy val clients = Seq(scalaExercisesClient)
lazy val scalaV = "2.11.7"
lazy val doobieVersion = "0.2.3"

lazy val commonSettings = Seq(
  scalaVersion := scalaV,
  wartremoverWarnings in (Compile, compile) ++= Warts.unsafe,
  resolvers ++= Seq(
    "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases",
    Resolver.sonatypeRepo("snapshots")
  )
)

lazy val scalaExercisesServer = (project in file("scala-exercises-server"))
  .settings(commonSettings: _*)
  .settings(
    routesImport += "config.Routes._",
    routesGenerator := InjectedRoutesGenerator,
    scalaJSProjects := clients,
    pipelineStages := Seq(scalaJSProd, gzip),
    libraryDependencies ++= Seq(
      filters,
      jdbc,
      evolutions,
      cache,
      ws,
      specs2 % Test,
      "org.slf4j" % "slf4j-nop" % "1.6.4",
      "com.vmunier" %% "play-scalajs-scripts" % "0.2.1",
      "com.lihaoyi" %% "upickle" % "0.2.8",
      "org.webjars" %% "webjars-play" % "2.3.0",
      "org.webjars" % "bootstrap-sass" % "3.2.0",
      "org.webjars" % "jquery" % "2.1.1",
      "org.webjars" % "font-awesome" % "4.1.0",
      "com.tristanhunt" %% "knockoff" % "0.8.3",
      "org.scala-lang" % "scala-compiler" % scalaV,
      "org.clapper" %% "classutil" % "1.0.5",
      "org.tpolecat" %% "doobie-core" % doobieVersion,
      "org.tpolecat" %% "doobie-contrib-hikari" % doobieVersion,
      "org.tpolecat" %% "doobie-contrib-postgresql" % doobieVersion,
      "org.tpolecat" %% "doobie-contrib-specs2" % doobieVersion,
      "com.toddfast.typeconverter" % "typeconverter" % "1.0",
      "org.typelevel" %% "scalaz-specs2" % "0.3.0" % "test"
    )

).enablePlugins(PlayScala).
    aggregate(clients.map(projectToRef): _*).
    dependsOn(scalaExercisesSharedJvm, scalaExercisesContent)

lazy val scalaExercisesClient = (project in file("scala-exercises-client"))
  .settings(commonSettings: _*)
  .settings(
    persistLauncher := true,
    persistLauncher in Test := false,
    sourceMapsDirectories += scalaExercisesSharedJs.base / "..",
    jsDependencies += RuntimeDOM,
    testFrameworks += new TestFramework("utest.runner.Framework"),
    libraryDependencies ++= Seq(
      "org.scala-js" %%% "scalajs-dom" % "0.8.1",
      "com.lihaoyi" %%% "scalatags" % "0.5.2",
      "com.lihaoyi" %%% "scalarx" % "0.2.8",
      "be.doeraene" %%% "scalajs-jquery" % "0.8.0",
      "com.lihaoyi" %%% "utest" % "0.3.1" % "test",
      "com.lihaoyi" %%% "upickle" % "0.2.8"
    )
  ).enablePlugins(ScalaJSPlugin, ScalaJSPlay).
      dependsOn(scalaExercisesSharedJs)

lazy val scalaExercisesShared = (crossProject.crossType(CrossType.Pure) in file("scala-exercises-shared"))
  .settings(commonSettings: _*)
  .settings(
      libraryDependencies ++= Seq(
        "org.scalatest" %% "scalatest" % "2.2.4",
        "org.scalaz" %% "scalaz-core" % "7.1.4",
        "org.scalaz" %% "scalaz-concurrent" % "7.1.4",
        "org.spire-math" %% "cats" % "0.4.0-SNAPSHOT" changing()
      )
    ).
    jsConfigure(_ enablePlugins ScalaJSPlay).
    jsSettings(sourceMapsBase := baseDirectory.value / "..")

lazy val scalaExercisesSharedJvm = scalaExercisesShared.jvm
lazy val scalaExercisesSharedJs = scalaExercisesShared.js

lazy val scalaExercisesContent = (project in file("scala-exercises-content"))
  .settings(commonSettings: _*)
  .settings(
      libraryDependencies ++= Seq(
        "org.scalatest" %% "scalatest" % "2.2.4",
        "org.scalaz" %% "scalaz-core" % "7.1.4"
      ),
      unmanagedResourceDirectories in Compile += baseDirectory.value / "src" / "main" / "scala"
    ).dependsOn(scalaExercisesSharedJvm)

// loads the jvm project at sbt startup
onLoad in Global := (Command.process("project scalaExercisesServer", _: State)) compose (onLoad in Global).value