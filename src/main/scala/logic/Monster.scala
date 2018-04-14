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

  def action(distance: Int): String

  case class Damage(nbdice: Int, diceSize: Int, baseDmg: Int) {

    def roll(): Int = {
      var res = baseDmg
      for (i <- 1 to nbdice) {
        res += 1 + Random.nextInt(diceSize)
      }
      res
    }
  }
}
