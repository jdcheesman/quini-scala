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


object Runner {

	var conf = ConfigFactory.load
	lazy val dbURL = conf.getString("db.url")
	
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
	  
	  implicit def strToTeam(x: String) = new Team(x)
	  implicit val getMatchResult = GetResult(r => domain.Match(r.nextString, r.nextString,
			  r.nextInt, r.nextInt))



	  Database.forURL(dbURL, driver = "com.mysql.jdbc.Driver") withSession {
	    val year = 2010
	    
	    val week = new Week(2010, 11)
	    (new Match("Athletic", "Almera",0,0) ::
		new Match("Atltico", "Osasuna",0,0) ::
		new Match("Barcelona", "Villarreal",0,0) ::
		new Match("Zaragoza", "Sevilla",0,0) ::
		new Match("Hrcules", "R Sociedad",0,0) ::
		new Match("Racing", "Espanyol",0,0) ::
		new Match("Mlaga", "Levante",0,0) ::
		new Match("Mallorca", "Deportivo",0,0) ::
		new Match("Sporting", "Real Madrid",0,0) ::
		new Match("Valencia", "Getafe",0,0) :: Nil).foreach(m => {
		  
	    	val matches = new ListBuffer[Match]()
	    	Q.queryNA[Match]("select home, away, home_goals, away_goals from liga.match where year=" + year + 
	    	    " and week>5 and week<11 and league=1 and (home='" + m.homeTeam +"' or away = '" + m.homeTeam + "' or home='" + m.awayTeam + "' or away = '" + m.awayTeam + "')" +
	    	    " order by week asc"
	    	    ) foreach {m => matches += m }
		
		    for (num <- 0 to (matches.toList.size-1)) {
		    	print(m.homeTeam + "\t")
		    	print(m.awayTeam + "\t")
			    print(num + "\t")
			    print(AllWins.calculate(m.homeTeam, m.awayTeam, matches.toList.drop(num)).asQuiniela + "\t")
			    print(HomeAwayWins.calculate(m.homeTeam, m.awayTeam, matches.toList.drop(num)).asQuiniela + "\t")
			    print(AllGoals.calculate(m.homeTeam, m.awayTeam, matches.toList.drop(num)).asQuiniela + "\t")
			    print(HomeAwayGoals.calculate(m.homeTeam, m.awayTeam, matches.toList.drop(num)).asQuiniela + "\n")
		    }
	    })
	    
//	    weeks.foreach(f => println(f))
	    
	    
	  
  }
}



}