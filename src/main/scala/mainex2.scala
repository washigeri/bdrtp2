import logic._
import org.apache.spark.graphx.{Edge, EdgeContext, Graph}
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.graphx.{Edge, Graph}


object mainex2 {

  val Conf: SparkConf = new SparkConf().setAppName("BDRTP2ex2").setMaster("local[*]")

  def main(args: Array[String]): Unit = {
    val sc = new SparkContext(Conf)
    var myVertices = sc.makeRDD(Array(
      (1L, new node(id = 1, monster = new Solar())), //A
      (2L, new node(id = 2, monster = new Worgrider())), //B
      (3L, new node(id = 3, monster = new Worgrider())), //C
      (4L, new node(id = 4, monster = new Worgrider())), //D
      (5L, new node(id = 5, monster = new Worgrider())), //E
      (6L, new node(id = 6, monster = new Worgrider())), //F
      (7L, new node(id = 7, monster = new Worgrider())), //G
      (8L, new node(id = 8, monster = new Worgrider())), //H
      (9L, new node(id = 9, monster = new Worgrider())), //I
      (10L, new node(id = 10, monster = new Worgrider())),
      (11L, new node(id = 11, monster = new Worgrider())), //A
      (12L, new node(id = 12, monster = new BarbarianOrc())), //B
      (13L, new node(id = 13, monster = new BarbarianOrc)), //C
      (14L, new node(id = 14, monster = new BarbarianOrc)), //D
      (15L, new node(id = 15, monster = new BarbarianOrc)), //E
      (16L, new node(id = 16, monster = new Warlord())) //F
    )) //J


    var myEdges = sc.makeRDD(Array(
      Edge(1L, 2L, 110), Edge(1L, 3L, 110), Edge(1L, 4L, 110),
      Edge(1L, 5L, 110), Edge(1L, 6L, 110),
      Edge(1L, 7L, 110), Edge(1L, 8L, 110),
      Edge(1L, 9L, 110), Edge(1L, 10L, 110),
      Edge(1L, 11L, 110), Edge(1L, 12L, 120),
      Edge(1L, 13L, 120),
      Edge(1L, 14L, 120),
      Edge(1L, 15L, 120),
      Edge(1L, 16L, 160)
    ))


    var myGraph = Graph(myVertices, myEdges)
    var messages = myGraph.aggregateMessages[String](sendActions, MergeActions)
    var res = messages.collect()
    println("fini")

  }


  def sendActions(ctx: EdgeContext[node, Int, String]): Unit = {
    ctx.sendToDst(ctx.srcAttr.monster.getClass.getSimpleName + ctx.srcAttr.id + " " + ctx.srcAttr.monster.action(ctx.attr))
    ctx.sendToSrc(ctx.dstAttr.monster.getClass.getSimpleName + ctx.dstAttr.id + " " + ctx.dstAttr.monster.action(ctx.attr))
  }

  def MergeActions(msg1: String, msg2: String): String = {
    msg1 + ";" + msg2
  }

}
