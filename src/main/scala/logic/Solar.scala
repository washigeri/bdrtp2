package logic

/**
  * Created by oxeyo on 10/04/2018.
  */
class Solar extends Serializable with Monster  {
  override var HP: Int = 363
  override var Armor: Int = 14
  override var ListAction: List[String] = List("move","ranged","melee","heal");
  override var Speed: Int = 50
  override var Regeneration = 15
  override def action(distance: Int): String = {
    if(distance <= 110 && distance >0 ){
      ListAction(1);
    }
    if(distance==0){
      ListAction(2);
    }
    if(HP<=100){
      ListAction(3)
    }
    ListAction(0);
  }
}
