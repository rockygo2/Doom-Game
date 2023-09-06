// DO NOT MODIFY FOR BASIC SUBMISSION
// scalastyle:off

package snake.game
import scala.math._
import processing.core._

import java.awt.{Stroke, event}
import processing.core.{PApplet, PConstants}
import processing.event.KeyEvent

import java.awt.event.KeyEvent._
import engine.graphics.{Color, Point}
import engine.graphics.Color._
import engine.random.ScalaRandomGen

import java.util


class SnakeGame extends PApplet{
  val ScreenSize = 1000
  var gameState: GameState = _

  var PlayerX: Double = 400
  var PlayerY: Double = 400
  var PlayerAngle = 0
  var map: Array[Array[Int]] = Array(
    Array(1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1),
    Array(1, 0, 1, 0, 0, 0, 0, 1, 0, 0, 1),
    Array(1, 0, 1, 0, 0, 0, 0, 0, 0, 0, 1),
    Array(1, 0, 0, 0, 0, 0, 0, 1, 0, 0, 1),
    Array(1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1),
    Array(1, 0, 0, 0, 2, 0, 0, 0, 0, 0, 1),
    Array(1, 0, 0, 1, 1, 0, 0, 0, 0, 0, 1),
    Array(1, 0, 0, 1, 1, 0, 0, 0, 0, 0, 1),
    Array(1, 0, 0, 1, 1, 0, 0, 0, 0, 0, 1),
    Array(1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1),
    Array(1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1),
    Array(1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1),
    Array(1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1)
  )


  // KEYS//
  var UPPRESS: Boolean = false
  var DOWNPRESS: Boolean = false
  var LEFTPRESS: Boolean = false
  var RIGHTPRESS: Boolean = false
  var DPRESS: Boolean = false
  var APRESS: Boolean = false
  var VPRESS: Boolean = false
  var DirX: Double = -1
  var DirY: Double = 0
  val BoxSize = 100

  def Draw2DMap(): Unit = {
    for (i <-  map.indices) {
      for (j <- map(0).indices) {
        if (map(i)(j) > 0) {
          drawBlock((ScreenSize/map.length)*i, (ScreenSize/map(0).length)*j)
        }
      }
    }
  }

  def drawBlock(x: Int, y: Int): Unit = {
    val BoxSizeX = ScreenSize/map.length
    val BoxSizeY = ScreenSize/map(0).length
    fill(255, 208, 203)
    rect(x, y, BoxSize, BoxSize)
  }

  def drawPlayer(): Unit = {
    fill(0, 0, 255)
    rect(PlayerX.toInt, PlayerY.toInt, 10, 10)

    stroke(255, 0, 0)
    line(PlayerX.toInt + DirX.toFloat * 50, PlayerY.toFloat + DirY.toFloat * 50, PlayerX.toFloat + 5, PlayerY.toFloat + 5)
  }

  def drawRays(): Unit = {

    val NumRays = 200
    val FOV = 100
    val lineMultiplier = ScreenSize/NumRays
    val RayScale : Double = FOV/NumRays.toDouble
    for (i <- 0 until NumRays){
      val RayAngle = PlayerAngle - FOV/2 + i*RayScale
      var RayDirX : Double = cos(toRadians(RayAngle))
      var RayDirY : Double = sin(toRadians(RayAngle))
      var CurrentBoxX : Int = PlayerX.toInt/BoxSize
      var CurrentBoxY : Int = PlayerY.toInt/BoxSize
      var CurrentX: Double = PlayerX/BoxSize
      var CurrentY: Double = PlayerY/BoxSize
      var isX : Boolean = false

      var hit : Boolean = false

      while(!hit){
        var DistX: Double = if (RayDirX < 0) CurrentBoxX - CurrentX else 1 - (CurrentX - CurrentBoxX)
        var DistY: Double = if (RayDirY < 0) CurrentBoxY - CurrentY else 1 - (CurrentY - CurrentBoxY)

        if (RayDirX == 0) RayDirX = 0.00000000000000001
        if (RayDirX == 0) RayDirY = 0.00000000000000001
        var DistTimeTakenX = DistX / RayDirX
        var DistTimeTakenY = DistY / RayDirY

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
      if (VPRESS) {
        line(CurrentX.toFloat * BoxSize, CurrentY.toFloat * BoxSize, PlayerX.toFloat + 5, PlayerY.toFloat + 5)
      }
      else{

        //Fix FishEyeEffect
        val depth: Double = math.cos(toRadians(PlayerAngle - RayAngle))

        val DistX : Double = PlayerX - CurrentX*BoxSize
        val DistY : Double = PlayerY - CurrentY*BoxSize
        val WallDistance : Double = sqrt(pow(DistX, 2) + pow(DistY, 2))
        val WallHeight = (ScreenSize/WallDistance) * 69/depth
        val  BoxNuM= map(CurrentBoxX)(CurrentBoxY)

        val ColorDivide = if (isX) 2 else 1

        BoxNuM match {
          case 1 => fill(255/ColorDivide, 0, 0); stroke(255/ColorDivide, 0, 0)
          case 2 => fill(255/ColorDivide, 208/ColorDivide, 203/ColorDivide); stroke(255/ColorDivide, 208/ColorDivide, 203/ColorDivide)
        }


        //line(i*lineMultiplier,ScreenSize/2 - WallHeight.toFloat,i*lineMultiplier,ScreenSize/2 + WallHeight.toFloat)
        rect(i*lineMultiplier, ScreenSize/2 - WallHeight.toFloat,ScreenSize/NumRays, WallHeight.toFloat*2)
      }
    }
  }

  def setBackground(): Unit = {
    fill(200, 200, 200)
    rect(0, ScreenSize / 2, ScreenSize, ScreenSize / 2)
    fill(100, 100, 100)
    rect(0, 0, ScreenSize, ScreenSize / 2)

    fill(0, 0, 0)
  }


  def update(): Unit = {

    val Speed: Double = 5

    if (UPPRESS && !(map((PlayerX + Speed * DirX).toInt/BoxSize)((PlayerY + Speed * DirY).toInt/BoxSize) > 0)) {PlayerY += Speed * DirY; PlayerX += Speed * DirX}
    if (DOWNPRESS && !(map((PlayerX - Speed * DirX).toInt/BoxSize)((PlayerY - Speed * DirY).toInt/BoxSize) > 0)) {PlayerY -= Speed * DirY; PlayerX -= Speed * DirX}
    if (LEFTPRESS) PlayerX -= 1
    if (RIGHTPRESS) PlayerX += 1
    if (DPRESS){PlayerAngle += 2}
    if (APRESS) {PlayerAngle -= 2}
    if (map(PlayerX.toInt/BoxSize)(PlayerY.toInt/BoxSize) > 0) {
      if (LEFTPRESS) PlayerX += 1
      if (RIGHTPRESS) PlayerX -= 1
    }
    DirX = cos(toRadians(PlayerAngle))
    DirY = sin(toRadians(PlayerAngle))
  }
  override def draw(): Unit = {
    background(255)
    if (VPRESS){
      Draw2DMap()
      drawPlayer()
      drawRays()
    }
    else {
      setBackground()
      drawRays()
    }
    //Draw2DMap()
    //drawPlayer(PlayerX, PlayerY)
    //drawRays()
    update()
    //gameState.render()

  }

  override def setup(): Unit = {
    gameState = new GameState()
  }

  override def settings(): Unit = {
    size(ScreenSize, ScreenSize)
  }

  override def keyPressed(event: KeyEvent): Unit = {

    event.getKeyCode match {
      case VK_UP => UPPRESS = true
      case VK_DOWN => DOWNPRESS = true
      case VK_LEFT => LEFTPRESS = true
      case VK_RIGHT => RIGHTPRESS = true
      case VK_D => DPRESS = true
      case VK_A => APRESS = true
      case VK_V => VPRESS = true
      case _ => ()
    }

  }

  override def keyReleased(event: KeyEvent): Unit = {
    event.getKeyCode match {
      case VK_UP => UPPRESS =false
      case VK_DOWN => DOWNPRESS =false
      case VK_LEFT => LEFTPRESS =false
      case VK_RIGHT => RIGHTPRESS =false
      case VK_D => DPRESS = false
      case VK_A => APRESS =false
      case VK_V => VPRESS = false
      case _ => ()
    }
  }
}

case class Pixel(x: Float, y: Float, color: Int)



object SnakeGame {

  def main(args: Array[String]): Unit = {
    // This is needed for Processing, using the name
    // of the class in a string is not very beautiful...
    PApplet.main("snake.game.SnakeGame")
  }
}
