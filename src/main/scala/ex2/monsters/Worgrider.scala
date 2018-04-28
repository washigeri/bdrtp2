package ex2.monsters

import ex2.logic.MessageTypeEnum.MessageTypeEnum
import ex2.logic.{MessageTypeEnum, Position}

class Worgrider(x: Double, y: Double) extends Serializable with Monster with Ennemy {

  override var HP: Int = 13
  override var Armor: Int = 18
  override var Speed: Int = 20
  override var ListAction: List[MessageTypeEnum] = List(MessageTypeEnum.MOVE, MessageTypeEnum.MELEE)
  override var damageMelee: Damage = Damage(1, 8, 2)

  MeleeAtckChance = List(6)
  MeleeAtckCount = 1
  RangedAtckCount = 0
  override var damageRanged: Damage = _
  override var position: Position = new Position(x, y)
  override var maxHp: Int = 13

  override def action(distance: Double): MessageTypeEnum = {
    if (distance == 0) {
      ListAction(1)
    }
    else
      ListAction.head
  }
}
