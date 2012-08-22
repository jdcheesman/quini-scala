package domain

class Team(n: String) {
  val name = n
  
	def == (other: Team): Boolean = (other.name == name)
	
	override def toString = {
	  val spaces = " " * (15 - name.length)
	  name + spaces
    }
}