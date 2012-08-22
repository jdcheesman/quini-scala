package domain


case class Match(ht: Team, at: Team, homeScore: Int, awayScore: Int) {
  val homeTeam = ht
  val awayTeam = at
  
  def draw(): Boolean = (homeScore == awayScore)
  def homeWin(): Boolean = (homeScore > awayScore)
  def awayWin(): Boolean = (homeScore < awayScore)
  
  
  def homeGoals(): Int = homeScore
  def awayGoals(): Int = awayScore
  
  override def toString = { homeTeam + " " + homeScore + " - " + awayTeam + " " + awayScore} 
}