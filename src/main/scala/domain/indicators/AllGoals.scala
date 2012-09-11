package domain.indicators

import domain.Team
import domain.Match
import domain.Result

case class AllGoals(config: Configuration) extends Indicator(config.awayDraw, config.awayHome, config.drawHome, "allGoals") {
  
  def calculate(homeTeam: Team, awayTeam: Team, matches: List[Match]): Result = {
    
    val homeTeamWinsAtHome = getHomeWins(matches, homeTeam)
    val homeTeamWinsAway = getAwayWins(matches, homeTeam)
    val awayTeamWinsAtHome = getHomeWins(matches, awayTeam)
    val awayTeamWinsAway = getAwayWins(matches, awayTeam)
    val draws = getDraws(matches, homeTeam) + getDraws(matches, awayTeam)
    
    getPrediction(homeTeamWinsAtHome+homeTeamWinsAway, draws, awayTeamWinsAtHome+awayTeamWinsAway)
  }
  
	  
  def getHomeWins(matches: List[Match], team: Team): Int = matches.filter(m => m.homeWin && m.homeTeam == team).foldLeft(0)((r,c) => r+c.homeGoals)
  def getAwayWins(matches: List[Match], team: Team): Int = matches.filter(m => m.awayWin && m.awayTeam == team).foldLeft(0)((r,c) => r+c.awayGoals)
  def getDraws(matches: List[Match], team: Team): Int = matches.filter(m => m.draw && (m.homeTeam == team  || m.awayTeam == team)).foldLeft(0)((r,c) => r+c.homeGoals)
	  
  
}