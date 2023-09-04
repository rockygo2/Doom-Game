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
  val ScreenSize = 800
  var gameState: GameState = _

  var PlayerX: Double = 400
  var PlayerY: Double = 400
  var PlayerAngle = 0
  var map: Array[Array[Int]] = Array(
    Array(1, 1, 1, 1, 1, 1, 1, 1, 1, 1),
    Array(1, 0, 1, 0, 0, 0, 0, 1, 0, 1),
    Array(1, 0, 1, 0, 0, 0, 0, 0, 0, 1),
    Array(1, 0, 0, 0, 0, 0, 0, 1, 0, 1),
    Array(1, 0, 0, 0, 0, 0, 0, 0, 0, 1),
    Array(1, 0, 0, 0, 0, 0, 0, 0, 0, 1),
    Array(1, 0, 0, 1, 1, 0, 0, 0, 0, 1),
    Array(1, 0, 0, 1, 1, 0, 0, 0, 0, 1),
    Array(1, 0, 0, 1, 1, 0, 0, 0, 0, 1),
    Array(1, 1, 1, 1, 1, 1, 1, 1, 1, 1)
  )


  // KEYS//
  var UPPRESS: Boolean = false
  var DOWNPRESS: Boolean = false
  var LEFTPRESS: Boolean = false
  var RIGHTPRESS: Boolean = false
  var DPRESS: Boolean = false
  var APRESS: Boolean = false
  var VPRESS: Boolean = false
  val BoxSize = 80
  var DirX: Double = -1
  var DirY: Double = 0
  var PlaneX : Double = 0
  var PlaneY : Double = 0.66

  // KEYS //
  def Draw2DMap(): Unit = {
    for (i <-  map.indices) {
      for (j <- map.indices) {
        if (map(i)(j) == 1) {
          drawBlock((ScreenSize/map.length)*i, (ScreenSize/map.length)*j)
        }
      }
    }
  }

  def drawBlock(x: Int, y: Int): Unit = {
    fill(255, 208, 203)
    rect(x, y, 80, 80)
  }

  def drawPlayer(): Unit = {
    fill(0, 0, 255)
    rect(PlayerX.toInt, PlayerY.toInt, 10, 10)

    stroke(255, 0, 0)
    line(PlayerX.toInt + DirX.toFloat * 50, PlayerY.toFloat + DirY.toFloat * 50, PlayerX.toFloat + 5, PlayerY.toFloat + 5)
  }

  def drawRays(): Unit = {

    var NumRays = 100
    var FOV = 0
    var RayAngle = PlayerAngle - FOV/2
    val BoxSize = 80
    for (i <- 0 until NumRays){
      var RayDirX : Double = cos(toRadians(RayAngle))
      var RayDirY : Double = sin(toRadians(RayAngle))
      var CurrentBoxX : Int = PlayerX.toInt/BoxSize
      var CurrentBoxY : Int = PlayerY.toInt/BoxSize
      var CurrentX: Double = PlayerX/BoxSize
      var CurrentY: Double = PlayerY/BoxSize


      var DistX: Double = if (RayDirX < 0) -CurrentBoxX + CurrentX else CurrentBoxX - CurrentX
      var DistY: Double = if (RayDirY < 0) -CurrentBoxY + CurrentY else CurrentBoxY - CurrentY

      println(DistX + " " + DistY)

      var DistTimeTakenX = DistX/RayDirX
      var DistTimeTakenY = DistY/RayDirY

      if (DistTimeTakenX < DistTimeTakenY){
        CurrentX += DistTimeTakenX
      }
      else{
        CurrentY += DistTimeTakenY
      }


    }
  }



  def update(): Unit = {

    val Speed: Double = 1


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
