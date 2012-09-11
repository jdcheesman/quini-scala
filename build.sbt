name := "quini-scala"

version := "0.1-snapshot"

scalaVersion := "2.10.0-M7"

resolvers += "Akka Snapshots" at "http://repo.akka.io/snapshots"

resolvers += "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"

//resolvers += "More Akka Snapshots" at "http://repo.typesafe.com/typesafe/snapshots/"

libraryDependencies += "com.typesafe" % "slick_2.10.0-M7" % "0.11.1"

libraryDependencies += "mysql" % "mysql-connector-java" % "5.1.12"

libraryDependencies += "org.slf4j" % "slf4j-nop" % "1.6.1" % "runtime"

libraryDependencies += "org.skife.com.typesafe.config" % "typesafe-config" % "0.3.0"

//libraryDependencies += "com.typesafe.akka" % "akka-actor_2.10.0-M7" % "2.1-M2"

//libraryDependencies += "com.typesafe.akka" %% "akka-actor" % "2.1-20120908-000916"

libraryDependencies += "com.typesafe.akka" % "akka-actor_2.10.0-M7" % "2.1-20120908-000916"

