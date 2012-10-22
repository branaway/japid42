import sbt._
import Keys._
import PlayProject._

object ApplicationBuild extends Build {

  val appName         = "japid42"
  val appVersion      = "0.5.1"

  val appDependencies = Seq(
    "org.apache.commons" % "commons-email" % "1.2",
    "commons-lang" % "commons-lang" % "2.6",
    "com.google.code.javaparser" % "javaparser" % "1.0.8",
    "org.eclipse.tycho" % "org.eclipse.jdt.core" % "3.8.2.v20120814-155456"
  )

  val main = PlayProject(
    appName, appVersion, appDependencies,
    mainLang = JAVA
  )
}
