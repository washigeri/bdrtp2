package logic

class Warlord extends Serializable with Monster {

  override var HP: Int = 141
  override var Armor: Int = 10
  override var ListAction: List[String] = List("move","ranged","melee")

  override def action(distance: Int): String = {
    if(distance > 40){
      ListAction(0)
    }
    if(distance < 40 && distance > 0){
      ListAction(1)
    }
    if(distance == 0){
      ListAction(2)
    }
  }

}
