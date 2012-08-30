package domain

class Statistic(n: String) {
  val name = n
  var single = 0
  var aggregate = 0
  var numAggregateResults = 0
  

  def addResult(a: Boolean) { 
    if (a) {
      aggregate += 1
 	  if (numAggregateResults == 0) single = 1
    }
    numAggregateResults += 1
   }
  
  override def toString: String = { name + "\t" + single + "\t" + aggregate } 
}