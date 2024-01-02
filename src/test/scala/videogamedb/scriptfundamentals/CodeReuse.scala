package videogamedb.scriptfundamentals

import io.gatling.core.Predef._
import io.gatling.core.structure.ChainBuilder
import io.gatling.http.Predef._


class CodeReuse extends Simulation {
  val httpProtocol = http.baseUrl("https://videogamedb.uk/api")

  def getAllVideoGames(): ChainBuilder = {
    repeat(5) {
      exec(http("Get All video game")
        .get("/videogame")
        .check(status.is(200)))
    }
  }

  def getSpecificGame(): ChainBuilder = {
    repeat(5, counterName = "counter") {
      exec(http("Get specific game with id - #{counter}")
        .get("/videogame/#{counter}")
        .check(status.in(200 to 210)))
    }
  }

  val scn = scenario("Get all games")
    .exec(getAllVideoGames())
    .pause(3)
    .exec(getSpecificGame())
    .repeat(3) {
      getAllVideoGames()
    }

  setUp(
    scn.inject(atOnceUsers(1))
  ).protocols(httpProtocol)

}
