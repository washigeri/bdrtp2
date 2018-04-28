package ex2.monsters

import ex2.logic.MessageTypeEnum.MessageTypeEnum
import ex2.logic.{MessageTypeEnum, Position}

class Warlord(x: Double, y: Double) extends Serializable with Monster with Ennemy {


  override var HP: Int = 141
  override var Armor: Int = 27
  override var ListAction: List[MessageTypeEnum] = List(MessageTypeEnum.MOVE, MessageTypeEnum.RANGED, MessageTypeEnum.MELEE)
  override var Speed: Int = 30
  override var damageMelee: Damage = Damage(1, 8, 20)

  MeleeAtckCount = 3
  RangedAtckCount = 0
  MeleeAtckChance = List(20, 15, 10).map(_ + 1)
  override var damageRanged: Damage = _
  override var position: Position = new Position(x, y)
  override var maxHp: Int = 141

  override def action(distance: Double): MessageTypeEnum = {
    if (distance == 0) {
      ListAction(2)
    }
    else {
      ListAction.head
    }
  }
}
