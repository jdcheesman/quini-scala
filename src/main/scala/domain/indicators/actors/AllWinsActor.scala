package domain.indicators.actors


import akka.actor._
import akka.actor.Actor
import akka.actor.Props
import akka.event.Logging
import domain.indicators.Configuration
import sql.Loader
import domain.CumulativeStatistic
import domain.indicators.AllWins
import com.typesafe.config.ConfigFactory
 
class AllWinsActor() extends Actor {
  
    val log = Logging(context.system, this)
    var conf = ConfigFactory.load
	lazy val dbURL = conf.getString("db.url")
	val loader = new sql.Loader(dbURL)
    
    def receive = {
      case "start" => sender ! run()
      case _ => log.error("unknown message")
    }

	
  
	def run(): Configuration = {
		var result = new Configuration(1.0,1.0,1.0)
		var bestResult = 0.0
		
		val year = 2009
		for (deltaA <- 1.0 to 0.0 by -0.1) {
			print ("deltaA: " + deltaA)
			for (deltaB <- 1.0 to 0.0 by -0.1) {
				print ("deltaB: " + deltaB)
				for (deltaC <- 1.0 to 0.0 by -0.1) {
					print ("deltaC: " + deltaC)
					val cumStat = new CumulativeStatistic("allWins")
					val allWinsConfig = new Configuration(deltaA, deltaB, deltaC)
					for (league <- 1 to 2) {
						println("LEAGUE: " + league)
					
						for (week <- 6 to 38) {
							val matches = loader.getMatches(year, week: Int, league)
							matches.toList.foreach(m => {
								val matches = loader.getListPreviousMatches(year, league, week, m)  
								val points = loader.getListPoints(year, league, week, m)  
								cumStat.addStatistic(AllWins(allWinsConfig).run(matches, m))
							})
						}
					}
					if (cumStat.singleSuccess > bestResult) {
					  result = new Configuration(deltaA, deltaB, deltaC)
					  bestResult = cumStat.singleSuccess
					  log.info("new best=" + bestResult + ", config=" + result)
					}
				}
			}
		}
		result
	}
}