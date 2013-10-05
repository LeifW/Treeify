scalaVersion := "2.10.2"

scalacOptions ++= Seq("-unchecked", "-deprecation", "-feature", "-language:postfixOps")

resolvers += "funes" at "http://funesrdf.info/maven"

libraryDependencies ++=
  "org.scardf" %% "scardf" % "0.6-SNAPSHOT" ::
  "org.scalatest" %% "scalatest" % "2.0.RC1" % "test" ::
  "org.json4s" %% "json4s-core" % "3.2.5" ::
  "org.json4s" %% "json4s-native" % "3.2.5" % "provided" ::
  Nil
