name := "experiment-emde-boas-tree"

version := "0.1"

scalaVersion := "2.13.3"

libraryDependencies += "io.monix" %% "minitest" % "2.8.2" % "test"

testFrameworks += new TestFramework("minitest.runner.Framework")

enablePlugins(JmhPlugin)
