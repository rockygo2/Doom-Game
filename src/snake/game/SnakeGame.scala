// DO NOT MODIFY FOR BASIC SUBMISSION
// scalastyle:off

package snake.game
import scala.math._
import processing.core._
import processing.event.KeyEvent
import ddf.minim.{AudioSample, Minim}

import java.awt.event.KeyEvent._


class DoomGame extends PApplet with SnakeGameTrait{

  // KEYS
  var UP_PRESS: Boolean = false
  var DOWN_PRESS: Boolean = false
  var LEFT_PRESS: Boolean = false
  var RIGHT_PRESS: Boolean = false
  var D_PRESS: Boolean = false
  var A_PRESS: Boolean = false
  var SPACE_PRESS: Boolean = false

  // Sounds
  var minim : Minim = _
  var ShotgunSound : AudioSample = _

  def drawWalls(): Unit = {
    val sortedWallArray = Images.WallArr.sortBy(_.WallDistance).reverse
    for (i <- sortedWallArray){
      image(i.Image, i.PosX, i.PosY)
    }
  }

  def BlowUp(currentSprite: Object): Unit = {
    if (currentSprite.Timer == 0 && currentSprite.Health > 0) return
    if (currentSprite.Timer < 5) currentSprite.Image = Images.ExplosionArr(0)
    else if (currentSprite.Timer < 10) currentSprite.Image = Images.ExplosionArr(1)
    else if (currentSprite.Timer < 15) currentSprite.Image = Images.ExplosionArr(2)
    else RemoveObject(currentSprite)
    currentSprite.Timer += 1
  }
  def drawRays(): Unit = {
    Images.WallArr = Array()

    val LineSize : Int = Miscellaneous.SCREEN_SIZE/Miscellaneous.NUM_RAYS
    val RayScale : Double = Miscellaneous.FOV/Miscellaneous.NUM_RAYS.toDouble
    val imgSizeCheck = 64 - LineSize
    val halfRays = Miscellaneous.NUM_RAYS/2
    for (i <- 0 until Miscellaneous.NUM_RAYS){
      val RayAngle = Player.PlayerAngle - Miscellaneous.FOV/2 + i*RayScale
      var RayDirX : Double = cos(toRadians(RayAngle))
      var RayDirY : Double = sin(toRadians(RayAngle))
      var CurrentBoxX : Int = Player.PlayerX.toInt/Miscellaneous.BOX_SIZE
      var CurrentBoxY : Int = Player.PlayerY.toInt/Miscellaneous.BOX_SIZE
      var CurrentX: Double = Player.PlayerX/Miscellaneous.BOX_SIZE
      var CurrentY: Double = Player.PlayerY/Miscellaneous.BOX_SIZE
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

        if (Miscellaneous.map(CurrentBoxX)(CurrentBoxY) > 0){
          hit = true
        }
      }
      //Fix FishEyeEffect
      val depth: Double = math.cos(toRadians(Player.PlayerAngle - RayAngle))
      val DistX : Double = Player.PlayerX - CurrentX*Miscellaneous.BOX_SIZE
      val DistY : Double = Player.PlayerY - CurrentY*Miscellaneous.BOX_SIZE
      val WallDistance : Double = sqrt(pow(DistX, 2) + pow(DistY, 2))
      if(i == halfRays){
        Player.WallDistanceShooting = WallDistance
      }
      val WallHeight = (Miscellaneous.SCREEN_SIZE/WallDistance) * 69/depth
      val BoxNuM= Miscellaneous.map(CurrentBoxX)(CurrentBoxY)
      var imageHeightY : Double = 64
      val ImageYLocation = 0

      if (imageHeightY <= 1) imageHeightY = 1
      val ImageX = if (!isX) abs((CurrentX - CurrentBoxX) * imgSizeCheck) else abs((CurrentY - CurrentBoxY) * imgSizeCheck)
      val croppedImg = Images.TexturesArr(BoxNuM).get(ImageX.toInt, ImageYLocation, LineSize, imageHeightY.toInt)
      croppedImg.resize(LineSize, WallHeight.toInt*2)

      val input : Walls = new Walls(croppedImg , WallDistance.toFloat, (i*LineSize).toFloat, Miscellaneous.HALF_SCREEN_SIZE - WallHeight.toInt)
      Images.WallArr = Images.WallArr :+ input
    }
  }

  def setBackground(): Unit = {
    fill(200, 200, 200)
    rect(0, Miscellaneous.HALF_SCREEN_SIZE, Miscellaneous.SCREEN_SIZE, Miscellaneous.HALF_SCREEN_SIZE)
    fill(100, 100, 100)
    rect(0, 0, Miscellaneous.SCREEN_SIZE, Miscellaneous.HALF_SCREEN_SIZE)
  }

  def drawSprite(): Unit = {
    for (i <- Images.ObjectArr) {
      val SpriteX: Double = i.PosX - Player.PlayerX
      val SpriteY: Double = i.PosY - Player.PlayerY
      val WallDistance: Float = sqrt(pow(SpriteX, 2) + pow(SpriteY, 2)).toFloat
      val arcTan: Double = math.toDegrees(math.atan2(SpriteY, SpriteX))
      val AngleDif: Double = ((arcTan - Player.PlayerAngle + 540) % 360) - 180
      val XSpriteLocation: Double = (Miscellaneous.FOV / 2 + AngleDif) * Miscellaneous.SCREEN_SIZE / Miscellaneous.FOV
      val proj = (100 / WallDistance * i.Scale) * 4000
      val WallHeight = (Miscellaneous.SCREEN_SIZE / WallDistance) * 69

      val YSpriteLocation = Miscellaneous.HALF_SCREEN_SIZE + WallHeight - proj


      if (XSpriteLocation > 0 - proj && XSpriteLocation < Miscellaneous.SCREEN_SIZE && (YSpriteLocation > 0 && YSpriteLocation < Miscellaneous.SCREEN_SIZE)) {
        val DrawEnemy: PImage = i.Image.copy()
        DrawEnemy.resize(proj.toInt, proj.toInt)

        if (WallDistance > 0) {
          val input: Walls = new Walls(DrawEnemy, WallDistance, XSpriteLocation.toFloat, YSpriteLocation)
          Images.WallArr = Images.WallArr :+ input
        }
      }
    }
  }

  def updateSprite(): Unit = {
    for (i <- Images.ObjectArr){
      i.objType match {
        case "HeadDemon" => Images.ObjectArr = i.UpdateSprite(Images.ObjectArr); BlowUp(i)
        case "CacoDemon" => Images.ObjectArr = i.UpdateSprite(Images.ObjectArr); BlowUp(i)
        case "BrownDemon" => Images.ObjectArr = i.UpdateSprite(Images.ObjectArr); BlowUp(i)
        case "Images.Bullet" => Images.ObjectArr = i.UpdateSprite(Images.ObjectArr)
        case "Key" => Images.ObjectArr = i.UpdateSprite(Images.ObjectArr)
        case "HealthPack" => Images.ObjectArr = i.UpdateSprite(Images.ObjectArr)
        case "Speed" =>  Images.ObjectArr = i.UpdateSprite(Images.ObjectArr)
        case _ =>
      }
    }
  }

  def RemoveObject(currentSprite : Object) : Unit= {
    Images.ObjectArr = Images.ObjectArr.filterNot(obj => obj == currentSprite)
  }

  def FireBullet(): Unit = {
    if (Player.ShootingTimer == 0) {
      ShotgunSound.trigger()
      for (i <- Images.ObjectArr) {
        val SpriteX: Double = i.PosX - Player.PlayerX
        val SpriteY: Double = i.PosY - Player.PlayerY
        val TanInverse: Double = Helper.getTanInverse(SpriteX, SpriteY)
        val WallDistance: Float = sqrt(pow(SpriteX, 2) + pow(SpriteY, 2)).toFloat
        val proj = (100 / WallDistance * i.Scale) * 4000
        if (Player.PlayerAngle > TanInverse && Player.PlayerAngle < TanInverse + (proj/Miscellaneous.SCREEN_SIZE) * Miscellaneous.FOV && WallDistance < Player.WallDistanceShooting){
          i.Health -= Player.PlayerDamage
          if (i.Health <= 0){
            BlowUp(i)
          }
        }
      }
      Player.ShootingTimer = 1
    }
  }

  def drawGun(): Unit = {
    val GunMiddle = 75
    if (Player.ShootingTimer == 0){
      image(Images.ShootingArr(0), Miscellaneous.HALF_SCREEN_SIZE - GunMiddle, Miscellaneous.SCREEN_SIZE - 150)
    }
    else if( Player.ShootingTimer < 3){
      image(Images.ShootingArr(0), Miscellaneous.HALF_SCREEN_SIZE - GunMiddle, Miscellaneous.SCREEN_SIZE - 150)
      image(Images.ShootingArr(4), Miscellaneous.HALF_SCREEN_SIZE - GunMiddle/2 + 15, Miscellaneous.SCREEN_SIZE - 165)
    }
    else if (Player.ShootingTimer < 6) {
      image(Images.ShootingArr(0), Miscellaneous.HALF_SCREEN_SIZE - GunMiddle, Miscellaneous.SCREEN_SIZE - 150)
      image(Images.ShootingArr(5), Miscellaneous.HALF_SCREEN_SIZE - GunMiddle/2 + 15, Miscellaneous.SCREEN_SIZE - 173)
    }
    else if (Player.ShootingTimer < 15){
      image(Images.ShootingArr(1), Miscellaneous.HALF_SCREEN_SIZE - GunMiddle, Miscellaneous.SCREEN_SIZE - 150)
    }
    else if (Player.ShootingTimer < 21) {
      image(Images.ShootingArr(2), Miscellaneous.HALF_SCREEN_SIZE - GunMiddle, Miscellaneous.SCREEN_SIZE - 150)
    }
    else{
      image(Images.ShootingArr(3), Miscellaneous.HALF_SCREEN_SIZE - GunMiddle, Miscellaneous.SCREEN_SIZE - 150)
    }
  }

  def DrawHUD() : Unit = {
    fill(0); // Set the fill color to black
    textSize(20)
    text("X", Miscellaneous.HALF_SCREEN_SIZE - 10, Miscellaneous.HALF_SCREEN_SIZE - 10)
    textSize(32); // Set the text size

    text("HP: " + Player.PlayerHealth.toInt, 50, Miscellaneous.SCREEN_SIZE - 100)
    text("Keys: " + Player.TotalKeys +"/11", 50, Miscellaneous.SCREEN_SIZE - 200)
  }

  def update(): Unit = {
    val BoxCheckSize: Double = 100

    def direction(angle: Double): (Double, Double) = {
      val radians = toRadians(angle)
      (cos(radians), sin(radians))
    }

    def calculateNewPosition(angle: Double): (Double, Double) = {
      val Dir = direction(angle)
      val DirX = Dir._1
      val DirY = Dir._2
      (Player.PlayerX + Player.PlayerSpeed * DirX, Player.PlayerY + Player.PlayerSpeed * DirY)
    }


    def isValid(x: Double, y: Double): Boolean = {
        Miscellaneous.isPositionClear(x, y) && Miscellaneous.isPositionClear(x, y + BoxCheckSize) &&
        Miscellaneous.isPositionClear(x + BoxCheckSize, y) && Miscellaneous.isPositionClear(x, y - BoxCheckSize) &&
        Miscellaneous.isPositionClear(x - BoxCheckSize, y)
    }

    def updatePositionIfValid(angle: Double): Unit = {
      val New = calculateNewPosition(angle)
      val NewX = New._1
      val NewY = New._2
      if (isValid(NewX, NewY)) {
        Player.PlayerX = NewX
        Player.PlayerY = NewY
      }
    }

    if (UP_PRESS) updatePositionIfValid(Player.PlayerAngle)
    if (DOWN_PRESS) updatePositionIfValid(Player.PlayerAngle + 180)
    if (LEFT_PRESS) updatePositionIfValid(Player.PlayerAngle - 90)
    if (RIGHT_PRESS) updatePositionIfValid(Player.PlayerAngle + 90)

    if (D_PRESS) Player.PlayerAngle += 3
    if (A_PRESS) Player.PlayerAngle -= 3

    Player.PlayerAngle = Helper.NormalizeAngle(Player.PlayerAngle)

    if (Player.ShootingTimer == 35) Player.ShootingTimer = 0
    if (SPACE_PRESS) FireBullet()
    if (Player.ShootingTimer != 0) Player.ShootingTimer += 1

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
    if(Player.TotalKeys == 11){
      GameWin()
    }
    else if (Player.PlayerHealth <= 0){
      DrawGameOver()
    }
    else {
      setBackground()
      drawRays()
      drawSprite()
      drawWalls()
      drawGun()
      DrawHUD()
      update()
    }
  }

  override def setup(): Unit = {
    Images.TexturesArr = Array(loadImage("src/engine/graphics/Images/Textures/barrel.png"),
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

    val HeadDemonIMG = loadImage("src/engine/graphics/Images/HeadDoom.png")
    val CacoDemonIMG = loadImage("src/engine/graphics/Images/CacodemonSmall.png")
    val BrownDemonIMG = loadImage("src/engine/graphics/Images/Afrit.png")
    val HealthPackIMG = loadImage("src/engine/graphics/Images/HealthPack.png")
    val SpeedBoostIMG = loadImage("src/engine/graphics/Images/SpeedPower.png")
    val KeyIMG = loadImage("src/engine/graphics/Images/Key.png")

    // Main Room
    Images.ObjectArr = Images.ObjectArr :+ new Object("HealthPack", HealthPackIMG, 9950, 10110, 0.1f)
    Images.ObjectArr = Images.ObjectArr :+ new Object("HealthPack", HealthPackIMG, 10060, 12280, 0.1f)
    Images.ObjectArr = Images.ObjectArr :+ new Object("Key", KeyIMG, 7446, 11562, 0.2f)

    // Maze Room 1
    Images.ObjectArr = Images.ObjectArr :+ new Object( "CacoDemon",CacoDemonIMG, 2670,12890, 0.4f, 100, 25)
    Images.ObjectArr = Images.ObjectArr :+ new Object( "CacoDemon",CacoDemonIMG, 2920,18960, 0.4f, 100, 25)
    Images.ObjectArr = Images.ObjectArr :+ new Object( "CacoDemon",CacoDemonIMG, 4620,12890, 0.4f, 100, 25)
    Images.ObjectArr = Images.ObjectArr :+ new Object( "CacoDemon",CacoDemonIMG, 4980,18960, 0.4f, 100, 25)
    Images.ObjectArr = Images.ObjectArr :+ new Object( "CacoDemon",CacoDemonIMG, 5490,15990, 0.4f, 100, 25)
    Images.ObjectArr = Images.ObjectArr :+ new Object( "HealthPack",HealthPackIMG, 2714,17960, 0.1f)
    Images.ObjectArr = Images.ObjectArr :+ new Object( "HealthPack",HealthPackIMG, 2814,14173, 0.1f)
    Images.ObjectArr = Images.ObjectArr :+ new Object( "Key",KeyIMG, 5564,16050, 0.2f)
    Images.ObjectArr = Images.ObjectArr :+ new Object( "Key",KeyIMG, 2714,16050, 0.2f)

    // Maze Rest
    Images.ObjectArr = Images.ObjectArr :+ new Object("CacoDemon", CacoDemonIMG, 9940, 19260, 0.4f, 100, 50)
    Images.ObjectArr = Images.ObjectArr :+ new Object("CacoDemon", CacoDemonIMG, 7460, 18990, 0.4f, 100, 50)
    Images.ObjectArr = Images.ObjectArr :+ new Object( "Key",KeyIMG, 9220,19260, 0.2f)
    Images.ObjectArr = Images.ObjectArr :+ new Object( "Key",KeyIMG, 8300,18960, 0.2f)
    Images.ObjectArr = Images.ObjectArr :+ new Object( "Key",KeyIMG, 8460,19660, 0.2f)
    Images.ObjectArr = Images.ObjectArr :+ new Object( "HealthPack",HealthPackIMG, 7510,16470, 0.1f)
    Images.ObjectArr = Images.ObjectArr :+ new Object( "HealthPack",HealthPackIMG, 7520,18710, 0.1f)

    // Purple Place
    Images.ObjectArr = Images.ObjectArr :+ new Object("Key", KeyIMG, 740, 10330, 0.2f)
    Images.ObjectArr = Images.ObjectArr :+ new Object("Key", KeyIMG, 2390, 7170, 0.2f)
    Images.ObjectArr = Images.ObjectArr :+ new Object("Key", KeyIMG, 7150, 5620, 0.2f)

    Images.ObjectArr = Images.ObjectArr :+ new Object("Speed", SpeedBoostIMG, 4970, 9700, 0.2f)

    Images.ObjectArr = Images.ObjectArr :+ new Object("BrownDemon", BrownDemonIMG, 2665, 3420, 0.4f, 100, 25)
    Images.ObjectArr = Images.ObjectArr :+ new Object("BrownDemon", BrownDemonIMG, 6260, 4650, 0.4f, 100, 25)
    Images.ObjectArr = Images.ObjectArr :+ new Object("BrownDemon", BrownDemonIMG, 630, 4700, 0.4f, 100, 25)

    Images.ObjectArr = Images.ObjectArr :+ new Object("HeadDemon", HeadDemonIMG, 3800, 3670, 0.4f, 100, 50)
    Images.ObjectArr = Images.ObjectArr :+ new Object("HeadDemon", HeadDemonIMG, 4788, 3900, 0.4f, 100, 50)
    Images.ObjectArr = Images.ObjectArr :+ new Object("HeadDemon", HeadDemonIMG, 2160, 6880, 0.4f, 100, 50)
    Images.ObjectArr = Images.ObjectArr :+ new Object("HeadDemon", HeadDemonIMG, 2008, 8960, 0.4f, 100, 50)
    Images.ObjectArr = Images.ObjectArr :+ new Object("HeadDemon", HeadDemonIMG, 2650, 2570, 0.4f, 100, 50)

    Images.ObjectArr = Images.ObjectArr :+ new Object("CacoDemon", CacoDemonIMG, 7000, 5800, 0.4f, 100, 25)
    Images.ObjectArr = Images.ObjectArr :+ new Object("CacoDemon", CacoDemonIMG, 3200, 7640, 0.4f, 100, 25)
    Images.ObjectArr = Images.ObjectArr :+ new Object("CacoDemon", CacoDemonIMG, 1120, 9610, 0.4f, 100, 25)
    Images.ObjectArr = Images.ObjectArr :+ new Object("CacoDemon", CacoDemonIMG, 1920, 6400, 0.4f, 100, 25)
    Images.ObjectArr = Images.ObjectArr :+ new Object("CacoDemon", CacoDemonIMG, 630, 5201, 0.4f, 100, 25)
    Images.ObjectArr = Images.ObjectArr :+ new Object("CacoDemon", CacoDemonIMG, 630, 4700, 0.4f, 100, 25)

    Images.ObjectArr = Images.ObjectArr :+ new Object("HealthPack", HealthPackIMG, 3680, 5760, 0.1f)
    Images.ObjectArr = Images.ObjectArr :+ new Object("HealthPack", HealthPackIMG, 5550, 4029, 0.1f)

    // Blue Place
    Images.ObjectArr = Images.ObjectArr :+ new Object("Key", KeyIMG, 15143, 3400, 0.2f)
    Images.ObjectArr = Images.ObjectArr :+ new Object("Key", KeyIMG, 9000, 3400, 0.2f)
    Images.ObjectArr = Images.ObjectArr :+ new Object("Key", KeyIMG, 12419, 15279, 0.2f)

    Images.ObjectArr = Images.ObjectArr :+ new Object("Speed", SpeedBoostIMG, 15226, 15386, 0.1f)
    Images.ObjectArr = Images.ObjectArr :+ new Object("HealthPack", HealthPackIMG, 15226, 14836, 0.1f)

    Images.ObjectArr = Images.ObjectArr :+ new Object("BrownDemon", BrownDemonIMG, 12758, 7899, 0.4f, 100, 25)
    Images.ObjectArr = Images.ObjectArr :+ new Object("CacoDemon", CacoDemonIMG, 12075, 8988, 0.4f, 100, 25)
    Images.ObjectArr = Images.ObjectArr :+ new Object("CacoDemon", CacoDemonIMG, 12163, 13768, 0.4f, 100, 25)
    Images.ObjectArr = Images.ObjectArr :+ new Object("CacoDemon", CacoDemonIMG, 12155, 18780, 0.4f, 100, 25)
    Images.ObjectArr = Images.ObjectArr :+ new Object("CacoDemon", CacoDemonIMG, 11166, 17193, 0.4f, 100, 25)
    Images.ObjectArr = Images.ObjectArr :+ new Object("CacoDemon", CacoDemonIMG, 14123, 15954, 0.4f, 100, 25)
    Images.ObjectArr = Images.ObjectArr :+ new Object("CacoDemon", CacoDemonIMG, 14123, 15954, 0.4f, 100, 25)
    Images.ObjectArr = Images.ObjectArr :+ new Object("CacoDemon", CacoDemonIMG, 10285, 629, 0.4f, 100, 25)
    Images.ObjectArr = Images.ObjectArr :+ new Object("CacoDemon", CacoDemonIMG, 10285, 1000, 0.4f, 100, 25)
    Images.ObjectArr = Images.ObjectArr :+ new Object("CacoDemon", CacoDemonIMG, 10285, 1400, 0.4f, 100, 25)
    Images.ObjectArr = Images.ObjectArr :+ new Object("CacoDemon", CacoDemonIMG, 10285, 1800, 0.4f, 100, 25)
    Images.ObjectArr = Images.ObjectArr :+ new Object("CacoDemon", CacoDemonIMG, 10285, 2200, 0.4f, 100, 25)
    Images.ObjectArr = Images.ObjectArr :+ new Object("CacoDemon", CacoDemonIMG, 10285, 2600, 0.4f, 100, 25)
    Images.ObjectArr = Images.ObjectArr :+ new Object("CacoDemon", CacoDemonIMG, 10285, 3000, 0.4f, 100, 25)
    Images.ObjectArr = Images.ObjectArr :+ new Object("CacoDemon", CacoDemonIMG, 10285, 3400, 0.4f, 100, 25)
    Images.ObjectArr = Images.ObjectArr :+ new Object("CacoDemon", CacoDemonIMG, 10285, 3800, 0.4f, 100, 25)
    Images.ObjectArr = Images.ObjectArr :+ new Object("CacoDemon", CacoDemonIMG, 10285, 4200, 0.4f, 100, 25)
    Images.ObjectArr = Images.ObjectArr :+ new Object("CacoDemon", CacoDemonIMG, 10285, 4600, 0.4f, 100, 25)
    Images.ObjectArr = Images.ObjectArr :+ new Object("CacoDemon", CacoDemonIMG, 10285, 5000, 0.4f, 100, 25)


    val WeaponsIMG = loadImage("src/engine/graphics/Images/DoomWeapons.png")

    var croppedImage = WeaponsIMG.get(135, 251, 78, 60)
    croppedImage.resize(150, 150)
    Images.ShootingArr =  Images.ShootingArr :+ croppedImage

    croppedImage = WeaponsIMG.get(218, 190, 118, 121)
    croppedImage.resize(150, 150)
    Images.ShootingArr = Images.ShootingArr :+ croppedImage

    croppedImage = WeaponsIMG.get(339, 160, 87, 151)
    croppedImage.resize(150, 150)
    Images.ShootingArr = Images.ShootingArr :+ croppedImage

    croppedImage = WeaponsIMG.get(429, 181, 113, 130)
    croppedImage.resize(150, 150)
    Images.ShootingArr = Images.ShootingArr :+ croppedImage

    croppedImage = WeaponsIMG.get(235, 157, 44, 30)
    Images.ShootingArr = Images.ShootingArr :+ croppedImage

    croppedImage = WeaponsIMG.get(282, 143, 54, 44)
    Images.ShootingArr = Images.ShootingArr :+ croppedImage

    val ProjectilesIMG = loadImage("src/engine/graphics/Images/Projectiles.png")
    croppedImage = ProjectilesIMG.get(47, 43, 13, 13)
    croppedImage.resize(150, 150)
    Images.Bullet = croppedImage


    croppedImage = ProjectilesIMG.get(2, 861, 72, 57)
    croppedImage.resize(150, 150)
    Images.ExplosionArr = Images.ExplosionArr :+ croppedImage

    croppedImage = ProjectilesIMG.get(76, 849, 87, 69)
    croppedImage.resize(150, 150)
    Images.ExplosionArr = Images.ExplosionArr :+ croppedImage

    croppedImage = ProjectilesIMG.get(165, 835, 102, 83)
    croppedImage.resize(150, 150)
    Images.ExplosionArr = Images.ExplosionArr :+ croppedImage

    frameRate(30)

    minim = new Minim(this)
    val music = minim.loadFile(dataPath("Doom_Soundtrack.mp3"))
    ShotgunSound = minim.loadSample(dataPath("ShotGun.mp3"))
    music.loop()

  }
  override def settings(): Unit = {
    size(Miscellaneous.SCREEN_SIZE, Miscellaneous.SCREEN_SIZE)
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
    PApplet.main("snake.game.DoomGame")
  }
}
