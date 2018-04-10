package logic

class Worgrider extends Serializable with Monster {
  override var HP: Int = 13
  override var Armor: Int = 18
  override var Speed: Int = 20
  override var ListAction: List[String] = List("move", "melee")

  override def action(distance: Int): String = {
    if (distance == 0) {
      ListAction(1)
    }
    else
      ListAction.head
  }
}
