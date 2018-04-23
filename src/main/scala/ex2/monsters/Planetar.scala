package ex2.monsters

import ex2.logic.MessageTypeEnum.MessageTypeEnum
import ex2.logic.{MessageTypeEnum, Position}

/**
  * Created by oxeyo on 10/04/2018.
  */
class Planetar(x: Double, y: Double) extends Serializable with Monster with Angel {
  override var HP: Int = 229
  override var Armor: Int = 32
  override var ListAction: List[MessageTypeEnum] = List(MessageTypeEnum.MOVE, MessageTypeEnum.MELEE, MessageTypeEnum.HEAL)
  override var Speed: Int = 10
  Regeneration = 10
  override var damageMelee: Damage = Damage(3, 6, 15)
  DamageReduction = 10
  canHeal = true
  MeleeAtckCount = 3
  RangedAtckCount = 0
  MeleeAtckChance = List(27,22,17 ).map(_ + 3)

  override var damageRanged: Damage = Damage(2, 6, 14)
  override var position: Position = new Position(x, y)
  healPower = 50 * 3

  override def action(distance: Double): MessageTypeEnum = {
    if (distance == 0) {
      return ListAction(1)
    }



    ListAction.head
  }

  override var maxHp: Int = 229

}
