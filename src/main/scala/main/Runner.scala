package main

import scala.collection.mutable.ListBuffer
import scala.collection.mutable.Map

import com.typesafe.config.ConfigFactory

import domain.CumulativeStatistic
import domain.Match
import domain.Statistic
import domain.Team
import domain.Week
import domain.indicators.AllGoals
import domain.indicators.AllWins
import domain.indicators.HomeAwayGoals
import domain.indicators.HomeAwayWins
import domain.indicators.PointsPredictorDifference
import domain.indicators.PointsPredictorRatio

object Runner {

	var conf = ConfigFactory.load
	lazy val dbURL = conf.getString("db.url")

	val loader = new sql.Loader(dbURL)

	def main(args: Array[String]): Unit = {
		loader.testDatabase
		run
	}



	def run() {

		val year = 2009
		for (league <- 1 to 1) {
			println("LEAGUE: " + league)
			val fullResults = Map[String, ListBuffer[CumulativeStatistic]]()
			for (w <- 6 to 38) {
				val currentWeek = new Week(year, w)
				//val league = 1

				val matches = loader.getMatches(year, currentWeek.week: Int, league)
				val weeksResults = Map[String, CumulativeStatistic]()

				
				matches.toList.foreach(m => {
					val matches = loader.getListPreviousMatches(year, league, currentWeek.week, m)  
					val points = loader.getListPoints(year, league, currentWeek.week, m)  
					val res = calculateResultByMatches(year, league, currentWeek.week, m, matches) ::: calculateResultByPoints(year, league, currentWeek.week, m, points)
					res.foreach(s => {
						val cumStat = weeksResults.getOrElse(s.name, new CumulativeStatistic(s.name))
						cumStat.addStatistic(s)
						weeksResults(s.name) = cumStat
					})
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

	private def calculateResultByMatches(year: Int, league:Int, currentWeek: Int,  m: domain.Match, matches: List[Match]): List[Statistic] = {
		val allWinsStatistic = AllWins(0.8,0.5,0.6).run(matches, m)
		val homeAwayWinsStatistic = HomeAwayWins(0.8,0.5,0.6).run(matches, m)
		val allGoalsStatistic = AllGoals(0.8,0.5,0.6).run(matches, m)
		val homeAwayGoalsStatistic = HomeAwayGoals(0.8,0.5,0.6).run(matches, m)

		allWinsStatistic :: homeAwayWinsStatistic :: allGoalsStatistic :: homeAwayGoalsStatistic :: Nil
	}



	
	private def calculateResultByPoints(year: Int, league:Int, currentWeek: Int,  m: domain.Match, points: List[Team]): List[Statistic] = {
		val deltaRatio = 0.25
		val ratioStatistic = PointsPredictorRatio("pointsPredictorRatio", deltaRatio).run(points, m)

		val deltaDifference = 5
		val differenceStatistic = PointsPredictorDifference("pointsPredictorDifference", deltaDifference).run(points.toList, m)

		ratioStatistic :: differenceStatistic :: Nil
	}
	
  def pad(s: String): String = {
	  val padding = " " * (30 - s.size)
	  s+padding
  }
  
  
  def round(x: Double): String = "%.2f".format(x).replace('.', ',')	
  

}