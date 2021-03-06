name := "bdrtp2"

version := "0.1"

scalaVersion := "2.11.8"

libraryDependencies ++= Seq(
  "org.apache.spark" % "spark-core_2.11" % "2.2.0" exclude("org.apache.hadoop", "hadoop-yarn-server-web-proxy"),
  "org.apache.spark" % "spark-graphx_2.11" % "2.2.0" exclude("org.apache.hadoop", "hadoop-yarn-server-web-proxy"),
  "net.ruippeixotog" %% "scala-scraper" % "2.1.0"

)