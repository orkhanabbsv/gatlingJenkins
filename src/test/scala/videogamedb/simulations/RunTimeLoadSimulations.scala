package videogamedb.simulations

import io.gatling.core.Predef._
import io.gatling.http.Predef._

class RunTimeLoadSimulations extends Simulation {
  val httpProtocol = http.baseUrl("https://videogamedb.uk/api")
    .acceptHeader("application/json")

  def USERCOUNT: Int = System.getProperty("USERS", "10").toInt
  def RAMPUSERS: Int = System.getProperty("RAMP", "20").toInt
  def MAXDURATION: Int = System.getProperty("DURATION", "30").toInt

  before{
    println(s"USERS COUNT ${USERCOUNT}")
    println(s"Ramp users  count ${RAMPUSERS}")
    println(s"Max duration of the test ${MAXDURATION}")
  }
  def getAllGames = {
    exec(http("get all games")
      .get("/videogame")
      .check(status.is(200)))
  }

  def getSpecificGame = {
    exec(http("Get specific game")
      .get("/videogame/1")
      .check(status.is(200)))
  }

  val scn = scenario("Ramp Load Simulation")
    .forever {
      exec(getAllGames)
        .pause(1)
        .exec(getSpecificGame)
        .pause(2)
        .exec(getAllGames)
    }


  setUp(
      scn.inject(
        nothingFor(4),
        atOnceUsers(USERCOUNT),
        rampUsers(RAMPUSERS).during(4)
    ).protocols(httpProtocol)
  ).maxDuration(MAXDURATION)
}