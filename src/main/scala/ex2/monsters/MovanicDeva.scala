package ex2.monsters

import ex2.logic.MessageTypeEnum.MessageTypeEnum
import ex2.logic.{MessageTypeEnum, Position}

class MovanicDeva(x: Double, y: Double) extends Serializable with Monster with Angel {
  override var HP: Int = 126
  override var Armor: Int = 24
  override var ListAction: List[MessageTypeEnum] = List(MessageTypeEnum.MOVE, MessageTypeEnum.MELEE, MessageTypeEnum.HEAL)
  override var Speed: Int = 40
  override var maxHp: Int = 126
  override var damageMelee: Damage = Damage(4, 6, 0)
  override var damageRanged: Damage = _
  override var position: Position = new Position(x, y)
  canHeal = true
  healPower = 50 * 3
  MeleeAtckCount = 3
  MeleeAtckChance = List(17, 12, 7)
  RangedAtckCount = 0
  DamageReduction = 10

  override def action(distance: Double,flying:Boolean): MessageTypeEnum = {
    if (distance == 0 && !flying) {
      return ListAction(1)
    }
    ListAction.head
  }
}
