package ex2.logic

import ex2.logic.MessageTypeEnum.MessageTypeEnum
import ex2.monsters.Monster

case class Message(source: Monster, srcid: Int, dest: Monster, dstid: Int, typem: MessageTypeEnum, var value: Int = 0) extends Serializable {
  override def equals(obj: scala.Any): Boolean = {
    obj match {
      case that: Message => that.srcid == srcid && that.dstid == dstid && that.typem == typem
      case _ => false
    }
  }
}


object MessageTypeEnum extends Enumeration with Serializable {
  type MessageTypeEnum = Value
  val MELEE, RANGED, MOVE, HEAL = Value
}