

ThisBuild / scalaVersion := "2.12.11"
ThisBuild / organization := "com.offgridcompute"

lazy val  hello = (project in file("."))
  .settings(
    name := "Hello",
    libraryDependencies += ("com.faunadb" %% "faunadb-scala" % "2.11.1"),
  )
