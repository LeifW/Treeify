scalaVersion := "2.9.0-1"

resolvers += "funes" at "http://funesrdf.info/maven"

libraryDependencies ++= Seq(
  "org.scardf" % "scardf" % "0.6-SNAPSHOT",
  "org.scalatest" %% "scalatest" % "1.6.1" % "test"
)
