package logic

class Warlord extends Serializable with Monster {

  override var HP: Int = 141
  override var Armor: Int = 10
  override var ListAction: List[String] = List("move","ranged","melee")
  override var Speed: Int = 30

  override def action(distance: Int): String = {
    if(distance < 40 && distance > 0){
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
