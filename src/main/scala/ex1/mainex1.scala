package ex1

import java.io.{File, IOException}
import java.nio.file.{Files, Paths}

import net.ruippeixotog.scalascraper.browser.JsoupBrowser
import net.ruippeixotog.scalascraper.dsl.DSL.Extract._
import net.ruippeixotog.scalascraper.dsl.DSL._
import net.ruippeixotog.scalascraper.model._
import org.apache.commons.io.FileUtils
import org.apache.spark.{SparkConf, SparkContext}


object mainex1 {

  val Root_URL = "http://paizo.com"
  val Base_URL = "http://paizo.com/pathfinderRPG/prd/"
  val Bestiaries = Array("bestiary/", "bestiary2/", "bestiary3/", "bestiary4/", "bestiary5/")
  val Indexes = Array("monsterIndex.html", "additionalMonsterIndex.html", "monsterIndex.html", "monsterIndex.html", "index.html")
  val Browser = JsoupBrowser()
  val MaxIndex = 4
  val Conf: SparkConf = new SparkConf().setAppName("BDRTP2ex1").setMaster("local[*]")

  def main(args: Array[String]): Unit = {
    val sc = new SparkContext(Conf)
    sc.setLogLevel("ERROR")
    println("Fetching Monsters list...")
    val res = sc.parallelize(BuildURLList())
    println("Monsters retrived : " + res.count())
    println("Fetching spells for monsters...")
    val distMonsters = res.map(u => getSortByURL(u))
    var pairs = distMonsters.flatMap(c => c.listspell.map(s => (s, c.name))).groupByKey()
    val resultExists = Files.exists(Paths.get("./results"))
    if (resultExists) {
      DeleteDirectory("results")
    }
    pairs = pairs.coalesce(1)
    println("Spells fetched !")
    pairs.saveAsTextFile("results")
    sc.stop()
  }

  def BuildURLList(): List[String] = {
    var res: List[String] = List()
    for (k <- 0 to MaxIndex) {
      res = res ::: ParseIndexPage(k)
    }
    res = res.filter(_ contains "#")
    res
  }

  def ParseIndexPage(id: Int): List[String] = {
    val url = Base_URL + Bestiaries(id) + Indexes(id)
    val doc = HTTPQuery(url = url)
    val uls = doc >> elementList("#monster-index-wrapper ul a")
    var res: List[String] = List()
    res = uls.map(a => (if (id == 0) Base_URL + Bestiaries(id) else Root_URL) + (a >> attr("href")))
    res
  }

  def getSortByURL(url: String): creature = {
    val res = HTTPQuery(url)
    val array = url.split("#")
    val named = array(1)
    val tab = res.toString
    val tabByCreatureBody = tab.split("<div class=\"body\">")

    val tabByCreature = tabByCreatureBody(1).split("<h1")
    val creature = new creature(name = named)
    for (k <- 0 until tabByCreature.length) {
      val id = tabByCreature(k).split("class")
      if (id(0).trim().contains("id=\"" + named + "\"")) {

        val tabspell = tabByCreature(k).split("/spells/")
        tabspell(0) = ""
        for (y <- 0 until tabspell.length) {

          if (tabspell(y).split(".html")(0) != "") {

            if (tabspell(y).split(".html")(0).contains("detectMagic#")) {
              val dm = tabspell(y).split(".html")(0).split("#")
              creature.listspell += dm(0)
            } else {
              creature.listspell += tabspell(y).split(".html")(0)
            }
          }

        }
      }
    }
    creature

  }

  def HTTPQuery(url: String): Document = {
    val res = Browser.get(url)
    res
  }

  def DeleteDirectory(path: String): Unit = {
    try {
      FileUtils.deleteDirectory(new File(path))
    } catch {
      case ioe: IOException =>
        // log the exception here
        ioe.printStackTrace()
    }
  }
}
