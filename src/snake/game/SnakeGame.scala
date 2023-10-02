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

  //music.loop()
  val ScreenSize = 800
  val HALFSCREENSIZE = ScreenSize/2
  var gameState: GameState = _

  var map: Array[Array[Int]] = Array(
    Array(9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 8, 8, 8, 8, 8, 8, 8),
    Array(9, 0, 0, 0, 0, 0, 0, 0, 0, 0, 9, 8, 8, 0, 0, 0, 0, 8),
    Array(9, 0, 0, 0, 0, 0, 0, 0, 0, 0, 9, 8, 8, 0, 0, 0, 0, 8),
    Array(9, 0, 0, 0, 0, 0, 0, 0, 0, 0, 9, 8, 8, 0, 0, 0, 0, 8),
    Array(9, 0, 0, 0, 0, 0, 0, 0, 0, 0, 9, 8, 8, 0, 0, 0, 0, 8),
    Array(9, 0, 0, 0, 0, 0, 0, 0, 0, 0, 9, 8, 8, 0, 0, 0, 0, 8),
    Array(9, 0, 0, 0, 0, 0, 0, 0, 0, 0, 9, 8, 8, 0, 0, 0, 0, 8),
    Array(9, 0, 0, 0, 0, 0, 0, 0, 0, 0, 8, 8, 0, 0, 0, 0, 0, 8),
    Array(9, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 8),
    Array(9, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 8),
    Array(9, 0, 0, 0, 0, 0, 0, 0, 0, 0, 8, 8, 0, 0, 0, 0, 0, 8),
    Array(9, 0, 0, 0, 0, 0, 0, 0, 0, 0, 9, 8, 0, 0, 0, 0, 0, 8),
    Array(9, 0, 0, 0, 0, 0, 0, 0, 0, 0, 9, 8, 0, 0, 0, 0, 0, 8),
    Array(9, 0, 0, 0, 0, 0, 0, 0, 0, 0, 9, 8, 0, 0, 0, 0, 0, 8),
    Array(9, 0, 0, 0, 0, 0, 0, 0, 0, 0, 9, 8, 0, 0, 0, 0, 0, 8),
    Array(9, 0, 0, 0, 0, 0, 0, 0, 0, 0, 9, 8, 0, 0, 0, 0, 0, 8),
    Array(9, 9, 9, 9, 9, 9, 9, 1, 0, 0, 1, 8, 8, 8, 8, 8, 8, 8),
    Array(9, 9, 9, 9, 9, 9, 9, 1, 0, 0, 1, 8, 8, 8, 8, 8, 8, 8),
    Array(1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1),
    Array(1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1),
    Array(1, 0, 1, 1, 1, 1, 1, 1, 0, 0, 1, 1, 1, 1, 0, 1, 1, 1),
    Array(1, 0, 1, 1, 1, 0, 0, 1, 0, 0, 1, 1, 1, 1, 0, 1, 1, 1),
    Array(1, 0, 0, 0, 0, 0, 1, 1, 0, 0, 1, 1, 0, 0, 0, 1, 1, 1),
    Array(1, 0, 1, 0, 1, 1, 1, 1, 0, 0, 1, 1, 0, 1, 1, 1, 1, 1),
    Array(1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1),
  )

  var TexturesArr : Array[PImage] = _
  // KEYS/
  var UPPRESS: Boolean = false
  var DOWNPRESS: Boolean = false
  var LEFTPRESS: Boolean = false
  var RIGHTPRESS: Boolean = false
  var DPRESS: Boolean = false
  var APRESS: Boolean = false
  var VPRESS: Boolean = false
  var SPACEPRESS: Boolean = false
  var DirX: Double = -1
  var DirY: Double = 0
  val BoxSize = 320
  val NumRays = 400
  var PlayerX: Double = 500
  var PlayerY: Double = 500
  var PlayerAngle = 0
  var ShootingTimer = 0
  val FOV = 100
  var WallArr : Array[Walls] = Array()
  var ObjectArr : Array[Object] = Array()
  var ShootingArr : Array[PImage] = Array()
  var ExplosionArr : Array[PImage] = Array()
  var Bullet : PImage = _
  var PlayerHealth : Double = 100
  var WallDistanceShooting : Double = 0;
  var minim : Minim = _
  var ShotgunSound : AudioSample = _

  def canSeePlayer(PosX : Double, PosY : Double): Boolean = {

    // bad function :(
    val PosX2 = PosX - 60
    val PosY2 = PosY - 60
    val SpriteX = PlayerX - PosX2
    val SpriteY = PlayerY - PosY

    val TanInverse: Double = normalizeAngle(math.toDegrees(math.atan2(SpriteY, SpriteX)).toInt)
    val CurrentDirX: Float = cos(toRadians(TanInverse)).toFloat
    val CurrentDirY: Float = sin(toRadians(TanInverse)).toFloat

    var RayDirX: Double = CurrentDirX
    var RayDirY: Double = CurrentDirY
    var CurrentBoxX: Int = PosX2.toInt / BoxSize
    var CurrentBoxY: Int = PosY2.toInt / BoxSize

    val startX = PosX2 / BoxSize
    val startY = PosY2 / BoxSize

    var CurrentX: Double = PosX / BoxSize
    var CurrentY: Double = PosY2 / BoxSize
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
        if(abs(startX - CurrentX) > abs(PlayerX/BoxSize - PosX2/BoxSize) && abs(startY - CurrentY) > abs(PlayerY/BoxSize - PosY2/BoxSize)){
          return true
        }
        else{
          return false
        }
      }
    }
    false
  }

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
    val BoxSizeX = ScreenSize/map(0).length
    val BoxSizeY = ScreenSize/map.length
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
      val WallDistance: Float = sqrt(pow(SpriteX, 2) + pow(SpriteY, 2)).toFloat
      val arcTan: Double = math.toDegrees(math.atan2(SpriteY, SpriteX))
      val AngleDif: Double = ((arcTan - PlayerAngle + 540) % 360) - 180

      val XSpriteLocation: Double = (FOV / 2 + AngleDif) * ScreenSize / FOV
      val proj = (100 / WallDistance * i.Scale) * 4000
      val WallHeight = (ScreenSize / WallDistance) * 69

      val YSpriteLocation = ScreenSize / 2 + WallHeight - proj


      if (XSpriteLocation > 0 - proj && XSpriteLocation < ScreenSize && (YSpriteLocation > 0 && YSpriteLocation < ScreenSize) ){
        val DrawEnemy: PImage = i.Image.copy()
        DrawEnemy.resize(proj.toInt, proj.toInt);

        val input: Walls = new Walls(DrawEnemy, WallDistance, XSpriteLocation.toFloat, YSpriteLocation)
        WallArr = WallArr :+ input
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
    else ObjectArr = ObjectArr.filterNot(obj => obj == currentSprite)
    currentSprite.Timer += 1
  }
  def drawRays(): Unit = {
    WallArr = Array()

    val LineSize : Int = ScreenSize/NumRays
    val RayScale : Double = FOV/NumRays.toDouble
    val imgSizeCheck = 64 - LineSize
    val halfRays = NumRays/2
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
        if(i == halfRays){
          WallDistanceShooting = WallDistance
        }
        val WallHeight = (ScreenSize/WallDistance) * 69/depth
        val BoxNuM= map(CurrentBoxX)(CurrentBoxY)
        var imageHeightY : Double = 64
        val ImageYLocation = 0

        if (imageHeightY <= 1) imageHeightY = 1
        val ImageX = if (!isX) abs((CurrentX - CurrentBoxX) * imgSizeCheck) else abs((CurrentY - CurrentBoxY) * imgSizeCheck)
        val croppedImg = TexturesArr(BoxNuM).get(ImageX.toInt, ImageYLocation, LineSize, imageHeightY.toInt)
        croppedImg.resize(LineSize, WallHeight.toInt*2);

        val input : Walls = new Walls(croppedImg , WallDistance.toFloat, i*LineSize, HALFSCREENSIZE - WallHeight.toInt)
        WallArr = WallArr :+ input

      }
    }
  }

  def setBackground(): Unit = {
    fill(200, 200, 200)
    rect(0, HALFSCREENSIZE, ScreenSize, HALFSCREENSIZE)
    fill(100, 100, 100)
    rect(0, 0, ScreenSize, HALFSCREENSIZE)

    fill(0, 0, 0)
  }

  def updateSprite(): Unit = {
    for (i <- ObjectArr){
      i.objType match {
        case 1 => Sprite1(i)
        case 2 => Sprite2(i)
        case 100 => Sprite100(i)
        case _ => {

        }
      }
    }
  }

  def Sprite1(currentSprite : Object): Unit = {
    val Speed : Float = 10f
    if (!canSeePlayer(currentSprite.PosX, currentSprite.PosY)){
      return
    }
    if (currentSprite.Timer == 0) {
      val SpriteX = PlayerX - currentSprite.PosX
      val SpriteY = PlayerY - currentSprite.PosY
      if (SpriteX <= 300 && SpriteY <= 300 && SpriteX >= -300 && SpriteY >= -300) {
        BlowUp(currentSprite)
        PlayerHealth -= 50
      }
      val TanInverse: Double = normalizeAngle(math.toDegrees(math.atan2(SpriteY, SpriteX)).toInt)
      val CurrentDirX: Float = cos(toRadians(TanInverse)).toFloat
      val CurrentDirY: Float = sin(toRadians(TanInverse)).toFloat

      val checkSpriteBoundsX: Float = currentSprite.PosX + Speed * CurrentDirX
      val checkSpriteBoundsY: Float = currentSprite.PosY + Speed * CurrentDirY
      if (map(checkSpriteBoundsX.toInt / BoxSize)(checkSpriteBoundsY.toInt / BoxSize) == 0) {
        currentSprite.PosX = checkSpriteBoundsX
        currentSprite.PosY = checkSpriteBoundsY
      }
    }
    else {
      BlowUp(currentSprite)
    }
  }
  def Sprite2(currentSprite : Object): Unit = {
    if (currentSprite.Timer == 0) {
      if (currentSprite.BulletTimer == 0) {
        val newBullet = new Object( 100, Bullet, currentSprite.PosX, currentSprite.PosY, 0.3f, 100)
        val SpriteX = PlayerX - currentSprite.PosX
        val SpriteY = PlayerY - currentSprite.PosY

        val TanInverse: Double = normalizeAngle(math.toDegrees(math.atan2(SpriteY, SpriteX)).toInt)
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
    val Speed = 90
    val checkSpriteBoundsX: Float = currentSprite.PosX + Speed * currentSprite.BulletX
    val checkSpriteBoundsY: Float = currentSprite.PosY + Speed * currentSprite.BulletY

    val SpriteX = PlayerX - currentSprite.PosX
    val SpriteY = PlayerY - currentSprite.PosY

    if (SpriteX <= 300 && SpriteY <= 300 && SpriteX  >= -300 && SpriteY >= -300) {
      ObjectArr = ObjectArr.filterNot(obj => obj == currentSprite)
      PlayerHealth -= 25
      return
    }
    if (map(checkSpriteBoundsX.toInt / BoxSize)(checkSpriteBoundsY.toInt / BoxSize) == 0) {
      currentSprite.PosX = checkSpriteBoundsX
      currentSprite.PosY = checkSpriteBoundsY
    }
    else ObjectArr = ObjectArr.filterNot(obj => obj == currentSprite)
  }

  def normalizeAngle(angle: Int): Int = {
    var normalizedAngle = angle % 360
    if (normalizedAngle < 0) {
      normalizedAngle += 360
    }
    normalizedAngle
  }
  def FireBullet(): Unit = {
    if (ShootingTimer == 0) {
      ShotgunSound.trigger()
      for (i <- ObjectArr) {
        val SpriteX: Double = i.PosX - PlayerX
        val SpriteY: Double = i.PosY - PlayerY
        val TanInverse: Double = normalizeAngle(math.toDegrees(math.atan2(SpriteY, SpriteX)).toInt)
        val PlayerShotDir : Double = PlayerAngle
        val WallDistance: Float = sqrt(pow(SpriteX, 2) + pow(SpriteY, 2)).toFloat
        val proj = (100 / WallDistance * i.Scale) * 4000
        if (PlayerShotDir > TanInverse && PlayerShotDir < TanInverse + (proj/ScreenSize) * FOV && WallDistance < WallDistanceShooting){
          i.Health -= 50
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
      image(ShootingArr(0), HALFSCREENSIZE - GunMiddle, ScreenSize - 150)
    }
    else if( ShootingTimer < 3){
      image(ShootingArr(0), HALFSCREENSIZE - GunMiddle, ScreenSize - 150)
      image(ShootingArr(4), HALFSCREENSIZE - GunMiddle/2 + 15, ScreenSize - 165)
    }
    else if (ShootingTimer < 6) {
      image(ShootingArr(0), HALFSCREENSIZE - GunMiddle, ScreenSize - 150)
      image(ShootingArr(5), HALFSCREENSIZE - GunMiddle/2 + 15, ScreenSize - 173)
    }
    else if (ShootingTimer < 15){
      image(ShootingArr(1), HALFSCREENSIZE - GunMiddle, ScreenSize - 150)
    }
    else if (ShootingTimer < 21) {
      image(ShootingArr(2), HALFSCREENSIZE - GunMiddle, ScreenSize - 150)
    }
    else{
      image(ShootingArr(3), HALFSCREENSIZE - GunMiddle, ScreenSize - 150)
    }
  }

  def DrawHUD() : Unit = {
    fill(0); // Set the fill color to black
    textSize(32); // Set the text size

    text("HP: " + PlayerHealth.toInt, 50, ScreenSize - 100);
  }
  def update(): Unit = {
    val Speed: Double = 25
    val BoxCheck : Double = 100

    DirX = cos(toRadians(PlayerAngle))
    DirY = sin(toRadians(PlayerAngle))

    var NewX : Double = PlayerX + Speed * DirX
    var NewY : Double = PlayerY + Speed * DirY

    def isValid(NewX: Double, NewY : Double): Boolean = {

      map(NewX.toInt / BoxSize)((NewY).toInt / BoxSize) == 0 &&
        map((NewX).toInt / BoxSize)((NewY + BoxCheck).toInt / BoxSize) == 0 &&
        map((NewX + BoxCheck).toInt / BoxSize)((NewY).toInt / BoxSize) == 0 &&
        map((NewX - BoxCheck).toInt / BoxSize)((NewY).toInt / BoxSize) == 0 &&
        map((NewX).toInt / BoxSize)((NewY - BoxCheck).toInt / BoxSize) == 0
    }

    if (UPPRESS && isValid(NewX, NewY))
      {
        PlayerY = NewY; PlayerX = NewX
      }

    NewX = PlayerX - Speed * DirX
    NewY = PlayerY - Speed * DirY

    if (DOWNPRESS && isValid(NewX, NewY))
    {
      PlayerY = NewY; PlayerX = NewX
    }

    val NewDirX = cos(toRadians(PlayerAngle - 90))
    val NewDirY = sin(toRadians(PlayerAngle - 90))

    NewX = PlayerX + Speed * NewDirX
    NewY = PlayerY + Speed * NewDirY

    if (LEFTPRESS && isValid(NewX, NewY))
    {
      PlayerY = NewY; PlayerX = NewX
    }

    NewX = PlayerX - Speed * NewDirX
    NewY = PlayerY - Speed * NewDirY

    if (RIGHTPRESS && isValid(NewX, NewY))
    {
      PlayerY = NewY; PlayerX = NewX
    }

    if (DPRESS){PlayerAngle += 3}
    if (APRESS) {PlayerAngle -= 3}

    PlayerAngle = normalizeAngle(PlayerAngle)

    DirX = cos(toRadians(PlayerAngle))
    DirY = sin(toRadians(PlayerAngle))

    if (ShootingTimer == 35) ShootingTimer = 0
    if (SPACEPRESS) FireBullet()
    if (ShootingTimer != 0) {ShootingTimer += 1}
    updateSprite()
  }

  def DrawGameOver(): Unit = {
    textSize(32)
    fill(255, 0, 0)
    text("Game Over", width / 2 - 100, height / 2)
  }
  override def draw(): Unit = {
    background(255)
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
    val CacoDemonIMG = loadImage("src/engine/graphics/Images/CacodemonSmall.png");
    //val croppedImage = orignalIMG.get(0, 0, 64, 64);

    ObjectArr = Array(new Object( 1,orignalIMG, 2000,2000, 0.4f, 100))
    //ObjectArr = ObjectArr :+ new Object(2, 1,orignalIMG, 3000,3000, 0.4f, 100)
    ObjectArr = ObjectArr :+ new Object( 2,CacoDemonIMG, 3000,3000, 0.4f, 100)

    orignalIMG = loadImage("src/engine/graphics/Images/DoomWeapons.png");

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
      case VK_SPACE => SPACEPRESS = true
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
      case VK_SPACE => SPACEPRESS = false
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
