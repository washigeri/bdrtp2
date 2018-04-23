package ex2.logic

/**
  * Created by oxeyo on 17/04/2018.
  */
class Position(var x: Double, var y: Double) extends Serializable {

  def setPosition(x: Double, y: Double): Unit = {
    this.x = x
    this.y = y
  }
}
