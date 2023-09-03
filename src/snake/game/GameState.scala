package snake.game
import processing.core.{PApplet, PConstants}
import engine.graphics.{Color, Point}
class GameState extends PApplet{
  val ScreenSize = 800
  var PlayerX: Int = 0;
  var PlayerY: Int = 0
  var SpeedX: Int = 0
  var SpeedY: Int = 0
  var map : Array[Array[Int]] = Array(
    Array(1,1,1,1,1,1,1,1,1,1),
    Array(1,0,1,0,0,0,0,1,0,1),
    Array(1,0,1,0,0,0,0,0,0,1),
    Array(1,0,0,0,0,0,0,1,0,1),
    Array(1,0,0,0,0,0,0,0,0,1),
    Array(1,0,0,1,1,0,0,0,0,1),
    Array(1,1,1,1,1,1,1,1,1,1)
  )

  def render(): Unit = {
    println("test")
    //loadPixels()
    //for (i <- 0 until (width * height)) {
      //pixels(i) = color(0,255,0)
    //}
    //updatePixels()
  }

  def DrawWall(): Unit = {

  }
  def update(): Unit = {

  }

  def addPixel(x: Float, y: Float, color: Int): Unit = {
    // Add a new pixel to the list

  }

  def changeDir(dir: String): Unit = {
    dir match {
      case "North" => SpeedY = 1
      case "South" => SpeedY = -1
      case "West" => SpeedX = 1
      case "East" => SpeedX = -1
      case _ => ()
    }
  }

  def step(): Unit = {
    PlayerX += SpeedX
    PlayerY += SpeedY
  }

}