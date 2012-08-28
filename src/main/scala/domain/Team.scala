package domain




case class Team(n: String, p: Int) {
  val name = n
  val points = p
  
  def this(n: String) = this(n, 0) 
    
  
  def == (other: Team): Boolean = (other.name == name)

  def debug() = { name + "\t" + points }
	
	override def toString = {
	  name
	}

  

}