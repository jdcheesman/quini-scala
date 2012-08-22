package domain.indicators

import domain.Team
import domain.Match
import domain.Result

object AllGoals extends Indicator {
  def calculate(homeTeam: Team, homeTeamMatches: List[Match], awayTeam: Team, awayTeamMatches: List[Match]): Result = {
    
    val homeTeamWinsAtHome = getHomeWins(homeTeamMatches, homeTeam)
    val homeTeamWinsAway = getAwayWins(homeTeamMatches, homeTeam)
    val awayTeamWinsAtHome = getHomeWins(awayTeamMatches, awayTeam)
    val awayTeamWinsAway = getAwayWins(awayTeamMatches, awayTeam)
    val draws = getDraws(homeTeamMatches, homeTeam) + getDraws(awayTeamMatches, awayTeam)
    
    getPrediction(homeTeamWinsAtHome+homeTeamWinsAway, draws, awayTeamWinsAtHome+awayTeamWinsAway)
  }
  
	  
  def getHomeWins(matches: List[Match], team: Team): Int = matches.filter(m => m.homeWin && m.homeTeam == team).foldLeft(0)((r,c) => r+c.homeGoals)
  def getAwayWins(matches: List[Match], team: Team): Int = matches.filter(m => m.awayWin && m.awayTeam == team).foldLeft(0)((r,c) => r+c.awayGoals)
  def getDraws(matches: List[Match], team: Team): Int = matches.filter(m => m.draw && (m.homeTeam == team  || m.awayTeam == team)).foldLeft(0)((r,c) => r+c.homeGoals)
	  
  
}