#!/usr/bin/ruby -w
# simple.rb - simple MySQL script using Ruby MySQL module

require "mysql"

begin
 years = [2008, 2009, 2010, 2011]

 years.each do |year|

 # connect to the MySQL server
 dbh = Mysql.real_connect("localhost", "root", "fatpera", "liga")


 res = dbh.query("SELECT home, away, home_goals, away_goals, week, league FROM liga.match where year=2010 order by week, league")

 results = Hash[]

  while row = res.fetch_row do
    homeTeam = row[0]
    awayTeam = row[1]
    home_goals = row[2]
    away_goals = row[3]
    if (results[homeTeam] == nil)
      results[homeTeam] = 0
    end
    if (results[awayTeam] == nil)
      results[awayTeam] = 0
    end

    if (home_goals > away_goals)
        results[homeTeam] += 3
    elsif (home_goals < away_goals)
        results[awayTeam] += 3
    else
        results[homeTeam] += 1
        results[awayTeam] += 1
    end


    printf("INSERT INTO standing (year, week, league, team, points) VALUES (" + year.to_s +
      ", "  + row[4].to_s + ", "  + row[5].to_s + ", '"  + homeTeam + "', "  + results[homeTeam].to_s + ");\n")
    printf("INSERT INTO standing (year, week, league, team, points) VALUES (" + year.to_s +
      ", "  + row[4].to_s + ", "  + row[5].to_s + ", '"  + awayTeam + "', "  + results[awayTeam].to_s + ");\n")
  end
  res.free

dbh.close if dbh

end

rescue Mysql::Error => e
 puts "Error code: #{e.errno}"
 puts "Error message: #{e.error}"
 puts "Error SQLSTATE: #{e.sqlstate}" if e.respond_to?("sqlstate")
ensure
 # disconnect from server
 #dbh.close if dbh
end
