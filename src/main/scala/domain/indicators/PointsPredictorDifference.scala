package domain.indicators


import domain.Result
import domain.HomeWin
import domain.Draw
import domain.AwayWin
import domain.Team


object PointsPredictorDifference {

  def calculate(homeTeam: List[Team], awayTeam: List[Team], delta: Double ): Result = {
    
    val pointsHomeTeam = homeTeam.foldLeft(0)((r,c) => r+c.points)
    // normalise points away using pointsHomeTeam
    val pointsAwayTeam = awayTeam.foldLeft(0)((r,c) => r+c.points) -  pointsHomeTeam 
    
    if (Math.abs(pointsAwayTeam) <= delta) new Draw
    else if (pointsAwayTeam < 0) new HomeWin
    else new AwayWin
  }
  
}