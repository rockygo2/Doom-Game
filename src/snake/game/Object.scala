package snake.game

import processing.core.PImage
import scala.math._

class Object (
               var objType : String,
               var Image : PImage,
               var PosX : Float,
               var PosY : Float,
               val Scale : Float,
               var Health : Double = 999999999,
               val  Damage : Double = 0
             ) {
  var Timer: Int = 0
  private var BulletTimer: Int = 0
  private var BulletDirX: Float = 0
  private var BulletDirY: Float = 0


  private def RemoveObject(ObjectArr : Array[Object]): Array[Object] = {
    ObjectArr.filterNot(obj => obj == this)
  }
  private def SpeedLogic( ObjectArr : Array[Object]): Array[Object] = {
    val SpriteX = Player.PlayerX - PosX
    val SpriteY = Player.PlayerY - PosY
    if (Timer != 0) {
      Timer += 1
      // 20 seconds as FPS is 30
      if (Timer > 600) {
        Player.PlayerSpeed -= 25
        return RemoveObject(ObjectArr)
      }
    }
    if (SpriteX <= 300 && SpriteY <= 300 && SpriteX >= -300 && SpriteY >= -300) {
      Timer = 1
      PosX = -1000
      Player.PlayerSpeed += 25
    }
    ObjectArr
  }

  private def HealthPackLogic( ObjectArr : Array[Object]): Array[Object] = {
    val SpriteX = Player.PlayerX - PosX
    val SpriteY = Player.PlayerY - PosY
    if (SpriteX <= 300 && SpriteY <= 300 && SpriteX >= -300 && SpriteY >= -300 && Player.PlayerHealth < 100) {
      Player.PlayerHealth += 50
      if (Player.PlayerHealth > 100) {
        Player.PlayerHealth = 100
      }
      return RemoveObject(ObjectArr)
    }
    ObjectArr
  }

  private def BulletLogic( ObjectArr : Array[Object]): Array[Object] = {
    val Speed = 90
    val checkSpriteBoundsX: Float = PosX + Speed * BulletDirX
    val checkSpriteBoundsY: Float = PosY + Speed * BulletDirY

    val SpriteX = Player.PlayerX - PosX
    val SpriteY = Player.PlayerY - PosY

    if (SpriteX <= 300 && SpriteY <= 300 && SpriteX >= -300 && SpriteY >= -300) {
      Player.PlayerHealth -= Damage
      return RemoveObject(ObjectArr)
    }

    if (Miscellaneous.map(checkSpriteBoundsX.toInt / Miscellaneous.BOX_SIZE)(checkSpriteBoundsY.toInt / Miscellaneous.BOX_SIZE) == 0) {
      PosX = checkSpriteBoundsX
      PosY = checkSpriteBoundsY
      ObjectArr
    }
    else RemoveObject(ObjectArr)
  }

  private def MovementLogic( ObjectArr : Array[Object]): Array[Object] = {
      val Speed: Float = 10f
      if (Timer == 0) {
        val SpriteX = Player.PlayerX - PosX
        val SpriteY = Player.PlayerY - PosY
        if (SpriteX <= 300 && SpriteY <= 300 && SpriteX >= -300 && SpriteY >= -300) {
          Timer = 1
          Player.PlayerHealth -= Damage
        }
        val TanInverse: Double = Helper.getTanInverse(SpriteX, SpriteY)
        val CurrentDirX: Float = cos(toRadians(TanInverse)).toFloat
        val CurrentDirY: Float = sin(toRadians(TanInverse)).toFloat

        val checkSpriteBoundsX: Float = PosX + Speed * CurrentDirX
        val checkSpriteBoundsY: Float = PosY + Speed * CurrentDirY
        PosX = checkSpriteBoundsX
        PosY = checkSpriteBoundsY
      }
    ObjectArr
  }

  private def ShootingLogic(ObjectArr : Array[Object]): Array[Object] = {
    if (Timer == 0) {
      if (BulletTimer == 0) {
        val newBullet = new Object("Bullet", Images.Bullet, PosX, PosY, 0.3f, 99999, Damage)
        val SpriteX = Player.PlayerX - PosX
        val SpriteY = Player.PlayerY - PosY

        val TanInverse: Double = Helper.getTanInverse(SpriteX, SpriteY)
        val CurrentDirX: Float = cos(toRadians(TanInverse)).toFloat
        val CurrentDirY: Float = sin(toRadians(TanInverse)).toFloat

        newBullet.BulletDirX = CurrentDirX
        newBullet.BulletDirY = CurrentDirY
        BulletTimer += 1
        ObjectArr :+ newBullet
      }
      if (BulletTimer == 130) {
        BulletTimer = 0
      }
      else {
        BulletTimer += 1
      }
    }
    ObjectArr
  }

  private def KeyLogic(ObjectArr : Array[Object]): Array[Object] = {
    val SpriteX = Player.PlayerX - PosX
    val SpriteY = Player.PlayerY - PosY
    if (SpriteX <= 300 && SpriteY <= 300 && SpriteX >= -300 && SpriteY >= -300) {
      Player.TotalKeys += 1
      return RemoveObject(ObjectArr)
    }
    ObjectArr
  }

  def UpdateSprite(ObjectArr : Array[Object]): Array[Object] = {
    objType match {
      case "HeadDemon" => if (Helper.canSeePlayer(PosX, PosY)){ MovementLogic(ObjectArr)} else ObjectArr
      case "CacoDemon" => ShootingLogic(ObjectArr)
      case "BrownDemon" => if (Helper.canSeePlayer(PosX, PosY)){ MovementLogic(ObjectArr); ShootingLogic(ObjectArr)} else ObjectArr
      case "Key" => KeyLogic(ObjectArr)
      case "Bullet" => BulletLogic(ObjectArr)
      case "HealthPack" => HealthPackLogic(ObjectArr)
      case "Speed" => SpeedLogic(ObjectArr)
      case _ => ObjectArr
    }
  }
}