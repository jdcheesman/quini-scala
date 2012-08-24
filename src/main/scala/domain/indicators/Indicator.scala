package domain.indicators

import domain.Match
import domain.Result
import domain.Draw
import domain.HomeWin
import domain.AwayWin
import domain.Team

trait Indicator {

  def calculate(homeTeam: Team, awayTeam: Team, matches: List[Match]): Result


  def getPrediction(h: Int, d: Int, a: Int): Result = {

    //TODO: Think about the normalisation here, should it be
    // using the given results (h+d+a) or the total number of matches 
    // being compared (for example last 5)?
    val homeNormalised = (h*1.0) / (h+d+a)
    val awayNormalised = (a*1.0) / (h+d+a)
    val drawNormalised = (d*1.0) / (h+d+a)

    val max = List(homeNormalised, awayNormalised, drawNormalised).max
    if ((h==0 && d==0 && a==0) || (drawNormalised == max)) new Draw
    else if (homeNormalised == max) new HomeWin
    else new AwayWin
  }

}