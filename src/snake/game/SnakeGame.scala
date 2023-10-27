// DO NOT MODIFY FOR BASIC SUBMISSION
// scalastyle:off

package snake.game
import scala.math._
import processing.core._
import processing.event.KeyEvent
import ddf.minim.{AudioPlayer, AudioSample, Minim}
import snake.game.Walls
import snake.game.Object

import java.awt.event.KeyEvent._
import scala.runtime.ObjectRef


class SnakeGame extends PApplet with SnakeGameTrait{


  // My map does look like this yes
  var map: Array[Array[Int]] = Array(
    Array(5,5,5,5,5,5,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5),
    Array(5,0,0,0,0,0,8,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,8,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,5),
    Array(5,0,0,0,0,0,8,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,8,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,5),
    Array(5,0,0,0,0,0,8,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,8,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,5),
    Array(5,0,0,0,0,0,8,0,0,0,0,0,0,0,0,0,0,0,0,0,0,8,8,0,0,0,0,0,0,0,0,8,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,5),
    Array(5,0,0,0,0,0,8,0,0,0,0,0,0,0,0,0,0,0,0,0,0,8,0,8,8,8,0,0,0,0,8,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,5),
    Array(5,0,0,0,0,0,8,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,8,0,0,0,8,0,0,8,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,5),
    Array(5,0,0,0,0,0,8,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,8,0,0,0,8,8,0,0,0,0,0,0,0,0,0,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,0,5),
    Array(5,0,0,0,0,0,8,0,0,0,0,0,0,0,0,0,8,8,8,8,8,8,8,8,8,0,0,0,0,0,0,0,0,0,0,0,0,0,9,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,9,0,5),
    Array(5,0,0,0,0,0,8,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,8,0,0,0,0,0,0,0,0,0,0,0,0,9,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,9,0,5),
    Array(5,0,0,0,0,0,8,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,8,8,0,0,0,0,0,0,0,0,0,0,9,9,9,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,9,9,9,0,5),
    Array(5,0,0,0,0,0,8,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,8,0,0,0,0,0,0,0,0,0,0,0,0,9,9,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,9,9,0,0,5),
    Array(5,0,0,0,0,0,8,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,8,0,0,0,0,0,0,0,0,0,0,0,9,9,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,9,9,0,0,0,5),
    Array(5,0,0,0,0,0,0,8,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,8,8,8,8,8,8,8,8,8,9,9,9,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,9,9,9,0,5),
    Array(5,0,0,0,0,0,0,0,8,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,8,9,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,9,0,5),
    Array(5,0,0,0,0,0,0,0,0,8,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,8,9,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,9,0,5),
    Array(5,0,0,0,0,0,0,0,0,0,8,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,8,9,9,9,9,9,9,9,9,9,0,9,0,0,9,0,9,9,9,9,9,9,9,9,9,0,5),
    Array(5,0,0,0,0,0,0,0,0,0,0,8,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,8,8,8,8,8,8,0,0,8,0,0,0,0,0,0,0,0,9,0,9,0,0,9,0,9,0,0,0,0,0,0,0,0,0,5),
    Array(5,0,0,0,0,0,0,0,0,0,0,0,8,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,8,0,0,0,0,0,8,0,0,8,0,0,0,0,0,0,0,0,9,0,9,9,9,9,0,9,0,0,9,9,9,9,9,9,9,5),
    Array(5,0,0,0,0,0,0,0,0,0,0,0,0,8,0,0,0,0,0,0,0,0,0,0,0,0,0,8,0,0,0,0,0,0,8,0,0,8,0,0,0,0,0,0,0,0,9,0,0,0,0,0,0,9,0,0,9,0,0,0,0,0,9,5),
    Array(5,0,0,0,0,0,0,0,0,0,0,0,0,0,8,0,0,0,0,0,0,0,0,0,0,0,8,0,0,0,6,6,6,6,8,0,0,8,6,6,0,0,0,0,0,0,9,0,9,9,9,9,0,9,9,9,9,0,9,0,9,0,9,5),
    Array(5,0,0,0,0,0,0,0,0,0,0,0,0,0,0,8,0,0,0,0,0,0,0,0,0,8,0,0,0,0,6,0,0,0,0,0,0,0,0,6,0,0,0,9,9,9,0,0,0,0,0,0,0,0,0,0,0,0,9,0,9,0,9,5),
    Array(5,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,8,0,0,0,0,0,0,8,8,0,0,0,0,0,6,0,0,0,0,0,0,0,0,6,0,0,0,9,0,9,9,9,9,0,9,9,9,9,9,9,9,9,9,0,9,0,9,5),
    Array(5,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,8,8,8,8,8,8,0,0,0,0,0,0,0,6,0,0,0,0,0,0,0,0,6,0,0,0,9,0,0,0,0,0,0,9,0,0,0,0,0,0,9,0,0,9,0,9,5),
    Array(5,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,6,0,0,0,0,0,0,0,0,6,0,0,0,9,0,9,9,9,9,0,9,9,9,9,0,9,0,9,9,9,9,0,9,5),
    Array(6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,1,1,1,1,1,1,1,1,1,1,1,1,0,0,0,0,0,0,0,0,9,9,9,9,9,0,9,0,0,0,0,0,0,0,0,0,9,0,0,0,0,9,0,9,5),
    Array(6,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,9,9,9,9,9,9,0,0,9,0,0,0,0,9,0,9,5),
    Array(6,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,9,9,9,9,9,9,9,9,0,0,0,0,0,9,0,0,9,9,9,9,9,9,9,0,5),
    Array(6,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,1,1,1,1,1,1,1,1,1,0,0,0,0,0,0,0,0,6,0,0,0,0,0,0,0,0,0,0,0,0,9,0,0,0,0,0,0,0,0,9,0,5),
    Array(6,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,1,6,0,0,0,0,0,0,0,0,6,0,0,0,0,0,0,0,0,0,0,0,0,9,0,0,0,0,0,0,0,0,9,0,5),
    Array(6,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,1,6,0,0,0,0,0,0,0,0,6,0,0,0,0,0,0,0,0,0,0,0,0,9,0,0,0,0,0,0,0,0,9,0,5),
    Array(6,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,1,6,0,0,0,0,0,0,0,0,6,0,0,0,0,0,0,0,0,0,0,0,0,9,0,0,0,0,0,0,0,0,9,0,5),
    Array(6,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,1,6,6,6,6,6,6,6,6,6,6,0,0,0,0,0,0,0,0,0,0,0,0,9,9,9,9,9,9,9,9,9,0,0,5),
    Array(6,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0,0,0,0,0,0,0,0,5),
    Array(6,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,1,0,0,0,0,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,5),
    Array(6,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,1,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,0,0,0,0,5),
    Array(6,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,1,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,0,0,5),
    Array(6,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,1,0,1,0,0,0,0,0,3,0,0,0,0,0,0,3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,5),
    Array(6,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3,0,0,0,0,0,0,0,0,1,0,5),
    Array(6,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,5),
    Array(6,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,5),
    Array(6,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,5),
    Array(6,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,5),
    Array(6,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,3,0,0,0,0,0,0,3,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,5),
    Array(6,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3,0,0,0,1,0,0,0,0,0,0,5),
    Array(6,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,5),
    Array(6,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0,0,1,0,0,0,0,0,0,0,0,5),
    Array(6,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,5),
    Array(6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,0,0,0,0,0,0,0,0,5),
    Array(5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5),
  )

  // Miscellanious CONSTANT Variables
  val FOV: Int = 100
  val BOX_SIZE: Int = 320
  val NUM_RAYS: Int = 400
  val SCREEN_SIZE: Int = 800
  val HALF_SCREEN_SIZE: Int = SCREEN_SIZE / 2

  // KEYS
  var UP_PRESS: Boolean = false
  var DOWN_PRESS: Boolean = false
  var LEFT_PRESS: Boolean = false
  var RIGHT_PRESS: Boolean = false
  var D_PRESS: Boolean = false
  var A_PRESS: Boolean = false
  var SPACE_PRESS: Boolean = false

  // Player Variables
  var PlayerX: Double = 10120
  var PlayerY: Double = 11361
  var PlayerAngle: Int = 180
  var ShootingTimer: Int = 0
  var PlayerHealth : Double = 100
  var PlayerSpeed: Double = 25
  var PlayerDamage: Double = 50
  var WallDistanceShooting : Double = 0;
  var TotalKeys: Int = 0

  // Image Handling Arrays
  var WallArr : Array[Walls] = Array()
  var ObjectArr : Array[Object] = Array()
  var ShootingArr : Array[PImage] = Array()
  var ExplosionArr : Array[PImage] = Array()
  var TexturesArr : Array[PImage] = _
  var Bullet : PImage = _

  // Sounds
  var minim : Minim = _
  var ShotgunSound : AudioSample = _
  def canSeePlayer(PosX : Double, PosY : Double): Boolean = {

    // bad function need to fix :(
    val PosX2 = PosX - 60
    val PosY2 = PosY - 60
    val SpriteX = PlayerX - PosX2
    val SpriteY = PlayerY - PosY

    val TanInverse: Double = Helper.getTanInverse(SpriteX,SpriteY)
    val CurrentDirX: Float = cos(toRadians(TanInverse)).toFloat
    val CurrentDirY: Float = sin(toRadians(TanInverse)).toFloat

    var RayDirX: Double = CurrentDirX
    var RayDirY: Double = CurrentDirY
    var CurrentBoxX: Int = PosX2.toInt / BOX_SIZE
    var CurrentBoxY: Int = PosY2.toInt / BOX_SIZE

    val startX = PosX2 / BOX_SIZE
    val startY = PosY2 / BOX_SIZE

    var CurrentX: Double = PosX / BOX_SIZE
    var CurrentY: Double = PosY2 / BOX_SIZE
    var isX: Boolean = false
    var hit: Boolean = false

    while (!hit) {
      val DistX: Double = if (RayDirX < 0) CurrentBoxX - CurrentX else 1 - (CurrentX - CurrentBoxX)
      val DistY: Double = if (RayDirY < 0) CurrentBoxY - CurrentY else 1 - (CurrentY - CurrentBoxY)

      if (RayDirX == 0) RayDirX = 0.00000000000000001
      if (RayDirX == 0) RayDirY = 0.00000000000000001
      val DistTimeTakenX = DistX / RayDirX
      val DistTimeTakenY = DistY / RayDirY

      if (abs(DistTimeTakenX) < abs(DistTimeTakenY)) {
        CurrentX += DistTimeTakenX * RayDirX
        CurrentY += DistTimeTakenX * RayDirY
        if (RayDirX < 0) CurrentBoxX -= 1 else CurrentBoxX += 1
        isX = true
      }
      else {
        CurrentX += DistTimeTakenY * RayDirX
        CurrentY += DistTimeTakenY * RayDirY
        if (RayDirY < 0) CurrentBoxY -= 1 else CurrentBoxY += 1
        isX = false
      }

      if (map(CurrentBoxX)(CurrentBoxY) > 0) {
        hit = true
        if(abs(startX - CurrentX) > abs(PlayerX/BOX_SIZE - PosX2/BOX_SIZE) && abs(startY - CurrentY) > abs(PlayerY/BOX_SIZE - PosY2/BOX_SIZE)){
          return true
        }
        else{
          return false
        }
      }
    }
    false
  }

  def drawSprite(): Unit = {
    for (i <- ObjectArr) {
      val SpriteX: Double = i.PosX - PlayerX
      val SpriteY: Double = i.PosY - PlayerY
      val WallDistance: Float = sqrt(pow(SpriteX, 2) + pow(SpriteY, 2)).toFloat
      val arcTan: Double = math.toDegrees(math.atan2(SpriteY, SpriteX))

      // Makes a positive angle difference Mathematically
      val AngleDif: Double = ((arcTan - PlayerAngle + 540) % 360) - 180

      val XSpriteLocation: Double = (FOV / 2 + AngleDif) * SCREEN_SIZE / FOV
      val proj = (100 / WallDistance * i.Scale) * 4000
      val WallHeight = (SCREEN_SIZE / WallDistance) * 69

      val YSpriteLocation = HALF_SCREEN_SIZE + WallHeight - proj


      if (XSpriteLocation > 0 - proj && XSpriteLocation < SCREEN_SIZE && (YSpriteLocation > 0 && YSpriteLocation < SCREEN_SIZE) ){
        val DrawEnemy: PImage = i.Image.copy()
        DrawEnemy.resize(proj.toInt, proj.toInt);

        if (WallDistance > 0) {
          val input: Walls = new Walls(DrawEnemy, WallDistance, XSpriteLocation.toFloat, YSpriteLocation)
          WallArr = WallArr :+ input
        }
      }
    }
  }

  def drawWalls(): Unit = {
    val sortedWallArray = WallArr.sortBy(_.WallDistance).reverse
    for (i <- sortedWallArray.indices){
      image(sortedWallArray(i).Image, sortedWallArray(i).PosX, sortedWallArray(i).PosY)
    }
  }

  def BlowUp(currentSprite: Object): Unit = {
    if (currentSprite.Timer < 5) currentSprite.Image = ExplosionArr(0)
    else if (currentSprite.Timer < 10) currentSprite.Image = ExplosionArr(1)
    else if (currentSprite.Timer < 15) currentSprite.Image = ExplosionArr(2)
    else RemoveObject(currentSprite)
    currentSprite.Timer += 1
  }
  def drawRays(): Unit = {
    WallArr = Array()

    val LineSize : Int = SCREEN_SIZE/NUM_RAYS
    val RayScale : Double = FOV/NUM_RAYS.toDouble
    val imgSizeCheck = 64 - LineSize
    val halfRays = NUM_RAYS/2
    for (i <- 0 until NUM_RAYS){
      val RayAngle = PlayerAngle - FOV/2 + i*RayScale
      var RayDirX : Double = cos(toRadians(RayAngle))
      var RayDirY : Double = sin(toRadians(RayAngle))
      var CurrentBoxX : Int = PlayerX.toInt/BOX_SIZE
      var CurrentBoxY : Int = PlayerY.toInt/BOX_SIZE
      var CurrentX: Double = PlayerX/BOX_SIZE
      var CurrentY: Double = PlayerY/BOX_SIZE
      var isX : Boolean = false

      var hit : Boolean = false

      while(!hit){
        val DistX: Double = if (RayDirX < 0) CurrentBoxX - CurrentX else 1 - (CurrentX - CurrentBoxX)
        val DistY: Double = if (RayDirY < 0) CurrentBoxY - CurrentY else 1 - (CurrentY - CurrentBoxY)

        if (RayDirX == 0) RayDirX = 0.00000000000000001
        if (RayDirX == 0) RayDirY = 0.00000000000000001
        val DistTimeTakenX = DistX / RayDirX
        val DistTimeTakenY = DistY / RayDirY

        if (abs(DistTimeTakenX) < abs(DistTimeTakenY)) {
          CurrentX += DistTimeTakenX * RayDirX
          CurrentY += DistTimeTakenX * RayDirY
          if (RayDirX < 0) CurrentBoxX -= 1 else CurrentBoxX += 1
          isX = true
        }
        else {
          CurrentX += DistTimeTakenY * RayDirX
          CurrentY += DistTimeTakenY * RayDirY
          if (RayDirY < 0) CurrentBoxY -= 1 else CurrentBoxY += 1
          isX = false
        }

        if (map(CurrentBoxX)(CurrentBoxY) > 0){
          hit = true
        }
      }
      //Fix FishEyeEffect
      val depth: Double = math.cos(toRadians(PlayerAngle - RayAngle))
      val DistX : Double = PlayerX - CurrentX*BOX_SIZE
      val DistY : Double = PlayerY - CurrentY*BOX_SIZE
      val WallDistance : Double = sqrt(pow(DistX, 2) + pow(DistY, 2))
      if(i == halfRays){
        WallDistanceShooting = WallDistance
      }
      val WallHeight = (SCREEN_SIZE/WallDistance) * 69/depth
      val BoxNuM= map(CurrentBoxX)(CurrentBoxY)
      var imageHeightY : Double = 64
      val ImageYLocation = 0

      if (imageHeightY <= 1) imageHeightY = 1
      val ImageX = if (!isX) abs((CurrentX - CurrentBoxX) * imgSizeCheck) else abs((CurrentY - CurrentBoxY) * imgSizeCheck)
      val croppedImg = TexturesArr(BoxNuM).get(ImageX.toInt, ImageYLocation, LineSize, imageHeightY.toInt)
      croppedImg.resize(LineSize, WallHeight.toInt*2);

      val input : Walls = new Walls(croppedImg , WallDistance.toFloat, i*LineSize, HALF_SCREEN_SIZE - WallHeight.toInt)
      WallArr = WallArr :+ input
    }
  }

  def setBackground(): Unit = {
    fill(200, 200, 200)
    rect(0, HALF_SCREEN_SIZE, SCREEN_SIZE, HALF_SCREEN_SIZE)
    fill(100, 100, 100)
    rect(0, 0, SCREEN_SIZE, HALF_SCREEN_SIZE)
  }

  def updateSprite(): Unit = {
    for (i <- ObjectArr){
      i.objType match {
        case 1 => Sprite1(i)
        case 2 => Sprite2(i)
        case 3 => {Sprite1(i); Sprite2(i)}
        case 69 => Sprite69(i)
        case 100 => Sprite100(i)
        case 1000 => Sprite1000(i)
        case 1001 => Sprite1001(i)
        case _ => {

        }
      }
    }
  }

  def RemoveObject(currentSprite : Object) : Unit= {
    ObjectArr = ObjectArr.filterNot(obj => obj == currentSprite)
  }
  def Sprite1(currentSprite : Object): Unit = {
    // Moving
    val Speed : Float = 10f
    if (currentSprite.Timer == 0 && canSeePlayer(currentSprite.PosX, currentSprite.PosY)) {
      val SpriteX = PlayerX - currentSprite.PosX
      val SpriteY = PlayerY - currentSprite.PosY
      if (SpriteX <= 300 && SpriteY <= 300 && SpriteX >= -300 && SpriteY >= -300) {
        BlowUp(currentSprite)
        PlayerHealth -= currentSprite.Damage
      }
      val TanInverse: Double = Helper.getTanInverse(SpriteX,SpriteY)
      val CurrentDirX: Float = cos(toRadians(TanInverse)).toFloat
      val CurrentDirY: Float = sin(toRadians(TanInverse)).toFloat

      val checkSpriteBoundsX: Float = currentSprite.PosX + Speed * CurrentDirX
      val checkSpriteBoundsY: Float = currentSprite.PosY + Speed * CurrentDirY
      currentSprite.PosX = checkSpriteBoundsX
      currentSprite.PosY = checkSpriteBoundsY
    }
    else if(currentSprite.Timer != 0) {
      BlowUp(currentSprite)
    }
  }
  def Sprite2(currentSprite : Object): Unit = {
    // Shooting
    if (currentSprite.Timer == 0) {
      if (currentSprite.BulletTimer == 0) {
        val newBullet = new Object( 100, Bullet, currentSprite.PosX, currentSprite.PosY, 0.3f, 100, currentSprite.Damage)
        val SpriteX = PlayerX - currentSprite.PosX
        val SpriteY = PlayerY - currentSprite.PosY

        val TanInverse: Double = Helper.getTanInverse(SpriteX,SpriteY)
        val CurrentDirX: Float = cos(toRadians(TanInverse)).toFloat
        val CurrentDirY: Float = sin(toRadians(TanInverse)).toFloat

        newBullet.BulletX = CurrentDirX
        newBullet.BulletY = CurrentDirY

        ObjectArr = ObjectArr :+ newBullet
        currentSprite.BulletTimer += 1
      }
      else if (currentSprite.BulletTimer == 130) {
        currentSprite.BulletTimer = 0
      }
      else {
        currentSprite.BulletTimer += 1
      }
    }
    else{
      BlowUp(currentSprite)
    }
  }
  def Sprite100(currentSprite : Object): Unit = {
    // Bullet
    val Speed = 90
    val checkSpriteBoundsX: Float = currentSprite.PosX + Speed * currentSprite.BulletX
    val checkSpriteBoundsY: Float = currentSprite.PosY + Speed * currentSprite.BulletY

    val SpriteX = PlayerX - currentSprite.PosX
    val SpriteY = PlayerY - currentSprite.PosY

    if (SpriteX <= 300 && SpriteY <= 300 && SpriteX  >= -300 && SpriteY >= -300) {
      ObjectArr = ObjectArr.filterNot(obj => obj == currentSprite)
      PlayerHealth -= currentSprite.Damage
      return
    }
    if (map(checkSpriteBoundsX.toInt / BOX_SIZE)(checkSpriteBoundsY.toInt / BOX_SIZE) == 0) {
      currentSprite.PosX = checkSpriteBoundsX
      currentSprite.PosY = checkSpriteBoundsY
    }
    else RemoveObject(currentSprite)
  }

  def Sprite1000(currentSprite : Object): Unit = {
    // HealthPack
    val SpriteX = PlayerX - currentSprite.PosX
    val SpriteY = PlayerY - currentSprite.PosY
    if (SpriteX <= 300 && SpriteY <= 300 && SpriteX >= -300 && SpriteY >= -300 && PlayerHealth < 100) {
      PlayerHealth += 50
      if (PlayerHealth > 100){
        PlayerHealth = 100
      }
      RemoveObject(currentSprite)
    }
  }

  def Sprite1001(currentSprite: Object): Unit = {
    // SpeedBoost
    val SpriteX = PlayerX - currentSprite.PosX
    val SpriteY = PlayerY - currentSprite.PosY
    if (currentSprite.Timer != 0){
      currentSprite.Timer += 1
      // 20 seconds
      if (currentSprite.Timer > 600){
        PlayerSpeed -= 25
        RemoveObject(currentSprite)
      }
      return
    }
    if (SpriteX <= 300 && SpriteY <= 300 && SpriteX >= -300 && SpriteY >= -300) {
      currentSprite.Timer = 1
      currentSprite.PosX = -1000
      PlayerSpeed += 25
    }
  }

  def Sprite69(currentSprite: Object): Unit = {
    // Keys
    val SpriteX = PlayerX - currentSprite.PosX
    val SpriteY = PlayerY - currentSprite.PosY
    if (SpriteX <= 300 && SpriteY <= 300 && SpriteX >= -300 && SpriteY >= -300) {
      TotalKeys += 1
      RemoveObject(currentSprite)
    }
  }

  def FireBullet(): Unit = {
    if (ShootingTimer == 0) {
      ShotgunSound.trigger()
      for (i <- ObjectArr) {
        val SpriteX: Double = i.PosX - PlayerX
        val SpriteY: Double = i.PosY - PlayerY
        val TanInverse: Double = Helper.getTanInverse(SpriteX, SpriteY)
        val WallDistance: Float = sqrt(pow(SpriteX, 2) + pow(SpriteY, 2)).toFloat
        val proj = (100 / WallDistance * i.Scale) * 4000
        if (PlayerAngle > TanInverse && PlayerAngle < TanInverse + (proj/SCREEN_SIZE) * FOV && WallDistance < WallDistanceShooting){
          i.Health -= PlayerDamage
          if (i.Health <= 0){
            BlowUp(i)
          }
        }
      }
      ShootingTimer = 1
    }
  }

  def drawGun(): Unit = {
    val GunMiddle = 75
    if (ShootingTimer == 0){
      image(ShootingArr(0), HALF_SCREEN_SIZE - GunMiddle, SCREEN_SIZE - 150)
    }
    else if( ShootingTimer < 3){
      image(ShootingArr(0), HALF_SCREEN_SIZE - GunMiddle, SCREEN_SIZE - 150)
      image(ShootingArr(4), HALF_SCREEN_SIZE - GunMiddle/2 + 15, SCREEN_SIZE - 165)
    }
    else if (ShootingTimer < 6) {
      image(ShootingArr(0), HALF_SCREEN_SIZE - GunMiddle, SCREEN_SIZE - 150)
      image(ShootingArr(5), HALF_SCREEN_SIZE - GunMiddle/2 + 15, SCREEN_SIZE - 173)
    }
    else if (ShootingTimer < 15){
      image(ShootingArr(1), HALF_SCREEN_SIZE - GunMiddle, SCREEN_SIZE - 150)
    }
    else if (ShootingTimer < 21) {
      image(ShootingArr(2), HALF_SCREEN_SIZE - GunMiddle, SCREEN_SIZE - 150)
    }
    else{
      image(ShootingArr(3), HALF_SCREEN_SIZE - GunMiddle, SCREEN_SIZE - 150)
    }
  }

  def DrawHUD() : Unit = {
    fill(0); // Set the fill color to black
    textSize(20);
    text("X", HALF_SCREEN_SIZE - 10, HALF_SCREEN_SIZE - 10);
    textSize(32); // Set the text size

    text("HP: " + PlayerHealth.toInt, 50, SCREEN_SIZE - 100);
    text("Keys: " + TotalKeys +"/11", 50, SCREEN_SIZE - 200);
  }
  def update(): Unit = {
    val BoxCheck : Double = 100

    val DirX = cos(toRadians(PlayerAngle))
    val DirY = sin(toRadians(PlayerAngle))

    var NewX : Double = PlayerX + PlayerSpeed * DirX
    var NewY : Double = PlayerY + PlayerSpeed * DirY

    def isValid(NewX: Double, NewY : Double): Boolean = {

      map(NewX.toInt / BOX_SIZE)((NewY).toInt / BOX_SIZE) == 0 &&
        map((NewX).toInt / BOX_SIZE)((NewY + BoxCheck).toInt / BOX_SIZE) == 0 &&
        map((NewX + BoxCheck).toInt / BOX_SIZE)((NewY).toInt / BOX_SIZE) == 0 &&
        map((NewX - BoxCheck).toInt / BOX_SIZE)((NewY).toInt / BOX_SIZE) == 0 &&
        map((NewX).toInt / BOX_SIZE)((NewY - BoxCheck).toInt / BOX_SIZE) == 0
    }

    if (UP_PRESS && isValid(NewX, NewY))
    {
      PlayerY = NewY; PlayerX = NewX
    }

    NewX = PlayerX - PlayerSpeed * DirX
    NewY = PlayerY - PlayerSpeed * DirY

    if (DOWN_PRESS && isValid(NewX, NewY))
    {
      PlayerY = NewY; PlayerX = NewX
    }

    val NewDirX = cos(toRadians(PlayerAngle - 90))
    val NewDirY = sin(toRadians(PlayerAngle - 90))

    NewX = PlayerX + PlayerSpeed * NewDirX
    NewY = PlayerY + PlayerSpeed * NewDirY

    if (LEFT_PRESS && isValid(NewX, NewY))
    {
      PlayerY = NewY; PlayerX = NewX
    }

    NewX = PlayerX - PlayerSpeed * NewDirX
    NewY = PlayerY - PlayerSpeed * NewDirY

    if (RIGHT_PRESS && isValid(NewX, NewY))
    {
      PlayerY = NewY; PlayerX = NewX
    }

    if (D_PRESS){PlayerAngle += 3}
    if (A_PRESS) {PlayerAngle -= 3}

    PlayerAngle = Helper.NormalizeAngle(PlayerAngle)

    if (ShootingTimer == 35) ShootingTimer = 0
    if (SPACE_PRESS) FireBullet()
    if (ShootingTimer != 0) {ShootingTimer += 1}
    updateSprite()
  }

  def DrawGameOver(): Unit = {
    textSize(32)
    fill(255, 0, 0)
    text("Game Over", width / 2 - 100, height / 2)
  }

  def GameWin(): Unit = {
    textSize(32)
    fill(0, 255, 0)
    text("GG thanks for playing :)", width / 2 - 200, height / 2)
  }

  override def draw(): Unit = {
    background(255)
    if(TotalKeys == 11){
      GameWin()
      return
    }
    if (PlayerHealth <= 0){
      DrawGameOver()
    }
    else {
      setBackground()
      drawRays()
      drawSprite()
      drawWalls()
      drawGun()
      DrawHUD()
    }
    update()

  }

  override def setup(): Unit = {
    TexturesArr = Array(loadImage("src/engine/graphics/Images/Textures/barrel.png"),
      loadImage("src/engine/graphics/Images/Textures/bluestone.png"),
      loadImage("src/engine/graphics/Images/Textures/colorstone.png"),
      loadImage("src/engine/graphics/Images/Textures/eagle.png"),
      loadImage("src/engine/graphics/Images/Textures/greenlight.png"),
      loadImage("src/engine/graphics/Images/Textures/greystone.png"),
      loadImage("src/engine/graphics/Images/Textures/mossy.png"),
      loadImage("src/engine/graphics/Images/Textures/pillar.png"),
      loadImage("src/engine/graphics/Images/Textures/purplestone.png"),
      loadImage("src/engine/graphics/Images/Textures/redbrick.png"),
      loadImage("src/engine/graphics/Images/Textures/wood.png"))

    val HeadDemonIMG = loadImage("src/engine/graphics/Images/HeadDoom.png");
    val CacoDemonIMG = loadImage("src/engine/graphics/Images/CacodemonSmall.png");
    val BrownDemonIMG = loadImage("src/engine/graphics/Images/Afrit.png");
    val HealthPackIMG = loadImage("src/engine/graphics/Images/HealthPack.png");
    val SpeedBoostIMG = loadImage("src/engine/graphics/Images/SpeedPower.png");
    val KeyIMG = loadImage("src/engine/graphics/Images/Key.png");

    // Main Room
    ObjectArr = ObjectArr :+ new Object(1000, HealthPackIMG, 9950, 10110, 0.1f)
    ObjectArr = ObjectArr :+ new Object(1000, HealthPackIMG, 10060, 12280, 0.1f)
    ObjectArr = ObjectArr :+ new Object(69, KeyIMG, 7446, 11562, 0.2f)

    // Maze Room 1
    ObjectArr = ObjectArr :+ new Object( 2,CacoDemonIMG, 2670,12890, 0.4f, 100, 25)
    ObjectArr = ObjectArr :+ new Object( 2,CacoDemonIMG, 2920,18960, 0.4f, 100, 25)
    ObjectArr = ObjectArr :+ new Object( 2,CacoDemonIMG, 4620,12890, 0.4f, 100, 25)
    ObjectArr = ObjectArr :+ new Object( 2,CacoDemonIMG, 4980,18960, 0.4f, 100, 25)
    ObjectArr = ObjectArr :+ new Object( 2,CacoDemonIMG, 5490,15990, 0.4f, 100, 25)
    ObjectArr = ObjectArr :+ new Object( 1000,HealthPackIMG, 2714,17960, 0.1f)
    ObjectArr = ObjectArr :+ new Object( 1000,HealthPackIMG, 2814,14173, 0.1f)
    ObjectArr = ObjectArr :+ new Object( 69,KeyIMG, 5564,16050, 0.2f)
    ObjectArr = ObjectArr :+ new Object( 69,KeyIMG, 2714,16050, 0.2f)

    // Maze Rest
    ObjectArr = ObjectArr :+ new Object(2, CacoDemonIMG, 9940, 19260, 0.4f, 100, 50)
    ObjectArr = ObjectArr :+ new Object(2, CacoDemonIMG, 7460, 18990, 0.4f, 100, 50)
    ObjectArr = ObjectArr :+ new Object( 69,KeyIMG, 9220,19260, 0.2f)
    ObjectArr = ObjectArr :+ new Object( 69,KeyIMG, 8300,18960, 0.2f)
    ObjectArr = ObjectArr :+ new Object( 69,KeyIMG, 8460,19660, 0.2f)
    ObjectArr = ObjectArr :+ new Object( 1000,HealthPackIMG, 7510,16470, 0.1f)
    ObjectArr = ObjectArr :+ new Object( 1000,HealthPackIMG, 7520,18710, 0.1f)

    // Purple Place
    ObjectArr = ObjectArr :+ new Object(69, KeyIMG, 740, 10330, 0.2f)
    ObjectArr = ObjectArr :+ new Object(69, KeyIMG, 2390, 7170, 0.2f)
    ObjectArr = ObjectArr :+ new Object(69, KeyIMG, 7150, 5620, 0.2f)

    ObjectArr = ObjectArr :+ new Object(1001, SpeedBoostIMG, 4970, 9700, 0.2f)

    ObjectArr = ObjectArr :+ new Object(3, BrownDemonIMG, 2665, 3420, 0.4f, 100, 25)
    ObjectArr = ObjectArr :+ new Object(3, BrownDemonIMG, 6260, 4650, 0.4f, 100, 25)
    ObjectArr = ObjectArr :+ new Object(3, BrownDemonIMG, 630, 4700, 0.4f, 100, 25)

    ObjectArr = ObjectArr :+ new Object(1, HeadDemonIMG, 3800, 3670, 0.4f, 100, 50)
    ObjectArr = ObjectArr :+ new Object(1, HeadDemonIMG, 4788, 3900, 0.4f, 100, 50)
    ObjectArr = ObjectArr :+ new Object(1, HeadDemonIMG, 2160, 6880, 0.4f, 100, 50)
    ObjectArr = ObjectArr :+ new Object(1, HeadDemonIMG, 2008, 8960, 0.4f, 100, 50)
    ObjectArr = ObjectArr :+ new Object(1, HeadDemonIMG, 2650, 2570, 0.4f, 100, 50)

    ObjectArr = ObjectArr :+ new Object(2, CacoDemonIMG, 7000, 5800, 0.4f, 100, 25)
    ObjectArr = ObjectArr :+ new Object(2, CacoDemonIMG, 3200, 7640, 0.4f, 100, 25)
    ObjectArr = ObjectArr :+ new Object(2, CacoDemonIMG, 1120, 9610, 0.4f, 100, 25)
    ObjectArr = ObjectArr :+ new Object(2, CacoDemonIMG, 1920, 6400, 0.4f, 100, 25)
    ObjectArr = ObjectArr :+ new Object(2, CacoDemonIMG, 630, 5201, 0.4f, 100, 25)
    ObjectArr = ObjectArr :+ new Object(2, CacoDemonIMG, 630, 4700, 0.4f, 100, 25)

    ObjectArr = ObjectArr :+ new Object(1000, HealthPackIMG, 3680, 5760, 0.1f)
    ObjectArr = ObjectArr :+ new Object(1000, HealthPackIMG, 5550, 4029, 0.1f)

    // Blue Place
    ObjectArr = ObjectArr :+ new Object(69, KeyIMG, 15143, 3400, 0.2f)
    ObjectArr = ObjectArr :+ new Object(69, KeyIMG, 9000, 3400, 0.2f)
    ObjectArr = ObjectArr :+ new Object(69, KeyIMG, 12419, 15279, 0.2f)

    ObjectArr = ObjectArr :+ new Object(1001, SpeedBoostIMG, 15226, 15386, 0.1f)
    ObjectArr = ObjectArr :+ new Object(1000, HealthPackIMG, 15226, 14836, 0.1f)

    ObjectArr = ObjectArr :+ new Object(3, BrownDemonIMG, 12758, 7899, 0.4f, 100, 25)
    ObjectArr = ObjectArr :+ new Object(2, CacoDemonIMG, 12075, 8988, 0.4f, 100, 25)
    ObjectArr = ObjectArr :+ new Object(2, CacoDemonIMG, 12163, 13768, 0.4f, 100, 25)
    ObjectArr = ObjectArr :+ new Object(2, CacoDemonIMG, 12155, 18780, 0.4f, 100, 25)
    ObjectArr = ObjectArr :+ new Object(2, CacoDemonIMG, 11166, 17193, 0.4f, 100, 25)
    ObjectArr = ObjectArr :+ new Object(2, CacoDemonIMG, 14123, 15954, 0.4f, 100, 25)
    ObjectArr = ObjectArr :+ new Object(2, CacoDemonIMG, 14123, 15954, 0.4f, 100, 25)
    ObjectArr = ObjectArr :+ new Object(2, CacoDemonIMG, 10285, 629, 0.4f, 100, 25)
    ObjectArr = ObjectArr :+ new Object(2, CacoDemonIMG, 10285, 1000, 0.4f, 100, 25)
    ObjectArr = ObjectArr :+ new Object(2, CacoDemonIMG, 10285, 1400, 0.4f, 100, 25)
    ObjectArr = ObjectArr :+ new Object(2, CacoDemonIMG, 10285, 1800, 0.4f, 100, 25)
    ObjectArr = ObjectArr :+ new Object(2, CacoDemonIMG, 10285, 2200, 0.4f, 100, 25)
    ObjectArr = ObjectArr :+ new Object(2, CacoDemonIMG, 10285, 2600, 0.4f, 100, 25)
    ObjectArr = ObjectArr :+ new Object(2, CacoDemonIMG, 10285, 3000, 0.4f, 100, 25)
    ObjectArr = ObjectArr :+ new Object(2, CacoDemonIMG, 10285, 3400, 0.4f, 100, 25)
    ObjectArr = ObjectArr :+ new Object(2, CacoDemonIMG, 10285, 3800, 0.4f, 100, 25)
    ObjectArr = ObjectArr :+ new Object(2, CacoDemonIMG, 10285, 4200, 0.4f, 100, 25)
    ObjectArr = ObjectArr :+ new Object(2, CacoDemonIMG, 10285, 4600, 0.4f, 100, 25)
    ObjectArr = ObjectArr :+ new Object(2, CacoDemonIMG, 10285, 5000, 0.4f, 100, 25)




    var orignalIMG = loadImage("src/engine/graphics/Images/DoomWeapons.png");

    var croppedImage = orignalIMG.get(135, 251, 78, 60)
    croppedImage.resize(150, 150)
    ShootingArr =  ShootingArr :+ croppedImage

    croppedImage = orignalIMG.get(218, 190, 118, 121)
    croppedImage.resize(150, 150)
    ShootingArr = ShootingArr :+ croppedImage

    croppedImage = orignalIMG.get(339, 160, 87, 151)
    croppedImage.resize(150, 150)
    ShootingArr = ShootingArr :+ croppedImage

    croppedImage = orignalIMG.get(429, 181, 113, 130)
    croppedImage.resize(150, 150)
    ShootingArr = ShootingArr :+ croppedImage

    croppedImage = orignalIMG.get(235, 157, 44, 30)
    ShootingArr = ShootingArr :+ croppedImage

    croppedImage = orignalIMG.get(282, 143, 54, 44)
    ShootingArr = ShootingArr :+ croppedImage

    orignalIMG = loadImage("src/engine/graphics/Images/Projectiles.png");
    croppedImage = orignalIMG.get(47, 43, 13, 13)
    croppedImage.resize(150, 150)
    Bullet = croppedImage


    croppedImage = orignalIMG.get(2, 861, 72, 57)
    croppedImage.resize(150, 150)
    ExplosionArr = ExplosionArr :+ croppedImage

    croppedImage = orignalIMG.get(76, 849, 87, 69)
    croppedImage.resize(150, 150)
    ExplosionArr = ExplosionArr :+ croppedImage

    croppedImage = orignalIMG.get(165, 835, 102, 83)
    croppedImage.resize(150, 150)
    ExplosionArr = ExplosionArr :+ croppedImage

    frameRate(30)

    minim = new Minim(this)
    val music = minim.loadFile(dataPath("Doom_Soundtrack.mp3"))
    ShotgunSound = minim.loadSample(dataPath("ShotGun.mp3"))
    music.loop()

  }
  override def settings(): Unit = {
    size(SCREEN_SIZE, SCREEN_SIZE)
  }

  override def keyPressed(event: KeyEvent): Unit = {

    event.getKeyCode match {
      case VK_UP => UP_PRESS = true
      case VK_DOWN => DOWN_PRESS = true
      case VK_LEFT => LEFT_PRESS = true
      case VK_RIGHT => RIGHT_PRESS = true
      case VK_D => D_PRESS = true
      case VK_A => A_PRESS = true
      case VK_SPACE => SPACE_PRESS = true
      case _ => ()
    }

  }

  override def keyReleased(event: KeyEvent): Unit = {
    event.getKeyCode match {
      case VK_UP => UP_PRESS =false
      case VK_DOWN => DOWN_PRESS =false
      case VK_LEFT => LEFT_PRESS =false
      case VK_RIGHT => RIGHT_PRESS =false
      case VK_D => D_PRESS = false
      case VK_A => A_PRESS =false
      case VK_SPACE => SPACE_PRESS = false
      case _ => ()
    }
  }
}




object SnakeGame {

  def main(args: Array[String]): Unit = {
    // This is needed for Processing, using the name
    // of the class in a string is not very beautiful...
    PApplet.main("snake.game.SnakeGame")
  }
}
