import net.ruippeixotog.scalascraper.browser.JsoupBrowser
import net.ruippeixotog.scalascraper.dsl.DSL.Extract._
import net.ruippeixotog.scalascraper.dsl.DSL._
import net.ruippeixotog.scalascraper.model._


object mainex1 {

  val Root_URL = "http://paizo.com"
  val Base_URL = "http://paizo.com/pathfinderRPG/prd/"
  val Bestiaries = Array("bestiary/", "bestiary2/", "bestiary3/", "bestiary4/", "bestiary5/")
  val Indexes = Array("monsterIndex.html", "additionalMonsterIndex.html", "monsterIndex.html", "monsterIndex.html", "index.html")
  val Browser = JsoupBrowser()
  val MaxIndex = 4

  def main(args: Array[String]): Unit = {
    val res = BuildURLList()
    println(res)
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

  def HTTPQuery(url: String): Document = {
    val res = Browser.get(url)
    res
  }
}
