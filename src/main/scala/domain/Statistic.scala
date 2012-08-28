package domain

class Statistic(n: String) {
  val name = n
  var single = 0
  var aggregate = 0
  

  
  def setSingle(s: Int) { single = s}
  def setAggregate(a: Int) { aggregate  = a}
  
  override def toString: String = { name + "\t" + single + "\t" + aggregate } 
}