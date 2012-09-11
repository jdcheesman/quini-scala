package domain.indicators

import domain.Match
import domain.Result
import domain.HomeWin
import domain.Team

case class HomeAwayWins(config: Configuration) extends Indicator(config.awayDraw, config.awayHome, config.drawHome, "homeAwayWins") {
  
  def calculate(homeTeam: Team, awayTeam: Team, matches: List[Match]): Result = {
    
    val homeWins = getHomeWins(matches, homeTeam)
    val awayWins = getAwayWins(matches, awayTeam)
    val draws = getDraws(matches, homeTeam) + getDraws(matches, awayTeam)
    getPrediction(homeWins, draws, awayWins)
  }
  
  def getHomeWins(matches: List[Match], team: Team): Int = matches.filter(m => m.homeWin && m.homeTeam==team).foldLeft(0)((r,c) => r+1)
  def getAwayWins(matches: List[Match], team: Team): Int = matches.filter(m => m.awayWin && m.awayTeam==team).foldLeft(0)((r,c) => r+1)
  def getDraws(matches: List[Match], team: Team): Int = matches.filter(m => m.draw && (m.homeTeam==team || m.awayTeam == team )).foldLeft(0)((r,c) => r+1)

  


}