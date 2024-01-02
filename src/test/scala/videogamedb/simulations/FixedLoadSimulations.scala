package videogamedb.simulations

import io.gatling.core.Predef._
import io.gatling.http.Predef._

class FixedLoadSimulations extends Simulation {
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

  val scn = scenario("Base Load Simulation")
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
      atOnceUsers(5),
      rampUsers(10).during(4),
    ).protocols(httpProtocol)
  ).maxDuration(20)
}
