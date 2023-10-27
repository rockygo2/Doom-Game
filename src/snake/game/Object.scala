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
  
  private def SpeedLogic(): Unit = {
    val SpriteX = Player.X - PosX
    val SpriteY = Player.Y - PosY
    if (Timer != 0) {
      Timer += 1
      // 20 seconds as FPS is 30
      if (Timer > 600) {
        Player.PlayerSpeed -= 25
        Helper.RemoveObject(this)
      }
    }
    if (SpriteX <= 300 && SpriteY <= 300 && SpriteX >= -300 && SpriteY >= -300) {
      Timer = 1
      PosX = -1000
      Player.PlayerSpeed += 25
    }
  }

  private def HealthPackLogic(): Unit = {
    val SpriteX = Player.X - PosX
    val SpriteY = Player.Y - PosY
    if (SpriteX <= 300 && SpriteY <= 300 && SpriteX >= -300 && SpriteY >= -300 && Player.Health < 100) {
      Player.Health += 50
      if (Player.Health > 100) {
        Player.Health = 100
      }
      Helper.RemoveObject(this)
    }
  }

  private def BulletLogic(): Unit = {
    val Speed = 90
    val checkSpriteBoundsX: Float = PosX + Speed * BulletDirX
    val checkSpriteBoundsY: Float = PosY + Speed * BulletDirY

    val SpriteX = Player.X - PosX
    val SpriteY = Player.Y - PosY

    if (SpriteX <= 300 && SpriteY <= 300 && SpriteX >= -300 && SpriteY >= -300) {
      Player.Health -= Damage
      Helper.RemoveObject(this)
    }

    if (Miscellaneous.map(checkSpriteBoundsX.toInt / Miscellaneous.BOX_SIZE)(checkSpriteBoundsY.toInt / Miscellaneous.BOX_SIZE) == 0) {
      PosX = checkSpriteBoundsX
      PosY = checkSpriteBoundsY
    }
    else Helper.RemoveObject(this)
  }

  private def MovementLogic(): Unit = {
      val Speed: Float = 10f
      if (Timer == 0) {
        if (!Helper.canSeePlayer(PosX, PosY)) return
        val SpriteX = Player.X - PosX
        val SpriteY = Player.Y - PosY
        if (SpriteX <= 300 && SpriteY <= 300 && SpriteX >= -300 && SpriteY >= -300) {
          Timer = 1
          Player.Health -= Damage
        }
        val TanInverse: Double = Helper.getTanInverse(SpriteX, SpriteY)
        val CurrentDirX: Float = cos(toRadians(TanInverse)).toFloat
        val CurrentDirY: Float = sin(toRadians(TanInverse)).toFloat

        val checkSpriteBoundsX: Float = PosX + Speed * CurrentDirX
        val checkSpriteBoundsY: Float = PosY + Speed * CurrentDirY
        PosX = checkSpriteBoundsX
        PosY = checkSpriteBoundsY
      }else{
        Helper.BlowUp(this)
      }
  }

  private def ShootingLogic(): Unit = {
    if (Timer == 0) {
      if (BulletTimer == 0) {
        val newBullet = new Object("Bullet", Images.Bullet, PosX, PosY, 0.3f, 99999, Damage)
        val SpriteX = Player.X - PosX
        val SpriteY = Player.Y - PosY

        val TanInverse: Double = Helper.getTanInverse(SpriteX, SpriteY)
        val CurrentDirX: Float = cos(toRadians(TanInverse)).toFloat
        val CurrentDirY: Float = sin(toRadians(TanInverse)).toFloat

        newBullet.BulletDirX = CurrentDirX
        newBullet.BulletDirY = CurrentDirY
        BulletTimer += 1
        Images.ObjectArr = Images.ObjectArr :+ newBullet
      }
      if (BulletTimer == 130) {
        BulletTimer = 0
      }
      else {
        BulletTimer += 1
      }
    } else {
      Helper.BlowUp(this)
    }
  }

  private def KeyLogic(): Unit = {
    val SpriteX = Player.X - PosX
    val SpriteY = Player.Y - PosY
    if (SpriteX <= 300 && SpriteY <= 300 && SpriteX >= -300 && SpriteY >= -300) {
      Player.TotalKeys += 1
      Helper.RemoveObject(this)
    }
  }

  def UpdateSprite(): Unit = {
    objType match {
      case "HeadDemon" =>  MovementLogic()
      case "CacoDemon" => ShootingLogic()
      case "BrownDemon" => MovementLogic(); ShootingLogic()
      case "Key" => KeyLogic()
      case "Bullet" => BulletLogic()
      case "HealthPack" => HealthPackLogic()
      case "Speed" => SpeedLogic()
      case _ =>
    }
  }
}