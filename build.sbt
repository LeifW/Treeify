scalaVersion := "2.10.3"

scalacOptions ++= Seq("-unchecked", "-deprecation", "-feature", "-language:postfixOps")

resolvers += Resolver.sonatypeRepo("snapshots")

resolvers += "funes" at "http://funesrdf.info/maven"

libraryDependencies ++=
  "org.scardf" %% "scardf" % "0.6-SNAPSHOT" ::
  "org.scalaz" %% "scalaz-core" % "7.0.5" ::
  "org.pelotom" %% "effectful" % "1.0.0" ::
  "org.json4s" %% "json4s-core" % "3.2.5" ::
  "org.json4s" %% "json4s-native" % "3.2.5" % "provided" ::
  "org.scalatest" %% "scalatest" % "2.0" % "test" ::
  Nil
