package videogamedb.csvfeeders

import io.gatling.core.Predef._
import io.gatling.core.structure.ChainBuilder
import io.gatling.http.Predef._

class CSVFeeder extends Simulation {
    val httpProtocol = http.baseUrl("https://videogamedb.uk/api")
      .acceptHeader("application/json")

  val feeder = csv("data/gameCSVFile.csv").circular

  def getSpecificGame(): ChainBuilder = {
    repeat(10) {
      feed(feeder)
        .exec(http("GetSpecificGame with name - #{gameName}")
          .get("/videogame/#{id}")
          .check(jsonPath("$.name").is("#{gameName}"))
          .check(status.is(200)))
        .pause(1)
    }
  }

  val scn = scenario("Get specific game")
    .exec(getSpecificGame())

  setUp(
    scn.inject(atOnceUsers(1))
  ).protocols(httpProtocol)
}
