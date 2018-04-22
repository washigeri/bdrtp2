package logic

import logic.MessageTypeEnum.MessageTypeEnum

class Message(val source: Monster, val srcid: Int, val dest: Monster, val dstid: Int, val typem: MessageTypeEnum, var value: Int = 0) extends Serializable {

}


object MessageTypeEnum extends Enumeration with Serializable {
  type MessageTypeEnum = Value
  val MELEE, RANGED, MOVE, HEAL = Value
}