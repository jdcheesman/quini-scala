package main

import scala.slick.session.Database
import Database.threadLocalSession
import scala.slick.jdbc.{GetResult, StaticQuery => Q}
import Q.interpolation
import domain.Match
import domain.Team
import java.sql.{DriverManager, Connection}
import com.typesafe.config.ConfigFactory
import scala.collection.mutable.ListBuffer
import domain.Week
import domain.indicators.AllWins
import domain.indicators.HomeAwayWins
import domain.indicators.AllGoals
import domain.indicators.HomeAwayGoals
import domain.indicators.PointsPredictorRatio
import domain.indicators.PointsPredictorDifference
import domain.Statistic
import domain.CumulativeStatistic


object Runner {

	var conf = ConfigFactory.load
			lazy val dbURL = conf.getString("db.url")

			implicit def strToTeam(x: String) = new Team(x)

	def main(args: Array[String]): Unit = {
		println("in Runner")
		testDatabase
	}


	def testDatabase() {
		println("Running database connectivity tests")

		def loadDriver()  {
			try{
				Class.forName("com.mysql.jdbc.Driver").newInstance
				println("Driver loaded successfully")
			} catch {
			case e: Exception  => {
				println("ERROR: Driver not available: " + e.getMessage)
			}
			}
		} 

		loadDriver


		val year = 2009
		for (w <- 6 to 38) {
					val currentWeek = new Week(year, w)
				val league = 1
				
				//println("wk=" + w + ", league=" + 1)
				
				val matches = new ListBuffer[Match]()
				implicit val getMatchResult = GetResult(r => domain.Match(r.nextString, r.nextString, r.nextInt, r.nextInt))
				Database.forURL(dbURL, driver = "com.mysql.jdbc.Driver") withSession {		  
					Q.queryNA[Match](
					    "select home, away, home_goals, away_goals from liga.match where year=" + year + " and week=" + currentWeek.week + " and league=" + league
					) foreach {m => matches += m }
				}
				val weeksResults = scala.collection.mutable.Map[String, CumulativeStatistic]()
				
				matches.toList.foreach(m => {
				  val res = calculateResultByMatches(year, league, currentWeek.week, m) ::: calculateResultByPoints(year, league, currentWeek.week, m)
				  res.foreach(s => {
				    val cumStat = weeksResults.getOrElse(s.name, new CumulativeStatistic(s.name))
				    cumStat.addStatistic(s)
				    weeksResults(s.name) = cumStat
				  })
				  }
				)
				
				weeksResults.foreach( v => println(v._2))
			}
		
	}


	private def calculateResultByMatches(year: Int, league:Int, currentWeek: Int,  m: domain.Match): List[Statistic] = {
		implicit val getMatchResult = GetResult(r => domain.Match(r.nextString, r.nextString, r.nextInt, r.nextInt))

		val startWeek = currentWeek-6
		val matches = new ListBuffer[Match]()
		Database.forURL(dbURL, driver = "com.mysql.jdbc.Driver") withSession {		  
			Q.queryNA[Match]("select home, away, home_goals, away_goals from liga.match where year=" + year + 
					" and week>" + startWeek + " and week<=" + (startWeek+5) + " and league=" + league +
					" and (home='" + m.homeTeam +"' or away = '" + m.homeTeam + "' or home='" + m.awayTeam + "' or away = '" + m.awayTeam + "')" +
					" order by week asc"
			) foreach {m => matches += m }
		}

		val allWinsStatistic = new Statistic("allWins")
		val homeAwayWinsStatistic = new Statistic("homeAwayWins")
		val allGoalsStatistic = new Statistic("allGoals")
		val homeAwayGoalsStatistic = new Statistic("homeAwayGoals")
		var allWins = 0
		var homeAwayWins = 0
		var allGoals = 0
		var homeAwayGoals = 0
		
		for (num <- 0 to (matches.toList.size/2-1)) {
			if (AllWins.calculate(m.homeTeam, m.awayTeam, matches.toList.drop(num)) == m.result) allWins += 1
			if (HomeAwayWins.calculate(m.homeTeam, m.awayTeam, matches.toList.drop(num)) == m.result) homeAwayWins += 1
			if (AllGoals.calculate(m.homeTeam, m.awayTeam, matches.toList.drop(num)) == m.result) allGoals += 1
			if (HomeAwayGoals.calculate(m.homeTeam, m.awayTeam, matches.toList.drop(num)) == m.result) homeAwayGoals += 1
			
			if (num == 0) {
			  allWinsStatistic.setSingle(allWins)
			  homeAwayWinsStatistic.setSingle(homeAwayWins)
			  allGoalsStatistic.setSingle(allGoals)
			  homeAwayGoalsStatistic.setSingle(homeAwayGoals)
			}
		}
		allWinsStatistic.setAggregate(allWins)
		homeAwayWinsStatistic.setAggregate(homeAwayWins)
		allGoalsStatistic.setAggregate(allGoals)
		homeAwayGoalsStatistic.setAggregate(homeAwayGoals)

		allWinsStatistic :: homeAwayWinsStatistic :: allGoalsStatistic :: homeAwayGoalsStatistic :: Nil
	}
	
	
	private def calculateResultByPoints(year: Int, league:Int, currentWeek: Int,  m: domain.Match): List[Statistic] = {
		implicit val getPointsResult = GetResult(r => domain.Team(r.nextString, r.nextInt))

		val startWeek = currentWeek-6 
		val points = new ListBuffer[Team]()
		Database.forURL(dbURL, driver = "com.mysql.jdbc.Driver") withSession {		  
			Q.queryNA[Team]("select team, points from liga.standing where year=" + year + 
					" and week>" + startWeek + " and week<=" + (startWeek+5) + " and league=" + league +
					" and (team='" + m.homeTeam +"' or team = '" + m.awayTeam + "')" +
					" order by week asc"
			) foreach {t => points += t }
		}

		val deltaRatio = 0.25
		val deltaDifference = 5
		
		var correctRatio = 0
		var correctDifference = 0
		val ratioStatistic = new Statistic("pointsPredictorRatio")
		val differenceStatistic = new Statistic("pointsPredictorDifference")
		
		for (num <- 0 to (points.toList.size/2-1)) {
			val homeTeamList = points.toList.filter(t => t == m.homeTeam).drop(num)
			val awayTeamList = points.toList.filter(t => t == m.awayTeam).drop(num)
			if (PointsPredictorRatio.calculate(homeTeamList, awayTeamList, deltaRatio) == m.result) correctRatio += 1
			if (PointsPredictorDifference.calculate(homeTeamList, awayTeamList, deltaDifference) == m.result) correctDifference += 1
			if (num == 0) {
			  ratioStatistic.setSingle(correctRatio)
			  differenceStatistic.setSingle(correctDifference)
			}
		}
		ratioStatistic.setAggregate(correctRatio)
		differenceStatistic.setAggregate(correctDifference)
		
		ratioStatistic :: differenceStatistic :: Nil
		
	}	
}