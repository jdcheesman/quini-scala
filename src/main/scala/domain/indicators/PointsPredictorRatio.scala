package domain.indicators

import domain.Result
import domain.HomeWin
import domain.Draw
import domain.AwayWin
import domain.Team


object PointsPredictorRatio {

  def calculate(homeTeam: List[Team], awayTeam: List[Team], delta: Double ): Result = {
    
    val pointsHomeTeam = homeTeam.foldLeft(0)((r,c) => r+c.points) * 1.0
    // normalise points away using pointsHomeTeam
    val pointsAwayTeam = Math.log( (awayTeam.foldLeft(0)((r,c) => r+c.points)*1.0) / pointsHomeTeam ) 

    if (Math.abs(pointsAwayTeam) <= delta) new Draw
    else if (pointsAwayTeam < 1) new HomeWin
    else new AwayWin
  }
  
}