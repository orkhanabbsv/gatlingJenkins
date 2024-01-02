package videogamedb

import io.gatling.core.Predef._
import io.gatling.http.Predef._

class VideoGameDbSimulation extends Simulation{

  val httpProtocol = http.baseUrl("https://videogamedb.uk/api/")
    .acceptHeader("application/json")

  val scn = scenario("My first test")
    .exec(http("Get First Name")
    .get("/videogame"))

  setUp(
    scn.inject(atOnceUsers(1))
      .protocols(httpProtocol)
  )
}
