import sbt._
import Keys._
import PlayProject._

object ApplicationBuild extends Build {

  val appName         = "japid42"
  val appVersion      = "0.5"

  val appDependencies = Seq(
    "org.apache.commons" % "commons-email" % "1.2",
    "commons-lang" % "commons-lang" % "2.6",
    "com.google.code.javaparser" % "javaparser" % "1.0.8"
  )

  val main = PlayProject(
    appName, appVersion, appDependencies,
    mainLang = JAVA
  )
}
