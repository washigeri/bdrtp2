import logic._
import org.apache.spark.graphx.{Edge, EdgeContext, Graph, VertexId}
import org.apache.spark.{SparkConf, SparkContext}

import scala.collection.mutable.ArrayBuffer
import scala.util.Random


object mainex2 {

  val Conf: SparkConf = new SparkConf().setAppName("BDRTP2ex2").setMaster("local[*]")

  def main(args: Array[String]): Unit = {
    val sc = new SparkContext(Conf)
    val myVertices = sc.makeRDD(Array(
      (1L, new node(id = 1, new Solar(0, 5))), //A
      (2L, new node(id = 2, new Worgrider(110, 0))), //B
      (3L, new node(id = 3, new Worgrider(110, 5))), //C
      (4L, new node(id = 4, new Worgrider(110, 10))), //D
      (5L, new node(id = 5, new Worgrider(110, 20))), //E
      (6L, new node(id = 6, new Worgrider(110, 30))), //F
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
    var myGraph = g

    def loop1(): Unit = {
      while (true) {
        println("Tour " + counter)
        counter += 1
        val messages = myGraph.aggregateMessages[ArrayBuffer[Message]](sendActions, MergeActions)
        if (messages.isEmpty()){
          println("fini")
          return
        }

        val res = messages.collect()
        println("fini")
        //println(res)
       myGraph = myGraph.joinVertices(messages)(
          (vid, sommet, message) => ChooseAction(vid, sommet, message)

        )
     /* var res = myGraph.vertices.collect()
        println(res)
*/
        val messages2 = myGraph.aggregateMessages[ArrayBuffer[Message]](
          sendActionsToApply,
          MergeActions
        )
        /*val res2 = messages2.collect()
        println(messages2)*/
        myGraph = myGraph.joinVertices(messages2)(
          (vid, sommet, message) => ApplyAction(vid, sommet, message)
        )

       /* val res = myGraph.vertices.collect()
        println(res)*/
        //return
      }
    }

    loop1()
  }


  def sendActions(ctx: EdgeContext[node, EdgeProperty, ArrayBuffer[Message]]): Unit = {
    if (ctx.dstAttr.monster.HP > 0 && ctx.srcAttr.monster.HP > 0) {
      val distance = ctx.srcAttr.monster.getDistance(ctx.dstAttr.monster)
      val message1 = new Message(ctx.srcAttr.monster, ctx.srcAttr.id, ctx.dstAttr.monster, ctx.dstAttr.id, ctx.srcAttr.monster.action(distance))
      val message2 = new Message(ctx.dstAttr.monster, ctx.dstAttr.id, ctx.srcAttr.monster, ctx.srcAttr.id, ctx.dstAttr.monster.action(distance))
      ctx.sendToSrc(ArrayBuffer(message1))
      ctx.sendToDst(ArrayBuffer(message2))
    }
  }


  def sendActionsToApply(ctx: EdgeContext[node, EdgeProperty, ArrayBuffer[Message]]): Unit = {
    //if(ctx.srcAttr.id == 1 || ctx.dstAttr.id == 1){
    //  println("solar")
    //}
    if (ctx.dstAttr.monster.HP > 0 && ctx.srcAttr.monster.HP > 0) {
      for (action <- ctx.srcAttr.monster.action) {
        if (action.typem == MessageTypeEnum.MOVE) {
          if (ctx.dstAttr.id == action.dstid) {
            ctx.sendToSrc(ArrayBuffer(action))
          }
        }
        else {
          ctx.sendToDst(ArrayBuffer(action))
        }
      }
      for (actiondst <- ctx.dstAttr.monster.action) {
        if (actiondst.typem == MessageTypeEnum.MOVE) {
          if (ctx.srcAttr.id == actiondst.dstid) {
            ctx.sendToDst(ArrayBuffer(actiondst))
          }
        }
        else {
          ctx.sendToSrc(ArrayBuffer(actiondst))
        }
      }
    }
  }

  def MergeActions(msg1: ArrayBuffer[Message], msg2: ArrayBuffer[Message]): ArrayBuffer[Message] = {
    msg1 ++ msg2
  }


  def ChooseAction(vid: VertexId, sommet: node, message: ArrayBuffer[Message]): node = {
  if(sommet.id==1){
    println("solar");
  }
    var moveBuffer = ArrayBuffer[Message]();
    for(action <- message){
      if(action.typem==MessageTypeEnum.MELEE){
        if(sommet.monster.action.size < sommet.monster.MeleeAtckCount){
          if(Random.nextInt(20)+sommet.monster.MeleeAtckChance(sommet.monster.action.size)>=action.dest.Armor){
            action.value= sommet.monster.damageMelee.roll();
          }else{
            action.value= 0
          }

          sommet.monster.action=sommet.monster.action ++ ArrayBuffer(action);
        }
      }
      if(action.typem==MessageTypeEnum.RANGED){
        if(sommet.monster.action.size < sommet.monster.MeleeAtckCount){
          if(Random.nextInt(20)+sommet.monster.RangedAtckChance(sommet.monster.action.size)>=action.dest.Armor){
            action.value=sommet.monster.damageRanged.roll();
          }else{
            action.value=0;
          }

          sommet.monster.action=sommet.monster.action ++ ArrayBuffer(action);
        }
      }
      if(action.typem==MessageTypeEnum.HEAL){
        sommet.monster.action=ArrayBuffer(action);
        return  new node(sommet.id, sommet.monster)
      }
      if(action.typem==MessageTypeEnum.MOVE){
      moveBuffer= moveBuffer++ArrayBuffer[Message](action);
      }
    }
    if(sommet.monster.action.size==0){
      sommet.monster.action=ArrayBuffer(moveBuffer(0));
      for(action2<-moveBuffer){
          if(sommet.monster.getDistance(sommet.monster.action(0).dest)>sommet.monster.getDistance(action2.dest)){
            sommet.monster.action=ArrayBuffer(action2);
          }
      }
    }

    new node(sommet.id, sommet.monster)

  }

  def ApplyAction(vid: VertexId, sommet: node, message: ArrayBuffer[Message]): node = {
    if (sommet.id == 1) {
      println("solar")
    }
    val monster: Monster = sommet.monster
    for (action <- message) {
      action.typem match {
        case MessageTypeEnum.MOVE => monster.move(action.dest); println("monstre"+ monster.getClass.getName + sommet.id+ "avance vers "+ action.dstid)
        case MessageTypeEnum.HEAL =>
        case _ =>{
          if(action.value==0){
            println("monstre"+ monster.getClass.getName + sommet.id +"ESQUIVE")
          }else{
            monster.removeHP(action.value);
            println("monstre"+ monster.getClass.getName + sommet.id +"se fait attaquer a "+action.typem +" de "+ action.value)
          }

        }
      }
    }
    if (sommet.monster.HP > 0 && sommet.monster.HP < sommet.monster.maxHp) {
      monster.HP += monster.Regeneration
    }
    if(sommet.monster.HP<=0){
      println("---------------------------------MONSTER--------------------")
      println(monster.getClass.getName +" " +sommet.id +" "+"est mort")
      println("---------------------------------MONSTER--------------------")
    }
    monster.action = ArrayBuffer[Message]();
    new node(sommet.id, monster)
  }

}
