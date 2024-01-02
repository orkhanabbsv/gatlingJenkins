package videogamedb.simulations

import io.gatling.core.Predef._
import io.gatling.http.Predef._

class RampLoadSimulation extends Simulation {
  val httpProtocol = http.baseUrl("https://videogamedb.uk/api")
    .acceptHeader("application/json")

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
    .exec(getAllGames)
    .pause(1)
    .exec(getSpecificGame)
    .pause(2)
    .exec(getAllGames)

  setUp(
    scn.inject(
      constantConcurrentUsers(100).during(10)
    )
  ).protocols(httpProtocol)
}