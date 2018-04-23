package ex2.monsters

import ex2.logic.MessageTypeEnum.MessageTypeEnum
import ex2.logic.{Message, MessageTypeEnum, Position}

import scala.collection.mutable.ArrayBuffer

class Dragon(x: Double, y: Double) extends Serializable with Monster with Ennemy {
  override var HP: Int = 391
  override var Armor: Int = 37
  override var ListAction: List[MessageTypeEnum] = List(MessageTypeEnum.MOVE, MessageTypeEnum.MELEE, MessageTypeEnum.RANGED)
  override var Speed: Int = 40
  override var maxHp: Int = 391
  override var damageMelee: Damage = Damage(4, 8, 21)
  override var damageRanged: Damage = Damage(24, 6, 0)
  override var position: Position = new Position(x, y)
  DamageReduction = 20
  MeleeAtckCount = 1
  RangedAtckCount = 3
  RangedAtckChance = List(31, 31, 31)
  MeleeAtckChance = List(33)
  var flying = false
  var alterself = true

  override def action(distance: Double): MessageTypeEnum = {
    if (distance == 0 && !flying) {
      ListAction(1)
    }
    else if (distance > 0 && flying && distance <= 70) {
      ListAction(2)
    }
    ListAction.head
  }

  def IA(messages: ArrayBuffer[Message]): ArrayBuffer[Message] = {
    ArrayBuffer[Message]()
  }

}
