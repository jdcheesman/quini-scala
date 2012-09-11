package domain.indicators

class Configuration(ad: Double, ah: Double, dh: Double ) {
  val awayDraw = ad
  val awayHome = ah
  val drawHome = dh
  
  override def toString = "Configuration[awayDraw=" + awayDraw + ", awayHome=" + awayHome + ", drawHome=" + drawHome + "]"
}