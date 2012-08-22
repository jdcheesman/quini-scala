package test.sql

import com.twitter.querulous.evaluator.QueryEvaluator

object SQLTest {

  def main(args: Array[String]): Unit = {
    val queryEvaluator = QueryEvaluator("host", "username", "password")
    val users = queryEvaluator.select("SELECT * FROM users") { row => 
    	println(row.getInt("id") + "-" + row.getString("name"))
    }
  }

}