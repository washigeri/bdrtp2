package logic

import logic.MessageTypeEnum.MessageTypeEnum

class Warlord(x: Double, y: Double) extends Serializable with Monster {


  override var HP: Int = 141
  override var Armor: Int = 10
  override var ListAction: List[MessageTypeEnum] = List(MessageTypeEnum.MOVE, MessageTypeEnum.RANGED, MessageTypeEnum.MELEE)
  override var Speed: Int = 30
  override var damageMelee: Damage = Damage(1, 8, 20)

  MeleeAtckCount = 3
  RangedAtckCount = 1
  MeleeAtckChance = List(20, 15, 10).map(_ + 1)
  RangedAtckChance = List(19)
  override var damageRanged: Damage = Damage(1, 6, 5)
  override var position: Position = new Position(x, y)

  override def action(distance: Double): MessageTypeEnum = {
    if (distance < 40 && distance > 0) {
      ListAction(1)
    }
    else if (distance == 0) {
      ListAction(2)
    }
    else {
      ListAction.head
    }
  }

  override var maxHp: Int = 141
}
