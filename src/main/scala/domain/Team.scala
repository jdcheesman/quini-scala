package domain

class Team(n: String) {
  val name = n
  
	def == (other: Team): Boolean = (other.name == name) 
}