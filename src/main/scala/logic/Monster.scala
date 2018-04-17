package logic

import scala.util.Random

trait Monster {

  var HP: Int
  var Armor: Int
  var ListAction: List[String]
  var Speed: Int
  var Regeneration: Int = 0
  var DistanceMoved: Int = 0

  var MeleeAtckCount: Int = 0
  var RangedAtckCount: Int = 0
  var MeleeAtckChance: List[Int] = List()
  var RangedAtckChance: List[Int] = List()
  var damageMelee: Damage
  var damageRanged: Damage
  var position: Position

  def action(distance: Double): String

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

  case class Damage(nbdice: Int, diceSize: Int, baseDmg: Int) {

    def roll(): Int = {
      var res = baseDmg
      for (_ <- 1 to nbdice) {
        res += 1 + Random.nextInt(diceSize)
      }
      res
    }
  }

}
