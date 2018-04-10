package logic

trait Monster {
  var HP: Int
  var Armor: Int
  var ListAction: List[String]
  var Speed: Int

  def action(distance: Int): String
}
