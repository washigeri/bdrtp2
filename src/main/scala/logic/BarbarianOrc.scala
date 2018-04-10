package logic

class BarbarianOrc extends Serializable with Monster {
  override var HP: Int = 142
  override var Armor: Int = 17
  override var ListAction: List[String] = List("melee", "move", "ranged")
  override var Speed: Int = 40

  override def action(distance: Int): String = {
    if ((distance <= 50) && (distance > 0))
      ListAction(2)
    else if (distance == 0)
      ListAction.head
    else
      ListAction(1)
  }
}
