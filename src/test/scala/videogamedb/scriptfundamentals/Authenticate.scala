package videogamedb.scriptfundamentals

import io.gatling.core.Predef._
import io.gatling.core.structure.ChainBuilder
import io.gatling.http.Predef._

class Authenticate extends Simulation{
  val httpProtocol = http.baseUrl("https://videogamedb.uk/api")
    .acceptHeader("application/json")
    .contentTypeHeader("application/json")

  def authenticate = {
    exec(http("Authenticate")
    .post("/authenticate")
    .body(StringBody("{\n  \"password\": \"admin\",\n  \"username\": \"admin\"\n}"))
    .check(jsonPath("$.token").saveAs("jwtToken")))
  }

  def createNewGame(): ChainBuilder = {
    exec(http("Create new game")
    .post("/videogame")
      .header("Authorization", "Bearer #{jwtToken}")
    .body(StringBody("{\n  \"category\": \"Platform\",\n  \"name\": \"Mario\",\n  \"rating\": \"Mature\",\n  \"releaseDate\": \"2012-05-04\",\n  \"reviewScore\": 85\n}"))
    .check(status.in(200 to 210)))
  }

  val scn = scenario("Authentication")
    .exec(authenticate)
    .exec(createNewGame())


  setUp(
    scn.inject(atOnceUsers(1))
  ).protocols(httpProtocol)

}
