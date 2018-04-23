package ex2.monsters

import ex2.logic.MessageTypeEnum.MessageTypeEnum
import ex2.logic.{Message, MessageTypeEnum, Position}

import scala.collection.mutable.ArrayBuffer
import scala.util.Random

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
  flying = false
  alterself = true

  override def action(distance: Double): MessageTypeEnum = {
    if (distance == 0 && !flying) {
      ListAction(1)
    }
    else if (distance > 0 && flying && distance <= 70) {
      ListAction(2)
    }
    ListAction.head
  }

  override def IA(messages: ArrayBuffer[Message]): ArrayBuffer[Message] = {
    var actionBuffer = ArrayBuffer[Message]()
    var actionBufferRanged = ArrayBuffer[Message]()
    for(action <- messages){
      if (alterself) {

        if(action.dstid==1 && action.typem==MessageTypeEnum.MOVE){
          actionBuffer.+=(action)
        }
        if(action.dstid==1 && action.typem==MessageTypeEnum.MELEE){
          if(Random.nextInt(20)+MeleeAtckChance.head>=action.dest.Armor){
            action.value=this.damageMelee.roll()
          }else{
            action.value=0
          }

          alterself=false
          actionBuffer = ArrayBuffer[Message](action)
          flying=true
        }

      }else{
        if(flying && action.typem==MessageTypeEnum.RANGED && actionBufferRanged.size<3){
          if(Random.nextInt(20)+RangedAtckChance.head>=action.dest.Armor){
            action.value=this.damageRanged.roll()
          }else{
            action.value=0
          }
          actionBufferRanged.+=(action)
        }
      }

    }
    if (actionBufferRanged.size <= 3 && actionBufferRanged.nonEmpty) {
      return actionBufferRanged
    }
    actionBuffer

  }

}
