package ex2.monsters

import ex2.logic.MessageTypeEnum.MessageTypeEnum
import ex2.logic.{MessageTypeEnum, Position}

/**
  * Created by oxeyo on 10/04/2018.
  */
class Solar(x: Double, y: Double) extends Serializable with Monster {
  override var HP: Int = 363
  override var Armor: Int = 44
  override var ListAction: List[MessageTypeEnum] = List(MessageTypeEnum.MOVE, MessageTypeEnum.RANGED, MessageTypeEnum.MELEE, MessageTypeEnum.HEAL)
  override var Speed: Int = 50
  Regeneration = 15
  override var damageMelee: Damage = Damage(3, 6, 18)
  DamageReduction = 15
  canHeal = true
  MeleeAtckCount = 4
  RangedAtckCount = 4
  MeleeAtckChance = List(35, 30, 25, 20).map(_ + 5)
  RangedAtckChance = List(31, 26, 21, 16).map(_ + 5)
  override var damageRanged: Damage = Damage(2, 6, 14)
  override var position: Position = new Position(x, y)
  healPower = 50 * 3

  override def action(distance: Double): MessageTypeEnum = {
    if (distance == 0) {
      return ListAction(2)
    }
    if (distance > 0 && distance <= 110) {

      return ListAction(1)

    }

    ListAction.head
  }

  override var maxHp: Int = 363

}
