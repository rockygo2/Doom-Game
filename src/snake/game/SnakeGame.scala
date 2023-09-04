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
    Array(1, 0, 0, 1, 1, 0, 0, 0, 0, 1),
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
    var Done : Boolean = false
    val Rays : Int = 800
    for (i <- 0 until Rays){
      var cameraX: Double = (2*i) / (Rays-1).toDouble
      var rayDirX: Double = DirX + PlaneX*cameraX
      var rayDirY: Double = DirY + PlaneY*cameraX
      println(rayDirX + " " + rayDirY)

      var MapX: Int = PlayerX.toInt/BoxSize
      var MapY: Int = PlayerY.toInt/BoxSize

      var sideDistX : Double = 0
      var sideDistY : Double = 0

      var deltaDistX : Double = sqrt(1 + (rayDirY * rayDirY) / (rayDirX * rayDirX))
      var deltaDistY : Double = sqrt(1 + (rayDirX * rayDirX) / (rayDirY * rayDirY))
      var perpWallDist : Double = 0

      var StepX : Int = 0
      var StepY : Int = 0

      var hit : Int = 0
      var side : Int = 0

      if(rayDirX < 0){
        StepX = -1
        sideDistX = (MapX - PlayerX.toDouble/80) * deltaDistX
      }
      else{
        StepX = 1;
        sideDistX = (MapX + 1.0 - PlayerX.toDouble/80) * deltaDistX;
      }
      if (rayDirY < 0) {
        StepY = -1;
        sideDistY = (PlayerY.toDouble/80 - MapY) * deltaDistY;
      }
      else {
        StepY = 1;
        sideDistY = (MapY + 1.0 - PlayerY.toDouble/80) * deltaDistY;
      }
      while (hit == 0) {
        //jump to next map square, either in x-direction, or in y-direction
        if (sideDistX < sideDistY) {
          sideDistX += deltaDistX;
          MapX += StepX;
          side = 0;
        }
        else {
          sideDistY += deltaDistY;
          MapY += StepY;
          side = 1;
        }
        //Check if ray has hit a wall
        if (map(MapX)(MapY) > 0) hit = 1;
      }

      if (side == 0) perpWallDist = sideDistX - deltaDistX
      else perpWallDist = sideDistY - deltaDistY
      //Calculate height of line to draw on screen
      var lineHeight : Int = ((ScreenSize / perpWallDist).toInt);

      var pitch : Int = 1

      var drawStart :Int = -lineHeight / 2 + ScreenSize / 2 + pitch
      var drawEnd : Int = lineHeight / 2 + ScreenSize / 2 + pitch
      if(drawStart < 0) drawStart = 0
      if(drawEnd >= ScreenSize) drawEnd = ScreenSize - 1
      if (side == 1) stroke(255, 0, 0)
      else stroke(0,0,255)

      if (VPRESS) line(PlayerX.toInt + rayDirX.toFloat * 50, PlayerY.toFloat + rayDirY.toFloat * 50, PlayerX.toFloat + 5, PlayerY.toFloat + 5)
      else line(i, drawStart,i, drawEnd);
    }

  }



  def update(): Unit = {

    var Speed: Double = 0.1
    var cameraX: Double = (2*200) / (600).toDouble
    var MoveModX: Double = DirX + PlaneX*cameraX
    var MoveModY: Double = DirY + PlaneY*cameraX
    if (UPPRESS) {PlayerY += 5 * MoveModY; PlayerX += 5 * MoveModX}
    if (DOWNPRESS) {PlayerY -= 5 * MoveModY; PlayerX -= 5 * MoveModX}
    if (LEFTPRESS) PlayerX -= 1
    if (RIGHTPRESS) PlayerX += 1
    if (DPRESS){
      val oldDirX = DirX
      DirX = DirX * cos(-Speed) - DirY * sin(-Speed)
      DirY = oldDirX * sin(-Speed) + DirY * cos(-Speed)
      val oldPlaneX = PlaneX
      PlaneX = PlaneX * cos(-Speed) - PlaneY * sin(-Speed)
      PlaneY = oldPlaneX * sin(-Speed) + PlaneY * cos(-Speed)

    }
    if (APRESS) {
      val oldDirX = DirX
      DirX = DirX * cos(Speed) - DirY * sin(Speed)
      DirY = oldDirX * sin(Speed) + DirY * cos(Speed)
      val oldPlaneX = PlaneX
      PlaneX = PlaneX * cos(Speed) - PlaneY * sin(Speed)
      PlaneY = oldPlaneX * sin(Speed) + PlaneY * cos(Speed)

    }
    if (map(PlayerX.toInt/BoxSize)(PlayerY.toInt/BoxSize) > 0) {
      if (UPPRESS) {PlayerY -= 5 * DirY; PlayerX -= 5 * DirX}
      if (DOWNPRESS) {PlayerY += 5 * DirY; PlayerX += 5 * DirX}
      if (LEFTPRESS) PlayerX += 1
      if (RIGHTPRESS) PlayerX -= 1
    }

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
