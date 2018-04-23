package ex2.monsters

import ex2.logic.MessageTypeEnum.MessageTypeEnum
import ex2.logic.{MessageTypeEnum, Position}

class DeathDealer(x: Double, y: Double) extends Serializable with Monster with Ennemy {
  override var HP: Int = 243
  override var Armor: Int = 13
  override var ListAction: List[MessageTypeEnum] = List(MessageTypeEnum.RANGED, MessageTypeEnum.MELEE, MessageTypeEnum.MOVE)
  override var Speed: Int = 40
  override var maxHp: Int = 243
  override var damageMelee: Damage = Damage(3, 6, 16)
  override var damageRanged: Damage = Damage(1, 8, 10)
  override var position: Position = new Position(x, y)
  MeleeAtckCount = 4
  MeleeAtckChance = List(31, 26, 21, 16)
  RangedAtckCount = 4
  RangedAtckChance = List(22, 17, 12, 7)

  override def action(distance: Double): MessageTypeEnum = {
    if (distance == 0) {
      ListAction(1)
    }
    else {
      ListAction(2)
    }
  }
}
