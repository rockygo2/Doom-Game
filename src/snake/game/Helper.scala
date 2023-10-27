package snake.game

object Helper {
  def normalizeAngle(angle: Int): Int = {
    var normalizedAngle = angle % 360
    if (normalizedAngle < 0) {
      normalizedAngle += 360
    }
    normalizedAngle
  }
  def getTanInverse(x : Double, y : Double): Double = {
    normalizeAngle(math.toDegrees(math.atan2(y, x)).toInt)
  }
}
