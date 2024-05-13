ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.3.0"

lazy val root = (project in file("."))
  .settings(
    name := "Language Diary"
  )
libraryDependencies += "org.scalafx" % "scalafx_3" % "20.0.0-R31"
libraryDependencies += "org.controlsfx" % "controlsfx" % "11.1.2"
//libraryDependencies += "io.github.cquiroz" %% "scala-java-time" % "2.0.0-RC3"
libraryDependencies += "io.github.cquiroz" %% "scala-java-time" % "2.5.0"
libraryDependencies += "com.calendarfx" % "view" % "11.12.6"

val circeVersion = "0.14.1"
libraryDependencies += "io.circe" %% "circe-core" % circeVersion
libraryDependencies += "io.circe" %% "circe-generic" % circeVersion
libraryDependencies += "io.circe" %% "circe-parser" % circeVersion

libraryDependencies += "com.lihaoyi" %% "upickle" % "3.0.0"
