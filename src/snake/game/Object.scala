package snake.game

import processing.core.PImage

class Object (
               val id : Int,
               val objType : Int,
               val Image : PImage,
               var PosX : Float,
               var PosY : Float,
               val Scale : Float,
               var Health : Double
             ) {
}
