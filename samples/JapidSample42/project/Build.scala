import sbt._
import Keys._
import PlayProject._

object ApplicationBuild extends Build {

    val appName         = "JapidSample42"
    val appVersion      = "0.1-SNAPSHOT"

    val appDependencies = Seq(
      // Add your project dependencies here,
      "japid42" % "japid42_2.9.1" % "0.1-SNAPSHOT"
    )

    val main = PlayProject(appName, appVersion, appDependencies, mainLang = JAVA).settings(
      // Add your own project settings here  
      resolvers += "Local Play Repository" at "/Users/bran/projects/Play20/repository/local"
    )

}
