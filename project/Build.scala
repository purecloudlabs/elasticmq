import sbt._
import Keys._

object Resolvers {
  val elasticmqResolvers = Seq(ScalaToolsSnapshots)
}

object BuildSettings {
  import Resolvers._

  val buildSettings = Defaults.defaultSettings ++ Seq (
    organization  := "org.elasticmq",
    version       := "1.0",
    scalaVersion  := "2.9.0-1",
    resolvers     := elasticmqResolvers
  )
}

object Dependencies {
  val squeryl = "org.squeryl" %% "squeryl" % "0.9.4"
  val h2 = "com.h2database" % "h2" % "1.3.156"
  val jodaTime = "joda-time" % "joda-time" % "1.6.2" // when available use https://github.com/jorgeortiz85/scala-time
  val netty = "org.jboss.netty" % "netty" % "3.2.4.Final"

  val scalatest = "org.scalatest" % "scalatest_2.9.0" % "1.6.1" % "test"
  val mockito = "org.mockito" % "mockito-core" % "1.7" % "test"

  val testing = Seq(scalatest, mockito)

  // To get the source run the <update-classifiers> task.
}

object ElasticMQBuild extends Build {
  import Dependencies._
  import BuildSettings._

  lazy val root: Project = Project(
    "root",
    file("."),
    settings = buildSettings
  ) aggregate(api, core, rest)

  lazy val api: Project = Project(
    "api",
    file("api"),
    settings = buildSettings
  )

  lazy val core: Project = Project(
    "core",
    file("core"),
    settings = buildSettings ++ Seq(libraryDependencies := Seq(squeryl, h2, jodaTime) ++ testing)
  ) dependsOn(api)

  lazy val rest: Project = Project(
    "rest",
    file("rest"),
    settings = buildSettings
  ) aggregate(restCore, restSqs)

  lazy val restCore: Project = Project(
    "rest-core",
    file("rest/rest-core"),
    settings = buildSettings ++ Seq(libraryDependencies := Seq(netty) ++ testing)
  ) dependsOn(api)

  lazy val restSqs: Project = Project(
    "rest-sqs",
    file("rest/rest-sqs"),
    settings = buildSettings ++ Seq(libraryDependencies := Seq() ++ testing)
  ) dependsOn(restCore)
}