// DO NOT MODIFY FOR BASIC SUBMISSION
// scalastyle:off

package snake.game
import scala.math._
import processing.core._
import processing.event.KeyEvent
import snake.game.Walls
import snake.game.Object

import java.awt.event.KeyEvent._
import scala.runtime.ObjectRef


class SnakeGame extends PApplet{


  val ScreenSize = 800
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
    Array(9, 0, 0, 0, 0, 9, 0, 0, 0, 0, 9),
    Array(9, 0, 0, 9, 0, 0, 0, 0, 0, 0, 9),
    Array(9, 0, 0, 0, 0, 0, 0, 0, 0, 0, 9),
    Array(9, 0, 0, 0, 0, 9, 0, 0, 0, 0, 9),
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
  val NumRays = 400
  var PlayerX: Double = 500
  var PlayerY: Double = 500
  var PlayerAngle = 0
  val FOV = 100
  var WallArr : Array[Walls] = Array()
  var ObjectArr : Array[Object] = Array()
  var ShootingArr : Array[PImage] = Array()

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
    for (i <- ObjectArr) {
      val SpriteX: Double = i.PosX - PlayerX
      val SpriteY: Double = i.PosY - PlayerY
      var WallDistance: Float = sqrt(pow(SpriteX, 2) + pow(SpriteY, 2)).toFloat
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
      val AngleDif: Double = (arcTan - PlayerAngle + 360) % 360
      val adjustedAngleDif = if (AngleDif > 180) {
        AngleDif - 360
      } else if (AngleDif < -180) {
        AngleDif + 360
      } else {
        AngleDif
      }
      val XSpriteLocation: Double = (FOV / 2 + adjustedAngleDif) * ScreenSize / FOV
      if (WallDistance < 10) WallDistance = 10
      val proj = (100 / WallDistance * i.Scale) * 4000
      val WallHeight = (ScreenSize / WallDistance) * 69

      val YSpriteLocation = ScreenSize / 2 + WallHeight - proj.toFloat

      if (XSpriteLocation > 0 - proj && XSpriteLocation < ScreenSize && (YSpriteLocation > 0 && YSpriteLocation < ScreenSize) ){
        val DrawEnemy: PImage = i.Image.copy()
        DrawEnemy.resize(proj.toInt, proj.toInt);

        val input: Walls = new Walls(DrawEnemy, WallDistance, XSpriteLocation.toFloat, YSpriteLocation)
        WallArr = WallArr :+ input
      }
      return
    }
  }

  def drawWalls(): Unit = {
    val sortedWallArray = WallArr.sortBy(_.WallDistance).reverse
    for (i <- sortedWallArray.indices){
      image(sortedWallArray(i).Image, sortedWallArray(i).PosX, sortedWallArray(i).PosY)
    }
  }
  def drawRays(): Unit = {
    WallArr = Array()
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
        val WallHeight = (ScreenSize/WallDistance) * 69/depth
        val BoxNuM= map(CurrentBoxX)(CurrentBoxY)
        var imageHeightY : Double = 64
        val ImageYLocation = 0

        if (imageHeightY <= 1) imageHeightY = 1
        //rect(i*LineSize, ScreenSize/2 - WallHeight.toFloat,ScreenSize/NumRays, WallHeight.toFloat*2)
        val ImageX = if (!isX) abs((CurrentX - CurrentBoxX) * imgSizeCheck) else abs((CurrentY - CurrentBoxY) * imgSizeCheck)
        val croppedImg = TexturesArr(BoxNuM).get(ImageX.toInt, ImageYLocation, LineSize, imageHeightY.toInt)
        croppedImg.resize(LineSize, WallHeight.toInt*2);

        val input : Walls = new Walls(croppedImg , WallDistance.toFloat, i*LineSize, ScreenSize/2 - WallHeight.toInt)
        WallArr = WallArr :+ input

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

  def updateSprite(): Unit = {
    val Speed : Double = 3.0

  }

  def drawGun(): Unit = {
    image(ShootingArr(0), 500 - 124, ScreenSize - 248)
  }
  def update(): Unit = {
    val Speed: Double = 25
    val BoxCheck : Double = 100

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

    val NewDirX = cos(toRadians(PlayerAngle - 90))
    val NewDirY = sin(toRadians(PlayerAngle - 90))

    NewX = PlayerX + Speed * NewDirX
    NewY = PlayerY + Speed * NewDirY

    if (LEFTPRESS && map (NewX.toInt / BoxSize)((NewY).toInt / BoxSize) == 0 &&
      map((NewX).toInt / BoxSize)((NewY + BoxCheck).toInt / BoxSize) == 0 &&
      map((NewX + BoxCheck).toInt / BoxSize)((NewY).toInt / BoxSize) == 0 &&
      map((NewX - BoxCheck).toInt / BoxSize)((NewY).toInt / BoxSize) == 0 &&
      map((NewX).toInt / BoxSize)((NewY - BoxCheck).toInt / BoxSize) == 0)
    {
      PlayerY = NewY; PlayerX = NewX
    }

    NewX = PlayerX - Speed * NewDirX
    NewY = PlayerY - Speed * NewDirY

    if (RIGHTPRESS && map(NewX.toInt / BoxSize)((NewY).toInt / BoxSize) == 0 &&
      map((NewX).toInt / BoxSize)((NewY + BoxCheck).toInt / BoxSize) == 0 &&
      map((NewX + BoxCheck).toInt / BoxSize)((NewY).toInt / BoxSize) == 0 &&
      map((NewX - BoxCheck).toInt / BoxSize)((NewY).toInt / BoxSize) == 0 &&
      map((NewX).toInt / BoxSize)((NewY - BoxCheck).toInt / BoxSize) == 0)
    {
      PlayerY = NewY; PlayerX = NewX
    }
    if (DPRESS){PlayerAngle += 4}
    if (APRESS) {PlayerAngle -= 4}

    DirX = cos(toRadians(PlayerAngle))
    DirY = sin(toRadians(PlayerAngle))

    updateSprite()
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
      drawWalls()
      drawGun()
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

    var orignalIMG = loadImage("src/engine/graphics/Images/HeadDoom.png");
    //val croppedImage = orignalIMG.get(0, 0, 64, 64);

    ObjectArr = Array(new Object(orignalIMG, 2000,2000, 0.5f))

    orignalIMG = loadImage("src/engine/graphics/Images/Shotgun.png");
    for (i <- 0 until 6) {
      var croppedImage = orignalIMG.get(70 + i * 112, 0, 112, 112)
      croppedImage.resize(248, 248)
      ShootingArr =  ShootingArr :+ croppedImage
    }

    frameRate(30)

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
