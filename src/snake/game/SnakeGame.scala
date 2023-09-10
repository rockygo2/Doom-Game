// DO NOT MODIFY FOR BASIC SUBMISSION
// scalastyle:off

package snake.game
import scala.math._
import processing.core._
import processing.event.KeyEvent

import java.awt.event.KeyEvent._


class SnakeGame extends PApplet{


  val ScreenSize = 1000
  var gameState: GameState = _

  var map: Array[Array[Int]] = Array(
    Array(9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9),
    Array(9, 0, 0, 0, 0, 0, 0, 0, 0, 0, 9),
    Array(9, 0, 0, 0, 0, 0, 0, 0, 0, 0, 9),
    Array(9, 0, 0, 0, 0, 0, 0, 9, 0, 0, 9),
    Array(9, 0, 0, 0, 0, 0, 0, 0, 0, 0, 9),
    Array(9, 0, 0, 0, 0, 0, 0, 0, 0, 0, 9),
    Array(9, 0, 0, 0, 0, 0, 0, 0, 0, 0, 9),
    Array(9, 0, 0, 0, 0, 0, 0, 0, 0, 0, 9),
    Array(9, 0, 0, 0, 0, 0, 0, 0, 0, 0, 9),
    Array(9, 0, 0, 0, 0, 0, 0, 0, 0, 0, 9),
    Array(9, 0, 0, 0, 0, 0, 0, 0, 0, 0, 9),
    Array(9, 0, 0, 0, 0, 0, 0, 0, 0, 0, 9),
    Array(9, 0, 0, 0, 0, 0, 0, 0, 0, 0, 9),
    Array(9, 0, 0, 0, 0, 0, 0, 0, 0, 0, 9),
    Array(9, 0, 0, 0, 0, 0, 0, 0, 0, 0, 9),
    Array(9, 0, 0, 0, 0, 0, 0, 0, 0, 0, 9),
    Array(9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9)
  )

  var TexturesArr : Array[PImage] = _
  var CroppedTexturesArr : Array[Array[PImage]] = _
  var Enemy: PImage = _
  // KEYS/
  var UPPRESS: Boolean = false
  var DOWNPRESS: Boolean = false
  var LEFTPRESS: Boolean = false
  var RIGHTPRESS: Boolean = false
  var DPRESS: Boolean = false
  var APRESS: Boolean = false
  var VPRESS: Boolean = false
  var DirX: Double = -1
  var DirY: Double = 0
  val BoxSize = 320
  val NumRays = 500
  var PlayerX: Double = 500
  var PlayerY: Double = 500
  var PlayerAngle = 0
  var EnemyX = 1800
  var EnemyY = 1800
  val FOV = 100
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
    val BoxSizeX = ScreenSize/map(0).size
    val BoxSizeY = ScreenSize/map.size
    fill(255, 208, 203)
    rect(x, y, BoxSizeX, BoxSizeY)
  }

  def drawPlayer(): Unit = {
    fill(0, 0, 255)
    rect(PlayerX.toInt, PlayerY.toInt, 10, 10)

    stroke(255, 0, 0)
    line(PlayerX.toInt + DirX.toFloat * 50, PlayerY.toFloat + DirY.toFloat * 50, PlayerX.toFloat + 5, PlayerY.toFloat + 5)
  }

  def drawSprite(): Unit = {
    val SpriteX : Double = EnemyX - PlayerX
    val SpriteY : Double = EnemyY - PlayerY
    val WallDistance : Float = sqrt(pow(SpriteX, 2) + pow(SpriteY, 2)).toFloat
    val arcTan: Double = if (SpriteX > 0) {
      math.toDegrees(math.atan(SpriteY / SpriteX))
    } else if (SpriteX < 0) {
      math.toDegrees(math.atan(SpriteY / SpriteX)) + 180.0
    } else {
      if (SpriteY >= 0) {
        90.0
      } else {
        -90.0
      }
    }
    val AngleDif : Double = (arcTan - PlayerAngle + 360) % 360
    val adjustedAngleDif = if (AngleDif > 180) {
      AngleDif - 360
    } else if (AngleDif < -180) {
      AngleDif + 360
    } else {
      AngleDif
    }
    val XSpriteLocation : Double = (FOV/2 + adjustedAngleDif)*ScreenSize/FOV
    val proj = (100 / WallDistance * 0.7)
    val WallHeight = (ScreenSize/WallDistance) * 69

    val DrawEnemy: PImage  = Enemy.copy()
    DrawEnemy.resize((2000 * proj).toInt, (2000 * proj).toInt);

    println(SpriteX, SpriteY)
    image(DrawEnemy, XSpriteLocation.toFloat, ScreenSize/2 + WallHeight )

  }
  def drawRays(): Unit = {
    val LineSize : Int = ScreenSize/NumRays
    val RayScale : Double = FOV/NumRays.toDouble
    val imgSizeCheck = 64 - LineSize
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
      if (VPRESS) {
        line(CurrentX.toFloat * BoxSize, CurrentY.toFloat * BoxSize, PlayerX.toFloat + 5, PlayerY.toFloat + 5)
      }
      else{

        //Fix FishEyeEffect
        val depth: Double = math.cos(toRadians(PlayerAngle - RayAngle))

        val DistX : Double = PlayerX - CurrentX*BoxSize
        val DistY : Double = PlayerY - CurrentY*BoxSize
        val WallDistance : Double = sqrt(pow(DistX, 2) + pow(DistY, 2))
        var WallHeight = (ScreenSize/WallDistance) * 69/depth
        val BoxNuM= map(CurrentBoxX)(CurrentBoxY)
        var imageHeightY : Double = 64
        var ImageYLocation = 0
        var WallHeightChange : Double = 0

        /*if (WallHeight > ScreenSize/2) {
          imageHeightY = (ScreenSize/ (2* WallHeight)) * 64
          ImageYLocation =  32 - imageHeightY.toInt/2
          WallHeightChange =  imageHeightY - imageHeightY.toInt
          println(WallHeightChange)
        }
         */

        if (imageHeightY <= 1) imageHeightY = 1
        //rect(i*LineSize, ScreenSize/2 - WallHeight.toFloat,ScreenSize/NumRays, WallHeight.toFloat*2)
        val ImageX = if (!isX) abs((CurrentX - CurrentBoxX) * imgSizeCheck) else abs((CurrentY - CurrentBoxY) * imgSizeCheck)
        val croppedImg = TexturesArr(BoxNuM).get(ImageX.toInt, ImageYLocation, LineSize, imageHeightY.toInt)
        croppedImg.resize(LineSize, WallHeight.toInt*2);

        image(croppedImg, i*LineSize, ScreenSize/2 - WallHeight.toInt)
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

    val Speed: Double = 15
    val BoxCheck : Double = 75

    DirX = cos(toRadians(PlayerAngle))
    DirY = sin(toRadians(PlayerAngle))

    var NewX : Double = PlayerX + Speed * DirX
    var NewY : Double = PlayerY + Speed * DirY

    if (UPPRESS && map(NewX.toInt / BoxSize)((NewY).toInt / BoxSize) == 0 &&
      map((NewX).toInt / BoxSize)((NewY + BoxCheck).toInt / BoxSize) == 0 &&
      map((NewX + BoxCheck).toInt / BoxSize)((NewY).toInt / BoxSize) == 0 &&
      map((NewX - BoxCheck).toInt / BoxSize)((NewY).toInt / BoxSize) == 0 &&
      map((NewX).toInt / BoxSize)((NewY - BoxCheck).toInt / BoxSize) == 0)
      {
        PlayerY = NewY; PlayerX = NewX
      }

    NewX = PlayerX - Speed * DirX
    NewY = PlayerY - Speed * DirY

    if (DOWNPRESS && map(NewX.toInt / BoxSize)((NewY).toInt / BoxSize) == 0 &&
      map((NewX).toInt / BoxSize)((NewY + BoxCheck).toInt / BoxSize) == 0 &&
      map((NewX + BoxCheck).toInt / BoxSize)((NewY).toInt / BoxSize) == 0 &&
      map((NewX - BoxCheck).toInt / BoxSize)((NewY).toInt / BoxSize) == 0 &&
      map((NewX).toInt / BoxSize)((NewY - BoxCheck).toInt / BoxSize) == 0)
    {
      PlayerY = NewY; PlayerX = NewX
    }
    if (LEFTPRESS) PlayerX -= 1
    if (RIGHTPRESS) PlayerX += 1
    if (DPRESS){PlayerAngle += 2}
    if (APRESS) {PlayerAngle -= 2}

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
      drawSprite()
    }
    update()

  }

  override def setup(): Unit = {
    gameState = new GameState()
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

    Enemy = loadImage("src/engine/graphics/Images/Textures/Box2.png")
      frameRate(60)
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
