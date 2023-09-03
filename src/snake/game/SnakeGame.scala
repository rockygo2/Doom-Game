// DO NOT MODIFY FOR BASIC SUBMISSION
// scalastyle:off

package snake.game
import scala.math._

import processing.core._
import java.awt.event
import processing.core.{PApplet, PConstants}
import processing.event.KeyEvent

import java.awt.event.KeyEvent._
import engine.graphics.{Color, Point}
import engine.graphics.Color._
import engine.random.ScalaRandomGen


class SnakeGame extends PApplet{
  val ScreenSize = 800
  var gameState: GameState = _

  var PlayerX = 400
  var PlayerY = 400
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

  // KEYS //
  def Draw2DMap(): Unit = {
    for (i <-  map.indices) {
      for (j <- map.indices) {
        if (map(i)(j) == 1) {
          drawBlock((ScreenSize/map.length)*j, (ScreenSize/map.length)*i)
        }
      }
    }
  }

  def drawBlock(x: Int, y: Int): Unit = {
    fill(255, 208, 160)
    rect(x, y, 80, 80)
  }

  def drawPlayer(x: Int , y: Int): Unit = {
    fill(0, 0, 255)
    rect(x, y, 10, 10)
  }

  def drawRays(): Unit = {
    var x = (cos(toRadians(PlayerAngle)) * 100).toInt
    var y = (sin(toRadians(PlayerAngle)) * 100).toInt

    println(x + " " + y)
    line(PlayerX + 30, PlayerY + 30, PlayerX + 12, PlayerY + 12)
    stroke(255,0,0)
    line(PlayerX + x, PlayerY + y, PlayerX + 5, PlayerY + 5)


  }

  def update(): Unit = {
    if (UPPRESS) PlayerY -= 1
    if (DOWNPRESS) PlayerY += 1
    if (LEFTPRESS) PlayerX -= 1
    if (RIGHTPRESS) PlayerX += 1
    if (DPRESS) PlayerAngle += 5
    if (APRESS) PlayerAngle -= 5
  }
  override def draw(): Unit = {
    background(255)
    Draw2DMap()
    drawPlayer(PlayerX, PlayerY)
    drawRays()
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
      case _ => ()
    }

  }

  override def keyReleased(event: KeyEvent): Unit = {
    event.getKeyCode match {
      case VK_UP => UPPRESS =false
      case VK_DOWN => DOWNPRESS =false
      case VK_LEFT => LEFTPRESS =false
      case VK_RIGHT => RIGHTPRESS =false
      case VK_D => DPRESS =false
      case VK_A => APRESS =false
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
