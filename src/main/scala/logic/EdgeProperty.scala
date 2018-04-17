package logic

import logic.RelationType.RelationType

object RelationType extends Enumeration with Serializable {
  type RelationType = Value
  val FRIEND, ENEMY = Value
}

case class EdgeProperty(relationType: RelationType) extends Serializable {
  def getRelation: RelationType = {
    relationType
  }

}
