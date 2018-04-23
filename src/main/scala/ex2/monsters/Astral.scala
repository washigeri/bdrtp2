package ex2.monsters

import ex2.logic.MessageTypeEnum.MessageTypeEnum
import ex2.logic.{MessageTypeEnum, Position}

/**
  * Created by oxeyo on 10/04/2018.
  */
class Astral(x: Double, y: Double) extends Serializable with Monster {
  override var HP: Int = 172
  override var Armor: Int = 29
  override var ListAction: List[MessageTypeEnum] = List(MessageTypeEnum.MOVE, MessageTypeEnum.MELEE, MessageTypeEnum.HEAL)
  override var Speed: Int = 50
  Regeneration = 0
  override var damageMelee: Damage = Damage(1, 8, 14)
  DamageReduction = 10
  canHeal = true
  MeleeAtckCount = 3
  RangedAtckCount = 0
  MeleeAtckChance = List(26,21,16 ).map(_ + 2)
  RangedAtckChance = _
  override var damageRanged: Damage = Damage(2, 6, 14)
  override var position: Position = new Position(x, y)
  healPower = 50 * 3

  override def action(distance: Double): MessageTypeEnum = {
    if (distance == 0) {
      return ListAction(1)
    }



    ListAction.head
  }

  override var maxHp: Int = 172

}
