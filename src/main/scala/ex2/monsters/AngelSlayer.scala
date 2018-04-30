package ex2.monsters

import ex2.logic.MessageTypeEnum.MessageTypeEnum
import ex2.logic.{MessageTypeEnum, Position}

/**
  * Created by oxeyo on 10/04/2018.
  */
class AngelSlayer(x: Double, y: Double) extends Serializable with Monster with Ennemy {
  override var HP: Int = 112
  override var Armor: Int = 26
  override var ListAction: List[MessageTypeEnum] = List(MessageTypeEnum.MOVE, MessageTypeEnum.RANGED, MessageTypeEnum.MELEE, MessageTypeEnum.HEAL)
  override var Speed: Int = 40
  Regeneration = 0
  override var damageMelee: Damage = Damage(1, 8, 7)
  DamageReduction = 0
  canHeal = true
  MeleeAtckCount = 3
  RangedAtckCount = 3
  MeleeAtckChance = List(21, 16, 11).map(_ + 1)
  RangedAtckChance = List(21, 16, 11).map(_ + 0)
  override var damageRanged: Damage = Damage(1, 8, 6)
  override var position: Position = new Position(x, y)
  healPower = 50 * 3
  override var maxHp: Int = 112

  override def action(distance: Double,flying:Boolean): MessageTypeEnum = {
    if (distance == 0) {
      return ListAction(2)
    }
    if (distance > 0 && distance <= 110) {

      return ListAction(1)

    }

    ListAction.head
  }

}
