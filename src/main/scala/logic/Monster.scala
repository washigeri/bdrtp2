package logic

trait Monster {
  var HP: Int
  var Armor: Int
  var ListAction: List[String]
  var Speed: Int
  var Regeneration: Int = 0

  def action(distance: Int): String
}
