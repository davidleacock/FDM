ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.12"

lazy val root = (project in file("."))
  .settings(
    name := "FDM",
    libraryDependencies += "org.typelevel" %% "cats-core" % "2.10.0"
  )
