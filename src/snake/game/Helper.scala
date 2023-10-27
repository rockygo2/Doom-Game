package snake.game

import scala.math._

object Helper {
  def NormalizeAngle(angle: Int): Int = {
    var normalizedAngle = angle % 360
    if (normalizedAngle < 0) {
      normalizedAngle += 360
    }
    normalizedAngle
  }
  def getTanInverse(x : Double, y : Double): Double = {
    NormalizeAngle(math.toDegrees(math.atan2(y, x)).toInt)
  }
  def canSeePlayer(PosX: Double, PosY: Double): Boolean = {

    // bad function need to fix :(
    val PosX2 = PosX - 60
    val PosY2 = PosY - 60
    val SpriteX = Player.PlayerX - PosX2
    val SpriteY = Player.PlayerY - PosY

    val TanInverse: Double = Helper.getTanInverse(SpriteX, SpriteY)
    val CurrentDirX: Float = cos(toRadians(TanInverse)).toFloat
    val CurrentDirY: Float = sin(toRadians(TanInverse)).toFloat

    var RayDirX: Double = CurrentDirX
    var RayDirY: Double = CurrentDirY
    var CurrentBoxX: Int = PosX2.toInt / Miscellaneous.BOX_SIZE
    var CurrentBoxY: Int = PosY2.toInt / Miscellaneous.BOX_SIZE

    val startX = PosX2 / Miscellaneous.BOX_SIZE
    val startY = PosY2 / Miscellaneous.BOX_SIZE

    var CurrentX: Double = PosX / Miscellaneous.BOX_SIZE
    var CurrentY: Double = PosY2 / Miscellaneous.BOX_SIZE
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

      if (Miscellaneous.map(CurrentBoxX)(CurrentBoxY) > 0) {
        hit = true
        if (abs(startX - CurrentX) > abs(Player.PlayerX / Miscellaneous.BOX_SIZE - PosX2 / Miscellaneous.BOX_SIZE) && abs(startY - CurrentY) > abs(Player.PlayerY / Miscellaneous.BOX_SIZE - PosY2 / Miscellaneous.BOX_SIZE)) {
          return true
        }
        else {
          return false
        }
      }
    }
    false
  }

}
