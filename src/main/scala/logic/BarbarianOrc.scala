package logic

class BarbarianOrc(val x: Double, val y: Double) extends Serializable with Monster {
  override var HP: Int = 142
  override var Armor: Int = 17
  override var ListAction: List[String] = List("melee", "move", "ranged")
  override var Speed: Int = 40
  override var damageMelee: Damage = Damage(1, 8, 10)

  MeleeAtckCount = 3
  RangedAtckCount = 3

  MeleeAtckChance = List(19, 14, 9).map(_ + 1)
  RangedAtckChance = List(16, 11, 6)
  override var damageRanged: Damage = Damage(1, 8, 6)
  override var position: Position = new Position(x, y)

  override def action(distance: Double): String = {
    if ((distance <= 50) && (distance > 0))
      ListAction(2)
    else if (distance == 0)
      ListAction.head
    else
      ListAction(1)
  }
}
