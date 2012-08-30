package domain

import scala.collection.mutable.ListBuffer

class CumulativeStatistic(name: String) {
  val stats: ListBuffer[Statistic] = new ListBuffer[Statistic]

  def addStatistic(toAdd: Statistic) = {stats += toAdd}
  
  def name(): String = name
  
  def getStats() = stats.reverse
  
  def singleSuccess: Double = (stats.foldLeft(0)((c,s) => c + s.single ) * 1.0) / stats.size
  
  // following line assumes all the elements in the stats array have same number of aggregate results.
  def cumulativeSuccess: Double = (stats.foldLeft(0)((c,s) => c + s.aggregate ) * 1.0) / (stats.size*stats(0).numAggregateResults) 
  
  //def cumulativeSuccess: Double = (stats.foldLeft(0)((c,s) => c + s.aggregate ) * 1.0) / (stats.size*5) 
  
  override def toString = name + "\t" + singleSuccess + "\t" + cumulativeSuccess 

}