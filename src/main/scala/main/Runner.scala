package main

import scala.collection.mutable.ListBuffer
import scala.collection.mutable.Map
import com.typesafe.config.ConfigFactory
import domain.CumulativeStatistic
import domain.Match
import domain.Statistic
import domain.Team
import domain.indicators.AllGoals
import domain.indicators.AllWins
import domain.indicators.HomeAwayGoals
import domain.indicators.HomeAwayWins
import domain.indicators.PointsPredictorDifference
import domain.indicators.PointsPredictorRatio
import domain.indicators.Configuration
import akka.actor.ActorSystem
import akka.actor.Props
import domain.indicators.actors.AllWinsActor
import scala.concurrent.Await
import akka.pattern.ask
import akka.util.Timeout
import scala.concurrent.util.duration._
import scala.concurrent.Await


object Runner {

  implicit val timeout = Timeout(5 seconds) // needed for `?` below
  
	var conf = ConfigFactory.load
	lazy val dbURL = conf.getString("db.url")

	val loader = new sql.Loader(dbURL)

	def main(args: Array[String]): Unit = {
		loader.testDatabase
		//run
		runActors()
		println("ending main")
	}

	def runActors() {
		val system = ActorSystem("MySystem")
		val allWinsActor = system.actorOf(Props[AllWinsActor], name = "allWinsActor")

		val future = allWinsActor ? "start"
		val result = Await.result(future, timeout.duration).asInstanceOf[Configuration]
		println("Best allWins: " + result)
		
		
		system shutdown
		
	}


	def run() {


	  val allWinsConfig = new Configuration(0.8,0.5,0.6)
	  val homeAwayWinsConfig = new Configuration(0.8,0.5,0.6)
	  val allGoalsConfig = new Configuration(0.8,0.5,0.6)
	  val homeAwayGoalsConfig = new Configuration(0.8,0.5,0.6)
	  
		val year = 2009
		for (league <- 1 to 2) {
			println("LEAGUE: " + league)
			val fullResults = Map[String, ListBuffer[CumulativeStatistic]]()
			for (week <- 6 to 38) {
				val matches = loader.getMatches(year, week: Int, league)
				val weeksResults = Map[String, CumulativeStatistic]()

				
				matches.toList.foreach(m => {
					val matches = loader.getListPreviousMatches(year, league, week, m)  
					val points = loader.getListPoints(year, league, week, m)  
					addStat(AllWins(allWinsConfig).run(matches, m), weeksResults)
					addStat(HomeAwayWins(homeAwayWinsConfig).run(matches, m), weeksResults)
					addStat(AllGoals(allGoalsConfig).run(matches, m), weeksResults)
					addStat(HomeAwayGoals(homeAwayGoalsConfig).run(matches, m), weeksResults)
					addStat(PointsPredictorRatio("pointsPredictorRatio", 0.25).run(points, m), weeksResults)
					addStat(PointsPredictorDifference("pointsPredictorDifference", 5).run(points.toList, m), weeksResults)
				})
				

				weeksResults.foreach(v => {
					val cumStat = v._2
					val fullStat = fullResults.getOrElse(cumStat.name, new ListBuffer[CumulativeStatistic])
					fullStat += cumStat
					fullResults(cumStat.name) = fullStat
				})

			}

			fullResults.foreach(fr => calcAverageStatAllWeeks(fr._2))
		}
	}

	private def calcAverageStatAllWeeks(stats: ListBuffer[CumulativeStatistic]) = {
		val sumSingle = stats.toList.foldLeft(0.0)((c,r) => c + r.singleSuccess) / stats.size
		val sumCumul = stats.toList.foldLeft(0.0)((c,r) => c + r.cumulativeSuccess) / stats.size
		println(pad(stats(0).name) + "\t" + round(sumSingle) + "\t" + round(sumCumul))
	}

	private def addStat(stat: Statistic, weeksResults: Map[String, CumulativeStatistic]) = {
		val cumStat = weeksResults.getOrElse(stat.name, new CumulativeStatistic(stat.name))
		cumStat.addStatistic(stat)
		weeksResults(stat.name) = cumStat
	}
	
  def pad(s: String): String = {
	  val padding = " " * (30 - s.size)
	  s+padding
  }
  
  
  def round(x: Double): String = "%.2f".format(x).replace('.', ',')	
  

}