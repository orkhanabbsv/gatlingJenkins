package videogamedb.scriptfundamentals

import io.gatling.core.Predef._
import io.gatling.http.Predef._

import scala.concurrent.duration.DurationInt

class AddPauseTime extends Simulation {

  val httpProtocol = http.baseUrl("https://videogamedb.uk/api")

  val scn = scenario("Video Game Db - 3 calls")

    .exec(http("Get All video games")
      .get("/videogame"))
    .pause(5)

    .exec(http("Get the first game")
      .get("/videogame/1"))
    .pause(1, 10)

    .exec(http("Get all video games - 2nd call")
      .get("/videogame"))
    .pause(3000.milliseconds)

  setUp(
    scn.inject(atOnceUsers(1))
  ).protocols(httpProtocol)
}
