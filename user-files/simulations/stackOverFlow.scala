package simulations


import scala.concurrent.duration._

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.jdbc.Predef._

class stackOverFlow extends Simulation {

  val httpProtocol = http
    .baseURL("http://stackoverflow.com/")
    .inferHtmlResources(BlackList(""".*\.js.*""", """.*\.css.*""", """.*\.jpg.*""", """.*\.png.*""", """.*\.ico.*""", """.*\.net.*""", """.*\.jpeg.*""", """.*\.gif.*""",""".*\?s=.*"""), WhiteList())
    .acceptHeader("*/*")
    .acceptEncodingHeader("gzip, deflate, sdch")
    .acceptLanguageHeader("en-US,en;q=0.8,zh-CN;q=0.6,zh;q=0.4")
    .userAgentHeader("Mozilla/5.0 (Macintosh; Intel Mac OS X 10.10; rv:45.0) Gecko/20100101 Firefox/45.0")

  val headers_0 = Map(
    "Accept" -> "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8",
    "Upgrade-Insecure-Requests" -> "1")

  val headers_1 = Map(
    "Content-Type" -> "application/x-www-form-urlencoded",
    "X-Requested-With" -> "XMLHttpRequest",
    "Accept" -> "text/html, */*; q=0.01")

  val scn = scenario("stackFlowDemo")
    //Home
    .exec(http("homepage")
    .get("/")
    .headers(headers_0)
  )
    //Search

    //Pages


    //Documentations

    //setUp
}