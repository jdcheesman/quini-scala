package domain.indicators


import domain.Result

import domain.Match
import domain.Statistic
import domain.HomeWin
import domain.Draw
import domain.AwayWin
import domain.Team


case class PointsPredictorDifference(n: String, d: Double) {
  val name = n
  val delta = d
  
  def run(matches: List[Team], m: Match): Statistic = {
		val result = new Statistic(name)
		for (num <- 0 to (matches.size/2-1)) {
			val prediction = calculate(matches.filter(t => t == m.homeTeam).drop(num), matches.filter(t => t == m.awayTeam).drop(num))
			result.addResult(prediction == m.result)
		}    
		result
  }  

  def calculate(homeTeam: List[Team], awayTeam: List[Team]): Result = {
    
    val pointsHomeTeam = homeTeam.foldLeft(0)((r,c) => r+c.points)
    // normalise points away using pointsHomeTeam
    val pointsAwayTeam = awayTeam.foldLeft(0)((r,c) => r+c.points) -  pointsHomeTeam 
    
    if (Math.abs(pointsAwayTeam) <= delta) new Draw
    else if (pointsAwayTeam < 0) new HomeWin
    else new AwayWin
  }
  
}