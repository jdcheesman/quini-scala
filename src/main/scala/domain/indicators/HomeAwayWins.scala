package domain.indicators

import domain.Match
import domain.Result
import domain.HomeWin
import domain.Team

object HomeAwayWins extends Indicator {
  
  def calculate(homeTeam: Team, homeTeamMatches: List[Match], awayTeam: Team, awayTeamMatches: List[Match]): Result = {
    
    val homeWins = getHomeWins(homeTeamMatches, homeTeam)
    val awayWins = getAwayWins(awayTeamMatches, awayTeam)
    val draws = getDraws(homeTeamMatches, homeTeam) + getDraws(awayTeamMatches, awayTeam)
    getPrediction(homeWins, draws, awayWins)
  }
  
  def getHomeWins(matches: List[Match], team: Team): Int = matches.filter(m => m.homeWin && m.homeTeam==team).foldLeft(0)((r,c) => r+1)
  def getAwayWins(matches: List[Match], team: Team): Int = matches.filter(m => m.awayWin && m.awayTeam==team).foldLeft(0)((r,c) => r+1)
  def getDraws(matches: List[Match], team: Team): Int = matches.filter(m => m.draw && (m.homeTeam==team || m.awayTeam == team )).foldLeft(0)((r,c) => r+1)

  


}