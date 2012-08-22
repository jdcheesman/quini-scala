package domain.indicators

import domain.Match
import domain.Result
import domain.Draw
import domain.HomeWin
import domain.AwayWin
import domain.Team

trait Indicator {

  def calculate(homeTeam: Team, homeTeamMatches: List[Match], awayTeam: Team, awayTeamMatches: List[Match]): Result


  def getPrediction(h: Int, d: Int, a: Int): Result = {
    assert(h>0 || d>0 || a>0)
    if (h == 0 && a==0) new Draw

    //TODO: Think about the normalisation here, should it be
    // using the given results (h+d+a) or the total number of matches 
    // being compared (for example last 5)?
    val homeNormalised = (h*1.0) / (h+d+a)
    val awayNormalised = (a*1.0) / (h+d+a)
    val drawNormalised = (d*1.0) / (h+d+a)

    if (homeNormalised > 0.5) new HomeWin
    else if (awayNormalised > 0.5) new AwayWin
    else new Draw
  }

}