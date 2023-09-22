package snake.game

import processing.core.PImage

class Object (
               val id : Int,
               var objType : Int,
               var Image : PImage,
               var PosX : Float,
               var PosY : Float,
               val Scale : Float,
               var Health : Double
             ) {
  val OriginalHealth = Health
  val OriginalobjType = objType

  var Timer: Int = 0
}