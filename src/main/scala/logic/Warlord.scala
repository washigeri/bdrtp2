package logic

class Warlord(val x: Double, val y: Double) extends Serializable with Monster {

  override var HP: Int = 141
  override var Armor: Int = 10
  override var ListAction: List[String] = List("move", "ranged", "melee")
  override var Speed: Int = 30
  override var damageMelee: Damage = Damage(1, 8, 20)

  MeleeAtckCount = 3
  RangedAtckCount = 1
  MeleeAtckChance = List(20, 15, 10).map(_ + 1)
  RangedAtckChance = List(19)
  override var damageRanged: Damage = Damage(1, 6, 5)
  override var position: Position = new Position(x, y)

  override def action(distance: Double): String = {
    if (distance < 40 && distance > 0) {
      ListAction(1)
    }
    else if (distance == 0) {
      ListAction(2)
    }
    else {
      ListAction.head
    }
  }
}
