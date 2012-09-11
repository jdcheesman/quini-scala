package domain.indicators

import domain.AwayWin
import domain.Draw
import domain.HomeWin
import domain.Match
import domain.Result
import domain.Statistic
import domain.Team


abstract class Indicator(ad: Double, ah: Double, dh: Double, n: String) {
  val awayDraw = ad
  val awayHome = ah
  val drawHome = dh
  val name = n

  def calculate(homeTeam: Team, awayTeam: Team, matches: List[Match]): Result


  def run(matches: List[Match], m: Match): Statistic = {
		val result = new Statistic(name)
		for (num <- 0 to (matches.size/2-1)) {
			val prediction = calculate(m.homeTeam, m.awayTeam, matches.toList.drop(num))
			result.addResult(prediction == m.result)
		}    
		result
  }
  
  
  def getPrediction(h: Int, d: Int, a: Int): Result = {

    //TODO: Think about the normalisation here, should it be
    // using the given results (h+d+a) or the total number of matches 
    // being compared (for example last 5)?
    var homeNormalised = (h*1.0) / (h+d+a)
    var awayNormalised = (a*1.0) / (h+d+a)
    var drawNormalised = (d*1.0) / (h+d+a)

    var max = List(homeNormalised, awayNormalised, drawNormalised).max

    // now apply home/away correctors:
    if (awayNormalised == max) {
      drawNormalised = (drawNormalised / awayDraw)
      homeNormalised = (homeNormalised / awayHome)
    }
    else if (drawNormalised == max){
      homeNormalised = (homeNormalised / drawHome)
    }
    max = List(homeNormalised, awayNormalised, drawNormalised).max
    if ((h==0 && d==0 && a==0) || (drawNormalised == max)) new Draw
    else if (homeNormalised == max) new HomeWin
    else new AwayWin
  }

}