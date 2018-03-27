import scalaj.http._
import net.ruippeixotog.scalascraper.browser.JsoupBrowser

object mainex1 {

  val url = "http://paizo.com/pathfinderRPG/prd/bestiary/monsterIndex.html"
  val url2 = "http://paizo.com/pathfinderRPG/prd/bestiary2/additionalMonsterIndex.html"
  var url3 = "http://paizo.com/pathfinderRPG/prd/bestiary3/monsterIndex.html"
  var url4 = "http://paizo.com/pathfinderRPG/prd/bestiary4/monsterIndex.html"
  var url5="http://paizo.com/pathfinderRPG/prd/bestiary5/index.html"
  def main(args: Array[String]): Unit = {
    val request: HttpRequest = Http(url)
    val response = request.asString
    //println(response)
    val browser = JsoupBrowser()
    val doc = browser.parseString(response.body)
    println(doc)
  }
}
