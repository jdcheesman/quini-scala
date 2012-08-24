package domain

import scala.collection.mutable.ListBuffer


class Week(yr: Int, wk: Int) {
  val year = yr
  val week = wk
  val matches = new ListBuffer[Match]()
  
  def addMatch(footballMatch: Match) = {
	  matches += footballMatch
  }
  
  implicit def weekToList(x: Week) = matches.toList
  
  //def getMatches(): List[Match] = matches.toList
  
  override def toString(): String = {
    var result = "**************" + year + "/" + week + "**************\n"
    matches.foreach(m => result += m + "\n")
    result
  }

}