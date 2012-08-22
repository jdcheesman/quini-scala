package test.domain.indicators

import domain._
import domain.indicators._



object WinsTest {

  def main(args: Array[String]): Unit = {
    val team1 = new Team("Barcelona")
    val team2 = new Team("Madrid")
    val team3 = new Team("Valencia")
    val team4 = new Team("Deportivo")
    
    // week 1:
    val match1 = new Match(team1, team2, 1, 0)
    val match2 = new Match(team3, team4, 0, 0)
    // week 2
    val match3 = new Match(team2, team3, 10, 5)
    val match4 = new Match(team4, team1, 0, 1)
    // week 3
    val match5 = new Match(team1, team3, 5, 0)
    val match6 = new Match(team2, team4, 0, 0)
    
    // NEXT MATCH: team2 vs. team1
    val homeTeamMatches = match1 :: match3 :: match6 :: Nil
    val awayTeamMatches = match1 :: match4 :: match5 :: Nil
    
    println("By home/away wins: " + HomeAwayWins.calculate(team2, homeTeamMatches, team1, awayTeamMatches))
    
    
    val allMatches = match1 :: match2 :: match3 :: match4 :: match5 :: match6 :: Nil
    println("By all wins: " + AllWins.calculate(team2, allMatches, team1, allMatches))

    println("By home/away goals: " + HomeAwayGoals.calculate(team2, homeTeamMatches, team1, awayTeamMatches))
    println("By all goals: " + AllGoals.calculate(team2, allMatches, team1, allMatches))
    
    
  }

}