package logic

trait Monster {
  var HP: Int
  var Armor: Int
  var ListAction:List[String]

  def action
}
