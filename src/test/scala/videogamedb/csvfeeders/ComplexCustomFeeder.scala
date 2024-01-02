package videogamedb.csvfeeders

import io.gatling.core.Predef._
import io.gatling.core.structure.ChainBuilder
import io.gatling.http.Predef._

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.regex.Pattern
import scala.util.Random

class ComplexCustomFeeder extends Simulation {
  val httpProtocol = http.baseUrl("https://videogamedb.uk/api")
    .acceptHeader("application/json")
    .contentTypeHeader("application/json")

  val rnd = new Random()

  val idNumbers = (1 to 10).iterator

  def randomString(length: Int) = {
    rnd.alphanumeric.filter(_.isLetter).take(length).mkString
  }

  val now = LocalDate.now()
  val pattern = DateTimeFormatter.ofPattern("yyyy-MM-dd")

  def getRandomDate(startDate: LocalDate, random: Random): Unit = {
    startDate.minusDays(random.nextInt(30))
  }

  val customFeeder = Iterator.continually(Map(
    "gameId" -> idNumbers.next(),
    "name" -> ("Game-" + randomString(5)),
    "releaseDate" -> getRandomDate(now, rnd),
    "reviewScore" -> rnd.nextInt(100),
    "category" -> ("Category-" + randomString(5)),
    "rating" -> ("Rating-" + randomString(4))
  ))

  def authenticate = {
    exec(http("Authenticate")
      .post("/authenticate")
      .body(StringBody("{\n  \"password\": \"admin\",\n  \"username\": \"admin\"\n}"))
      .check(jsonPath("$.token").saveAs("jwtToken")))
  }

  def createNewGame(): ChainBuilder = {
    repeat(10) {
      feed(customFeeder)
        .exec(http("CreateNewGame with name - #{name}")
          .post("/videogame")
          .header("Authorization", "Bearer #{jwtToken}")
          .body(ElFileBody("bodies/newGameTemplate.json")).asJson
          .check(status.is(200))
          .check(bodyString.saveAs("responseBody")))
        .exec { session => println(session("responseBody").as[String]); session }
        .pause(2)
    }
  }

  val scn = scenario("Complex Custom Feeder")
    .exec(authenticate)
    .exec(createNewGame())

  setUp(
    scn.inject(atOnceUsers(1))
  ).protocols(httpProtocol)
}
