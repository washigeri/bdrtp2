package ex2

import ex2.logic._
import ex2.monsters._
import org.apache.spark.graphx.{Edge, EdgeContext, Graph, VertexId}
import org.apache.spark.{SparkConf, SparkContext}

import scala.collection.mutable.ArrayBuffer
import scala.math.{max, min}
import scala.util.Random

object mainex2 {

  val Conf: SparkConf = new SparkConf().setAppName("BDRTP2ex2").setMaster("local[*]")

  def main(args: Array[String]): Unit = {
    val sc = new SparkContext(Conf)
    sc.setLogLevel("ERROR")
    var solar = new Solar(0, 5)
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


    val myGraph = Graph(myVertices, myEdges)
    var res = execute(myGraph, sc).vertices.collect()
    println("ETAT APRES LE COMBAT")
    //println(res)
    res = res.filter(tuple => tuple._2.monster.HP > 0)
    println("SURVIVANTS")
    for (t <- res) {
      println(t._2.monster.getClass.getSimpleName + " " + t._2.id + " HP: " + t._2.monster.HP)
    }


  }

  def execute(g: Graph[node, EdgeProperty], sc: SparkContext): Graph[node, EdgeProperty] = {
    var counter = 1
    var myGraph = g

    def loop1(): Unit = {
      while (true) {
        println("---------------------------------DEBUT DU TOUR--------------------")
        println("Tour " + counter)
        //println("---------------------------------MONSTER--------------------")
        counter += 1
        val messages = myGraph.aggregateMessages[ArrayBuffer[Message]](sendActions, MergeActions)
        if (messages.isEmpty()) {

          println("---------------------------------FIN--------------------")

          //println("fini")
          return
        }

        // val res = messages.collect()
        //println("fini")
        //println(res)
        //println("---------------------------------CHOIX DES ACTIONS--------------------")

        myGraph = myGraph.joinVertices(messages)(
          (vid, sommet, message) => ChooseAction2(vid, sommet, message)

        )
        //var res = myGraph.vertices.collect()
        //   println(res)

        val messages2 = myGraph.aggregateMessages[ArrayBuffer[Message]](
          sendActionsToApply,
          MergeActions
        )
        /*val res2 = messages2.collect()
        println(messages2)*/
        //println("---------------------------------APPLICATION DES ACTIONS--------------------")

        myGraph = myGraph.joinVertices(messages2)(
          (vid, sommet, message) => ApplyAction(vid, sommet, message)
        )

        /* val res = myGraph.vertices.collect()
         println(res)*/
        //return
        // println("---------------------------------FIN DU TOUR--------------------")

      }
    }

    loop1()
    myGraph
  }


  def sendActions(ctx: EdgeContext[node, EdgeProperty, ArrayBuffer[Message]]): Unit = {
    if (ctx.dstAttr.monster.HP > 0 && ctx.srcAttr.monster.HP > 0) {
      if (ctx.attr.getRelation == RelationType.ENEMY) {
        val distance = ctx.srcAttr.monster.getDistance(ctx.dstAttr.monster)
        val message1 = Message(ctx.srcAttr.monster, ctx.srcAttr.id, ctx.dstAttr.monster, ctx.dstAttr.id, ctx.srcAttr.monster.action(distance))
        val message2 = Message(ctx.dstAttr.monster, ctx.dstAttr.id, ctx.srcAttr.monster, ctx.srcAttr.id, ctx.dstAttr.monster.action(distance))
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

  def ChooseAction2(vid: VertexId, sommet: node, message: ArrayBuffer[Message]): node = {
    val random = new Random()
    var meleeTargets = ArrayBuffer[Message]()
    var rangedTargets = ArrayBuffer[Message]()
    var moveTargets = ArrayBuffer[Message]()
    var healTargets = ArrayBuffer[Message]()
    for (msg <- message) {
      msg.typem match {
        case MessageTypeEnum.MELEE => meleeTargets.+=(msg)
        case MessageTypeEnum.RANGED => rangedTargets.+=(msg)
        case MessageTypeEnum.MOVE => moveTargets.+=(msg)
        case MessageTypeEnum.HEAL => healTargets.+=(msg)
      }
    }
    if (healTargets.nonEmpty) {
      if (healTargets.size == 1) {
        healTargets(0).value = sommet.monster.healPower
        sommet.monster.action = ArrayBuffer(healTargets(0))

      } else {
        healTargets.foreach(_.value = sommet.monster.healPower)
        sommet.monster.action = healTargets
        return new node(sommet.id, sommet.monster)
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
          damage = sommet.monster.damageMelee.roll(random)
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
          damage = sommet.monster.damageRanged.roll(random)
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

    if (sommet.monster.action.isEmpty) {
      sommet.monster.action = ArrayBuffer(moveTargets(0))
      for (move <- moveTargets) {
        if (sommet.monster.getDistance(sommet.monster.action(0).dest) < sommet.monster.getDistance(move.dest)) {
          sommet.monster.action = ArrayBuffer(move)
        }
      }
    }
    new node(sommet.id, sommet.monster)

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

  /*def ChooseAction(vid: VertexId, sommet: node, message: ArrayBuffer[Message]): node = {
    var moveBuffer = ArrayBuffer[Message]()
    for (action <- message) {
      if (action.typem == MessageTypeEnum.MELEE) {
        if (sommet.monster.action.size < sommet.monster.MeleeAtckCount) {
          if (Random.nextInt(20) + sommet.monster.MeleeAtckChance(sommet.monster.action.size) >= action.dest.Armor) {
            action.value = sommet.monster.damageMelee.roll()
          } else {
            action.value = 0
          }

          sommet.monster.action = sommet.monster.action ++ ArrayBuffer(action)
        }
      }
      if (action.typem == MessageTypeEnum.RANGED) {
        if (sommet.monster.action.size < sommet.monster.RangedAtckCount) {
          if (Random.nextInt(20) + sommet.monster.RangedAtckChance(sommet.monster.action.size) >= action.dest.Armor) {
            action.value = sommet.monster.damageRanged.roll()
          } else {
            action.value = 0
          }

          sommet.monster.action = sommet.monster.action ++ ArrayBuffer(action)
        }
      }
      if (action.typem == MessageTypeEnum.HEAL) {
        sommet.monster.action = ArrayBuffer(action)
        return new node(sommet.id, sommet.monster)
      }
      if (action.typem == MessageTypeEnum.MOVE) {
        moveBuffer = moveBuffer ++ ArrayBuffer[Message](action)
      }
    }
    if (sommet.monster.action.isEmpty) {
      sommet.monster.action = ArrayBuffer(moveBuffer(0))
      for (action2 <- moveBuffer) {
        if (sommet.monster.getDistance(sommet.monster.action(0).dest) > sommet.monster.getDistance(action2.dest)) {
          sommet.monster.action = ArrayBuffer(action2)
        }
      }
    }

    new node(sommet.id, sommet.monster)

  }*/

}
