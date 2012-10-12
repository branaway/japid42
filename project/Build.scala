import sbt._
import Keys._
import PlayProject._

object ApplicationBuild extends Build {

    val appName         = "japid42"
    val appVersion      = "0.4"

    val appDependencies = Seq(
    		"org.apache.commons" % "commons-email" % "1.2"
   // 		"javax.activation" % "activation" % "1.1",
    //		"javax.mail" % "mail" % "1.4.1"
    )

    val main = PlayProject(appName, appVersion, appDependencies, mainLang = JAVA).settings(
      // Add your own project settings here      
    )

}
