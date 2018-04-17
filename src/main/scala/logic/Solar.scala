package logic

/**
  * Created by oxeyo on 10/04/2018.
  */
class Solar(val x: Double, val y: Double) extends Serializable with Monster {
  override var HP: Int = 363
  override var Armor: Int = 14
  override var ListAction: List[String] = List("move", "ranged", "melee", "heal")
  override var Speed: Int = 50
  Regeneration = 15
  override var damageMelee: Damage = Damage(3, 6, 18)

  MeleeAtckCount = 4
  RangedAtckCount = 4
  MeleeAtckChance = List(35, 30, 25, 20).map(_ + 5)
  RangedAtckChance = List(31, 26, 21, 16).map(_ + 5)
  override var damageRanged: Damage = Damage(2, 6, 14)
  override var position: Position = new Position(x, y)

  override def action(distance: Double): String = {
    //println("distance " + distance)
    if (distance <= 110 && distance > 0) {

      return ListAction(1)

    }
    if (distance == 0) {
      return ListAction(2)
    }
    if (HP <= 100) {

      return ListAction(3)

    }
    return ListAction.head
  }
}
