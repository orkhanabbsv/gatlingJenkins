package videogamedb.csvfeeders

import io.gatling.core.Predef._
import io.gatling.core.structure.ChainBuilder
import io.gatling.http.Predef._

class CustomFeeder extends Simulation {
  val httpProtocol = http.baseUrl("https://videogamedb.uk/api")
    .acceptHeader("application/json")

  val idNumber = (1 to 10).iterator

  val customFeeder = Iterator.continually(Map("gameId" -> idNumber.next()))

  def getSpecificGame(): ChainBuilder = {
    repeat(10) {
      feed(customFeeder)
        .exec(http("Get specific game with id - #{gameId}")
          .get("/videogame/#{gameId}")
          .check(status.is(200)))
        .pause(1)
    }
  }

  val scn = scenario("Custom feeder")
    .exec(getSpecificGame())

  setUp(
    scn.inject(atOnceUsers(1))
  ).protocols(httpProtocol)
}
