val dottyVersion = "0.4.0-RC1"
val scala212Version = "2.12.4"
val scala211Version = "2.11.11"

lazy val root = (project in file(".")).
  settings(
    name := "squid-type-experiments",
    version := "0.1.0",
    
    libraryDependencies += "com.novocode" % "junit-interface" % "0.11" % "test",
    
    // Scalatest not supported by Dotty
    //libraryDependencies += "junit" % "junit-dep" % "4.10" % "test",
    //libraryDependencies += "org.scalatest" % "scalatest_2.11" % "2.2.0" % "test",
    //libraryDependencies += "org.scalatest" %% "scalatest" % "2.2.0" % "test",
    
    // To make the default compiler and REPL use Dotty
    scalaVersion := dottyVersion,
    
    // To cross compile with Dotty and Scala 2
    crossScalaVersions := Seq(dottyVersion, scala211Version, scala212Version)
  )

lazy val root_2_12 = root.settings(scalaVersion := scala212Version)

lazy val macros = (project in file("macros")).dependsOn(root_2_12).
  settings(
    scalaVersion := scala212Version,
    libraryDependencies += "org.scala-lang" % "scala-reflect" % scalaVersion.value
    //crossScalaVersions := Seq(scala211Version, scala212Version)
  )
