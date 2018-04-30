package ex2.monsters

import ex2.logic.MessageTypeEnum.MessageTypeEnum
import ex2.logic.{Message, Position}

import scala.collection.mutable.ArrayBuffer
import scala.math.max
import scala.util.Random

trait Monster {
  var HP: Int
  var Armor: Int
  var ListAction: List[MessageTypeEnum]
  var Speed: Int
  var Regeneration: Int = 0
  var maxHp: Int
  var DamageReduction: Int = 0
  var MeleeAtckCount: Int = 0
  var RangedAtckCount: Int = 0
  var MeleeAtckChance: List[Int] = List()
  var RangedAtckChance: List[Int] = List()
  var damageMelee: Damage
  var damageRanged: Damage
  var position: Position
  var canHeal: Boolean = false
  var massHeal: Boolean = false
  var healPower: Int = 0
  var alterself = false
  var flying = false
  var action: ArrayBuffer[Message] = ArrayBuffer[Message]()


  def shouldHeal(target: Monster): Boolean = {
    canHeal && target.HP < 0.25 * target.maxHp
  }

  def heal(): Int = {
    healPower
  }

  def action(distance: Double,flying:Boolean): MessageTypeEnum

  def move(monster: Monster): Unit = {
    val distance = this.getDistance(monster)
    if (distance <= this.Speed) {
      this.position.setPosition(monster.position.x, monster.position.y)
    }
    else {
      val x = this.position.x
      val y = this.position.y
      this.position.setPosition(this.position.x - x, this.position.y - y)
      monster.position.setPosition(monster.position.x - x, monster.position.y - y)
      val rapport = distance / Speed
      val xproj = monster.position.x / rapport
      val yproj = monster.position.y / rapport
      this.position.setPosition(this.position.x + x, this.position.y + y)
      monster.position.setPosition(monster.position.x + x, monster.position.y + y)
      this.position.setPosition(this.position.x + xproj, this.position.y + yproj)
    }
  }

  def getDistance(monster2: Monster): Double = {
    val monster = this
    scala.math.sqrt((monster2.position.x - monster.position.x) * (monster2.position.x - monster.position.x) + (monster2.position.y - monster.position.y) * (monster2.position.y - monster.position.y))
  }

  def removeHP(damage: Int): Unit = {
    this.HP -= max(0, damage - DamageReduction)
  }

  def IA(messages: ArrayBuffer[Message]): ArrayBuffer[Message] = {
    ArrayBuffer[Message]()
  }

  case class Damage(nbdice: Int, diceSize: Int, baseDmg: Int) extends Serializable {

    def roll(): Int = {

      var res = baseDmg
      for (_ <- 1 to nbdice) {
        res += 1 + Random.nextInt(diceSize)
      }
      res
    }
  }

}
