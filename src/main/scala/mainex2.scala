import logic._
import org.apache.spark.graphx.{Edge, EdgeContext, Graph}
import org.apache.spark.{SparkConf, SparkContext}


object mainex2 {

  val Conf: SparkConf = new SparkConf().setAppName("BDRTP2ex2").setMaster("local[*]")

  def main(args: Array[String]): Unit = {
    val sc = new SparkContext(Conf)
    val myVertices = sc.makeRDD(Array(
      (1L, new node(id = 1, monster = new Solar(0, 5))), //A
      (2L, new node(id = 2, monster = new Worgrider(110, 0))), //B
      (3L, new node(id = 3, monster = new Worgrider(110, 5))), //C
      (4L, new node(id = 4, monster = new Worgrider(110, 10))), //D
      (5L, new node(id = 5, monster = new Worgrider(110, 20))), //E
      (6L, new node(id = 6, monster = new Worgrider(110, 30))), //F
      (7L, new node(id = 7, monster = new Worgrider(110, 40))), //G
      (8L, new node(id = 8, monster = new Worgrider(110, 50))), //H
      (9L, new node(id = 9, monster = new Worgrider(110, 60))), //I
      (10L, new node(id = 10, monster = new Worgrider(110, 70))),
      (11L, new node(id = 11, monster = new Worgrider(110, 80))), //A
      (12L, new node(id = 12, monster = new BarbarianOrc(130, 0))), //B
      (13L, new node(id = 13, monster = new BarbarianOrc(130, 15))), //C
      (14L, new node(id = 14, monster = new BarbarianOrc(130, 30))), //D
      (15L, new node(id = 15, monster = new BarbarianOrc(130, 45))), //E
      (16L, new node(id = 16, monster = new Warlord(160, 30))) //F
    )) //J


    val myEdges = sc.makeRDD(Array(
      Edge(1L, 2L, EdgeProperty(RelationType.ENEMY)), Edge(1L, 3L, EdgeProperty(RelationType.ENEMY)), Edge(1L, 4L, EdgeProperty(RelationType.ENEMY)),
      Edge(1L, 5L, EdgeProperty(RelationType.ENEMY)), Edge(1L, 6L, EdgeProperty(RelationType.ENEMY)),
      Edge(1L, 7L, EdgeProperty(RelationType.ENEMY)), Edge(1L, 8L, EdgeProperty(RelationType.ENEMY)),
      Edge(1L, 9L, EdgeProperty(RelationType.ENEMY)), Edge(1L, 10L, EdgeProperty(RelationType.ENEMY)),
      Edge(1L, 11L, EdgeProperty(RelationType.ENEMY)), Edge(1L, 12L, EdgeProperty(RelationType.ENEMY)),
      Edge(1L, 13L, EdgeProperty(RelationType.ENEMY)),
      Edge(1L, 14L, EdgeProperty(RelationType.ENEMY)),
      Edge(1L, 15L, EdgeProperty(RelationType.ENEMY)),
      Edge(1L, 16L, EdgeProperty(RelationType.ENEMY))
    ))


    val myGraph = Graph(myVertices, myEdges)
    execute(myGraph, sc)


  }

  def execute(g: Graph[node, EdgeProperty], sc: SparkContext): Unit = {
    var counter = 1

    def loop1(): Unit = {
      while (true) {
        println("Tour " + counter)
        counter += 1
        val messages = g.aggregateMessages[String](sendActions, MergeActions)
        val res = messages.collect()
        println("fini")
        println(res)
        return
      }
    }

    loop1()
  }


  def sendActions(ctx: EdgeContext[node, EdgeProperty, String]): Unit = {
    val distance = ctx.srcAttr.monster.getDistance(ctx.dstAttr.monster)
    ctx.sendToSrc(ctx.dstAttr.monster.getClass.getSimpleName + ctx.dstAttr.id + " " + ctx.srcAttr.monster.action(distance))
    ctx.sendToDst(ctx.srcAttr.monster.getClass.getSimpleName + ctx.srcAttr.id + " " + ctx.dstAttr.monster.action(distance))
  }

  def MergeActions(msg1: String, msg2: String): String = {
    msg1 + ";" + msg2
  }

}
