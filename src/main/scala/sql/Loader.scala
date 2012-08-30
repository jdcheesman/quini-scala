package sql

import scala.collection.mutable.ListBuffer
import scala.slick.jdbc.GetResult
import scala.slick.jdbc.{StaticQuery => Q}
import scala.slick.session.Database
import scala.slick.session.Database.threadLocalSession

import domain.Match
import domain.Team

class Loader(dbURL: String) {
  
  implicit def strToTeam(x: String) = new Team(x)

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
	}  
  
	def getListPoints(year: Int, league:Int, currentWeek: Int,  m: domain.Match): List[Team] = {
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
		
		points.toList
	}	
	
	
	
  def getListPreviousMatches(year: Int, league: Int, currentWeek: Int, m: domain.Match): List[Match] = {
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
		
		matches.toList
	}
  
  
	def getMatches(year: Int, week: Int, league: Int): List[Match] = {
				val matches = new ListBuffer[Match]()
				implicit val getMatchResult = GetResult(r => domain.Match(r.nextString, r.nextString, r.nextInt, r.nextInt))
				Database.forURL(dbURL, driver = "com.mysql.jdbc.Driver") withSession {		  
					Q.queryNA[Match](
							"select home, away, home_goals, away_goals from liga.match where year=" + year + " and week=" + week + " and league=" + league
							) foreach {m => matches += m }
				}	  
				
				matches.toList
	}
}