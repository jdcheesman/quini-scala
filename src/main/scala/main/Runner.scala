package main

import scala.slick.session.Database
import Database.threadLocalSession
import scala.slick.jdbc.{GetResult, StaticQuery => Q}
import Q.interpolation

import domain.Match
import domain.Team


import java.sql.{DriverManager, Connection}

object Runner {

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



	  Database.forURL("jdbc:mysql://127.0.0.1:3306/liga?user=root&password=ginormous", driver = "com.mysql.jdbc.Driver") withSession {
	  Q.queryNA[Match]("select home, away, home_goals, away_goals from liga.match where year=2010 and week=1 and league=1") foreach { println }

  }
}



}