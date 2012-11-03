// Comment to get more information during initialization
logLevel := Level.Warn

// The Typesafe repository
resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"

//resolvers ++= Seq(
//  Resolver.file("Local Repository", file("/Users/bran/projects/playversions/Play20/repository/local"))(Resolver.ivyStylePatterns)
//)

// Use the Play sbt plugin for Play projects
addSbtPlugin("play" % "sbt-plugin" % "2.0.4")
//addSbtPlugin("play" % "sbt-plugin" % "2.1-SNAPSHOT")

//Resolver.url("sbt-plugin-releases", new URL("http://scalasbt.artifactoryonline.com/scalasbt/sbt-plugin-releases")) (Resolver.ivyStylePatterns)
  
//addSbtPlugin("com.jsuereth" % "xsbt-gpg-plugin" % "0.6.1")
