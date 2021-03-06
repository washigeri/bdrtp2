package ex2.monsters

import ex2.logic.MessageTypeEnum.MessageTypeEnum
import ex2.logic.{MessageTypeEnum, Position}

class BarbarianOrc(x: Double, y: Double) extends Serializable with Monster with Ennemy {

  override var HP: Int = 142
  override var Armor: Int = 17
  override var ListAction: List[MessageTypeEnum] = List(MessageTypeEnum.MELEE, MessageTypeEnum.MOVE, MessageTypeEnum.RANGED)
  override var Speed: Int = 40
  override var damageMelee: Damage = Damage(1, 8, 10)

  MeleeAtckCount = 3
  RangedAtckCount = 0

  MeleeAtckChance = List(19, 14, 9).map(_ + 1)
  RangedAtckChance = List(16, 11, 6)
  override var damageRanged: Damage = Damage(1, 8, 6)
  override var position: Position = new Position(x, y)
  override var maxHp: Int = 142

  override def action(distance: Double,flying:Boolean): MessageTypeEnum = {
    //if ((distance <= 50) && (distance > 0))
    // ListAction(2)
    if (distance == 0)
      ListAction.head
    else
      ListAction(1)
  }
}
