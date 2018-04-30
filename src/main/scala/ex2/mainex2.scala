package ex2

import java.io.Serializable

import ex2.logic._
import ex2.monsters._
import org.apache.spark.graphx.{Edge, EdgeContext, Graph, VertexId}
import org.apache.spark.util.LongAccumulator
import org.apache.spark.{SparkConf, SparkContext}

import scala.collection.mutable.ArrayBuffer
import scala.math.{max, min}
import scala.util.Random

object mainex2 {

  val Conf: SparkConf = new SparkConf().set("spark.cleaner.referenceTracking.cleanCheckpoints", "true")
    .setAppName("BDRTP2ex2")
    .setMaster("local[*]")

  var angelCount = 1L
  var enemyCount = 15L

  def combat1(sc: SparkContext): Unit = {
    val solar = new Solar(0, 5)
    solar.canHeal = false
    val myVertices = sc.makeRDD(Array(

      (1L, new node(id = 1, solar)), //A
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
      Edge(1L, 16L, EdgeProperty(RelationType.ENEMY)), Edge(1L, 1L, EdgeProperty(RelationType.FRIEND)) //Edge qui boucle sur le solar pour qu'il puisse se soigner
    ))


    println("---------------------------------COMBAT 1--------------------")
    val myGraph = Graph(myVertices, myEdges)
    var res = execute(myGraph, sc).vertices.collect()
    println("ETAT APRES LE COMBAT")
    res = res.filter(tuple => tuple._2.monster.HP > 0)
    println("SURVIVANTS")
    for (t <- res) {
      println(t._2.monster.getClass.getSimpleName + " " + t._2.id + " HP: " + t._2.monster.HP)
    }
    println("--------------------------------- FIN COMBAT 1--------------------")
  }

  def execute(g: Graph[node, EdgeProperty], sc: SparkContext): Graph[node, EdgeProperty] = {
    var counter = 1
    var myGraph = g
    val angelAccum = sc.longAccumulator("angelAccum")
    val enemyAccum = sc.longAccumulator(name = "enemyAccum")


    def loop1(): Unit = {
      while (true) {
        println("---------------------------------DEBUT DU TOUR--------------------")
        println("Tour " + counter)
        counter += 1
        val messages = myGraph.aggregateMessages[ArrayBuffer[Message]](sendActions, MergeActions)

        myGraph = myGraph.joinVertices(messages)(
          (vid, sommet, message) => ChooseAction2(vid, sommet, message)

        )

        val messages2 = myGraph.aggregateMessages[ArrayBuffer[Message]](
          sendActionsToApply,
          MergeActions
        )

        myGraph = myGraph.joinVertices(messages2)(
          (vid, sommet, message) => ApplyAction(vid, sommet, message)
        )

        myGraph.vertices.foreach(v => incrementAccs(v, angelAccum, enemyAccum))
        myGraph = myGraph.subgraph(vpred = (_, attr) => attr.monster.HP > 0)
        myGraph.checkpoint()
        if (angelAccum.value == angelCount || enemyAccum.value == enemyCount) {
          println("---------------------------------FIN--------------------")
          return
        }
      }
    }

    loop1()
    myGraph
  }

  def incrementAccs(v: (VertexId, node), acc1: LongAccumulator, acc2: LongAccumulator): Unit = {
    if (v._2.monster.HP <= 0) {
      if (v._2.monster.isInstanceOf[Serializable with Monster with Ennemy]) {
        acc2.add(1L)
      }
      if (v._2.monster.isInstanceOf[Serializable with Monster with Angel]) {
        acc1.add(1)
      }
    }
  }

  def ChooseAction2(vid: VertexId, sommet: node, message: ArrayBuffer[Message]): node = {
    sommet.monster.action.clear()
    if (sommet.monster.getClass.getSimpleName != "Dragon") {
      var meleeTargets = ArrayBuffer[Message]()
      var rangedTargets = ArrayBuffer[Message]()
      var moveTargets = ArrayBuffer[Message]()
      var healTargets = ArrayBuffer[Message]()
      for (msg <- message.filter(m => !m.dest.alterself)) {
        msg.typem match {
          case MessageTypeEnum.MELEE => meleeTargets.+=(msg)
          case MessageTypeEnum.RANGED => rangedTargets.+=(msg)
          case MessageTypeEnum.MOVE => moveTargets.+=(msg)
          case MessageTypeEnum.HEAL => healTargets.+=(msg)
        }
      }
      if (healTargets.nonEmpty) {
        if (healTargets.size <= 2) {
          val sorted = healTargets.sortBy(m => m.dest.HP)
          sommet.monster.action = ArrayBuffer(sorted(0).copy(value = sommet.monster.healPower))

        } else {
          if (sommet.monster.massHeal) {
            healTargets.foreach(_.value = sommet.monster.healPower)
            sommet.monster.action = healTargets
            sommet.monster.massHeal = false
            return new node(sommet.id, sommet.monster)
          }
          else {
            val sorted = healTargets.sortBy(m => m.dest.HP)
            sommet.monster.action = ArrayBuffer(sorted(0).copy(value = sommet.monster.healPower))
          }
        }

      }
      val maxAttacks = max(sommet.monster.RangedAtckCount, sommet.monster.MeleeAtckCount)
      var mcount: Int = 0
      var rcount: Int = 0
      var atkCount: Int = 0
      var hp = 0
      var damage = 0
      var continueOnSameTarget = true
      for (melee <- meleeTargets if mcount < sommet.monster.MeleeAtckCount if atkCount < maxAttacks) {
        hp = melee.dest.HP
        for (i <- 0 until sommet.monster.MeleeAtckCount if continueOnSameTarget) {

            damage = 0
            if (Random.nextInt(20) + sommet.monster.MeleeAtckChance(i) >= melee.dest.Armor) {
              damage = sommet.monster.damageMelee.roll()
              melee.value = damage
              hp -= damage
              continueOnSameTarget = hp > 0
            }
            else {
              melee.value = 0
            }
            sommet.monster.action ++= ArrayBuffer(melee.copy(value = damage))
            mcount += 1
            atkCount += 1


        }
        continueOnSameTarget = true
      }
      hp = 0
      damage = 0
      continueOnSameTarget = true
      for (ranged <- rangedTargets if rcount < sommet.monster.RangedAtckCount if atkCount < maxAttacks) {
        hp = ranged.dest.HP
        for (i <- 0 until sommet.monster.RangedAtckCount if continueOnSameTarget) {
          damage = 0
          if (Random.nextInt(20) + sommet.monster.RangedAtckChance(i) >= ranged.dest.Armor) {
            damage = sommet.monster.damageRanged.roll()
            ranged.value = damage
            hp -= damage
            continueOnSameTarget = hp > 0
          }
          else {
            ranged.value = 0
          }
          sommet.monster.action ++= ArrayBuffer(ranged.copy(value = damage))
          rcount += 1
          atkCount += 1
        }
        continueOnSameTarget = true
      }

      if (sommet.monster.action.isEmpty && moveTargets.nonEmpty) {
        sommet.monster.action = ArrayBuffer(moveTargets(0))
        for (move <- moveTargets) {
          if (sommet.monster.getDistance(sommet.monster.action(0).dest) < sommet.monster.getDistance(move.dest)) {
            sommet.monster.action = ArrayBuffer(move)
          }
        }
      }

    } else {
      val res = sommet.monster.IA(message)
      sommet.monster.action = res
    }

    new node(sommet.id, sommet.monster)
  }

  def sendActions(ctx: EdgeContext[node, EdgeProperty, ArrayBuffer[Message]]): Unit = {

    if (ctx.dstAttr.monster.HP > 0 && ctx.srcAttr.monster.HP > 0) {
      if (ctx.attr.getRelation == RelationType.ENEMY) {
        val distance = ctx.srcAttr.monster.getDistance(ctx.dstAttr.monster)
        val flying1 = ctx.dstAttr.monster.flying
        val flying2 = ctx.srcAttr.monster.flying
        val message1 = Message(ctx.srcAttr.monster, ctx.srcAttr.id, ctx.dstAttr.monster, ctx.dstAttr.id, ctx.srcAttr.monster.action(distance, flying1))
        val message2 = Message(ctx.dstAttr.monster, ctx.dstAttr.id, ctx.srcAttr.monster, ctx.srcAttr.id, ctx.dstAttr.monster.action(distance, flying2))
        ctx.sendToSrc(ArrayBuffer(message1))
        ctx.sendToDst(ArrayBuffer(message2))
      }
      else {
        if (ctx.srcAttr.monster.shouldHeal(ctx.dstAttr.monster)) {
          val message = Message(ctx.srcAttr.monster, ctx.srcAttr.id, ctx.dstAttr.monster, ctx.dstAttr.id, MessageTypeEnum.HEAL)
          ctx.sendToSrc(ArrayBuffer(message))
        }
      }

    }
  }

  def sendActionsToApply(ctx: EdgeContext[node, EdgeProperty, ArrayBuffer[Message]]): Unit = {
    if (ctx.dstAttr.monster.HP > 0 && ctx.srcAttr.monster.HP > 0) {
      for (action <- ctx.srcAttr.monster.action) {
        if (ctx.dstAttr.id == action.dstid) {
          if (action.typem == MessageTypeEnum.MOVE) {
            ctx.sendToSrc(ArrayBuffer(action))
          }

          else {
            ctx.sendToDst(ArrayBuffer(action))
          }
        }
      }
      for (actiondst <- ctx.dstAttr.monster.action) {
        if (ctx.srcAttr.id == actiondst.dstid) {
          if (actiondst.typem == MessageTypeEnum.MOVE) {
            ctx.sendToDst(ArrayBuffer(actiondst))
          }

          else {
            ctx.sendToSrc(ArrayBuffer(actiondst))
          }
        }
      }
    }
  }

  def MergeActions(msg1: ArrayBuffer[Message], msg2: ArrayBuffer[Message]): ArrayBuffer[Message] = {
    msg1 ++ msg2
  }

  def ApplyAction(vid: VertexId, sommet: node, message: ArrayBuffer[Message]): node = {
    val monster: Monster = sommet.monster

    for (action <- message) {
      action.typem match {
        case MessageTypeEnum.MOVE => monster.move(action.dest); println(monster.getClass.getSimpleName + sommet.id + " avance vers " + action.dest.getClass.getSimpleName + action.dstid)
        case MessageTypeEnum.HEAL => monster.HP = min(monster.maxHp, monster.HP + action.value); println(action.source.getClass.getSimpleName + action.srcid + " soigne " + monster.getClass.getSimpleName + sommet.id + " de " + action.value + " PVs")
        case _ =>
          if (action.value == 0) {
            println(monster.getClass.getSimpleName + sommet.id + " ESQUIVE l'attaque " + action.typem + " de " + action.source.getClass.getSimpleName + action.srcid)
          } else {
            monster.removeHP(action.value)
            println(monster.getClass.getSimpleName + sommet.id + " se fait attaquer a " + action.typem + " de " + action.source.getClass.getSimpleName + " pour " + action.value + " points de degats")
          }
      }
    }
    if (sommet.monster.HP > 0 && sommet.monster.HP < sommet.monster.maxHp) {
      monster.HP = min(sommet.monster.maxHp, monster.Regeneration + monster.HP)
    }
    if (sommet.monster.HP <= 0) {
      println("---------------------------------MONSTER--------------------")
      println(monster.getClass.getSimpleName + " " + sommet.id + " " + "est mort")
      println("---------------------------------MONSTER--------------------")
    }
    monster.action = ArrayBuffer[Message]()
    new node(sommet.id, monster)
  }

  def main(args: Array[String]): Unit = {

    val sc = new SparkContext(Conf)
    sc.setCheckpointDir("./RDDCheckpoint")
    sc.setLogLevel("ERROR")
    combat1(sc)
    combat2(sc)
    sc.stop()


  }

  def combat2(sc: SparkContext): Unit = {
    println("---------------------------------COMBAT 2--------------------")
    angelCount = 10L
    enemyCount = 211L
    var myVertices2Buffer = ArrayBuffer(
      (1L, new node(1, new Solar(0, 0))),
      (2L, new node(2, new Planetar(0, 10))),
      (3L, new node(3, new Planetar(0, 5))),
      (4L, new node(4, new MovanicDeva(5, 5))),
      (5L, new node(5, new MovanicDeva(0, -5))),
      (6L, new node(6, new Astral(5, -5))),
      (7L, new node(7, new Astral(5, -6))),
      (8L, new node(8, new Astral(5, 7))),
      (9L, new node(9, new Astral(-5, -8))),
      (10L, new node(10, new Astral(-5, -4))),
      (11L, new node(11, new Dragon(200, 200))),
      (12L, new node(12, new AngelSlayer(110, 110))),
      (13L, new node(13, new AngelSlayer(115, 110))),
      (14L, new node(14, new AngelSlayer(110, 115))),
      (15L, new node(15, new AngelSlayer(115, 115))),
      (16L, new node(16, new AngelSlayer(120, 110))),
      (17L, new node(17, new AngelSlayer(110, 120))),
      (18L, new node(18, new AngelSlayer(120, 120))),
      (19L, new node(19, new AngelSlayer(125, 110))),
      (20L, new node(20, new AngelSlayer(110, 125))),
      (21L, new node(21, new AngelSlayer(125, 125)))
    )
    val startIndex = 22L
    for (i <- 0 until 200) {
      myVertices2Buffer.+=((startIndex + i, new node(startIndex.toInt + i, new BarbarianOrc(110, -100 + i))))
    }
    val myVertices2 = sc.makeRDD(myVertices2Buffer)
    var myEdges2Buffer: ArrayBuffer[Edge[EdgeProperty]] = ArrayBuffer()
    for (i <- myVertices2Buffer.indices) {
      for (j <- i until myVertices2Buffer.size) {
        val node1 = myVertices2Buffer(i)
        val node2 = myVertices2Buffer(j)
        (node1._2.monster, node2._2.monster) match {
          case (_: Serializable with Monster with Angel, _: Serializable with Monster with Angel) => myEdges2Buffer.+=(Edge(node1._1, node2._1, EdgeProperty(RelationType.FRIEND)))
          case (_: Serializable with Monster with Angel, _: Serializable with Monster with Ennemy) => myEdges2Buffer.+=(Edge(node1._1, node2._1, EdgeProperty(RelationType.ENEMY)))
          case (_: Dragon, _: AngelSlayer) => myEdges2Buffer.+=(Edge(node1._1, node2._1, EdgeProperty(RelationType.FRIEND)))
          case _ =>
        }

      }
    }
    val myEdges2 = sc.makeRDD(myEdges2Buffer)
    val myGraph2 = Graph(myVertices2, myEdges2)
    //val edges = myGraph2.edges.collect()
    //println(edges)
    var res2 = execute(myGraph2, sc).vertices.collect()
    println("ETAT APRES LE COMBAT")
    //println(res)
    res2 = res2.filter(tuple => tuple._2.monster.HP > 0)
    println("SURVIVANTS")
    for (t <- res2) {
      println(t._2.monster.getClass.getSimpleName + " " + t._2.id + " HP: " + t._2.monster.HP)
    }
    println("--------------------------------- FIN COMBAT 2--------------------")

  }

}
