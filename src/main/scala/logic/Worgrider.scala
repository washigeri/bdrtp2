package logic

class Worgrider(val x: Double, val y: Double) extends Serializable with Monster {
  override var HP: Int = 13
  override var Armor: Int = 18
  override var Speed: Int = 20
  override var ListAction: List[String] = List("move", "melee")
  override var damageMelee: Damage = Damage(1, 8, 2)

  MeleeAtckChance = List(6)
  MeleeAtckCount = 1
  RangedAtckCount = 0
  override var damageRanged: Damage = _
  override var position: Position = new Position(x, y)

  override def action(distance: Double): String = {
    if (distance == 0) {
      ListAction(1)
    }
    else
      ListAction.head
  }
}
