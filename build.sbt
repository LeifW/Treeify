// scalaVersion := "2.9.0-1"

resolvers += "Twitter Repo" at "http://maven.twttr.com"

libraryDependencies ++= Seq(
  "com.twitter" %% "json" % "2.1.6",
  "org.scardf" % "scardf" % "0.5" from "http://scardf.googlecode.com/files/scardf-0.5.jar",
  "joda-time" % "joda-time" % "1.6.1", // Used by scardf
  "org.scalatest" %% "scalatest" % "1.5.1" % "test"
)
