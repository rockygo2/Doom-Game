package snake.game

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
}
