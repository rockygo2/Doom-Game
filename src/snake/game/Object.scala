package snake.game

import processing.core.PImage

class Object (
               var objType : Int,
               var Image : PImage,
               var PosX : Float,
               var PosY : Float,
               val Scale : Float,
               var Health : Double = 0,
               val  Damage : Double = 0
             ) {
  val OriginalHealth = Health
  val OriginalobjType = objType

  var Timer: Int = 0
  var BulletTimer: Int = 0
  var BulletX: Float = 0
  var BulletY: Float = 0
}