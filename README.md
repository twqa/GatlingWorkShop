# Gatling Workshop

Performance test using Gatling 

## Preparation
Download Zip bundle 
1. http://gatling.io/#/resources/download

## Workshop Steps

### Scenario 1
1. 在"stackoverflow" 网站上搜索'gatling'
2. 选择进入一条搜索结果

#### Refer to http://gatling.io/docs/2.0.0-RC2/http/http_request.html#query-parameters
#### Example
GET http://stackoverflow.com/search?q=gatling
GET http://stackoverflow.com/questions/22563517/using-gatling-as-an-integration-test-tool?s=1|3.1610

    exec(http("Search")
          .get("/search")
          .headers(headers_0)
          .queryParam("q", "gatling")
        )
    .pause(1)
    .exec(http("Select")
          .get("/questions/22563517/using-gatling-as-an-integration-test-tool")
          .headers(headers_0)
          .queryParam("s", "1|3.1596")
        )
    .pause(1)


### Scenario 2
1. 在结果列表,跳转到第2页,第3页

#### Refer to http://gatling.io/docs/2.0.0-RC2/http/http_request.html#query-parameters
#### Example
GET http://stackoverflow.com/search?page=2&tab=relevance&q=gatling

    exec(http("Page 2")
        .get("/search")
        .headers(headers_0)
        .queryParam("page", "2")
        .queryParam("tab", "relevance")
        .queryParam("q", "gatling")
        )
        .pause(2)
        .exec(http("Page 3")
          .get("/search")
          .headers(headers_0)
          .queryParam("page", "3")
          .queryParam("tab", "relevance")
          .queryParam("q", "gatling")
        )

### Scenario 3
1. 进入'Documentation'
2. 输入需要寻找的tag
3. 进入具体的主题页面

#### Refer to 
http://gatling.io/docs/2.0.0-RC2/http/http_request.html#query-parameters
http://gatling.io/docs/2.0.0-RC2/http/http_request.html#post-parameters

#### Example
GET  http://stackoverflow.com/documentation
POST http://stackoverflow.com/documentation/filter/submit
GET  http://stackoverflow.com/documentation/css/topics

    exec(http("Documentations")
        .get("/documentation")
        .headers(headers_0)
      )
        .pause(2)
        .exec(http("Tag")
          .post("/documentation/filter/submit")
          .headers(headers_1)
          .formParam("filter", "CSS")
          .formParam("fkey", "f757bad658420c9ff22bd7a93654132c")
          .formParam("tab", "popular")
          .check(status.is(200))
        )
        .pause(2)
        .exec(http("Topic")
          .get("/documentation/css/topics")
          .headers(headers_0)
        )


### Scenario 4
1. 用户注入:5秒内平滑注入10个用户

#### Refer to http://gatling.io/docs/2.0.0-RC2/general/simulation_setup.html
#### Example

    setUp(scn.inject(rampUsers(5) over (5 seconds))).protocols(httpProtocol)


### Scenario 5
1. 将三个scenario封装成object
2. 定义三类虚拟用户进行不同操作:userA-search&browse,userB-documentation,userC-search&browse&documentation
3. 使用三种方法丰富用户注入
   - userA: rampUsers(nbUsers) over(duration)
   - userB: atOnceUsers(nbUsers)
   - userC: constantUsersPerSec(rate) during(duration)

#### Refer to
http://gatling.io/docs/2.0.0-RC2/advanced_tutorial.html#step-01-isolate-processes
#### Example

    //Search
    object Search {
        val search =
          exec(http("Home")
            .get("/")
            .headers(headers_0))
            .pause(1)
            .exec(http("Search")
              .get("/search")
              .headers(headers_0)
              .queryParam("q", "gatling"))
            .pause(1)
            .exec(http("Select")
              .get("/questions/22563517/using-gatling-as-an-integration-test-tool")
              .headers(headers_0)
              .queryParam("s", "1|3.1596"))
            .pause(1)
      }
      
      //Browse
      object Browse {
         
         val browse =
            exec(http("Page 2")
               .get("/search")
               .headers(headers_0)
               .queryParam("page", "2")
               .queryParam("tab", "relevance")
               .queryParam("q", "gatling")
            )
            .pause(2)
            .exec(http("Page 3")
               .get("/search")
               .headers(headers_0)
               .queryParam("page", "3")
               .queryParam("tab", "relevance")
               .queryParam("q", "gatling")
            )
        }
        
        //Documentation
        object Documentations {
        
            val documentation =
              exec(http("Documentation")
                .get("/documentation")
                .headers(headers_0))
                .pause(1)
                .exec(http("Tag")
                  .post("/documentation/filter/submit")
                  .headers(headers_1)
                  .formParam("filter", "CSS")
                  .formParam("fkey", "f757bad658420c9ff22bd7a93654132c")
                  .formParam("tab", "popular")
                )
                .pause(1)
                .exec(http("Topics")
                  .get("/documentation/css/topics")
                  .headers(headers_0))
          }
          
        val userA = scenario("searchChannel").exec(Search.search, Browse.browse)
        val userB = scenario("documentationsChannel").exec(Documentations.documentation)
        val userC = scenario("allChannel").exec(Search.search, Browse.browse, Documentations.documentation)
        
        setUp(
            userA.inject(rampUsers(5) over (5 seconds)),
            userB.inject(atOnceUsers(10)),
            userC.inject(constantUsersPerSec(5) during (2 seconds))
          ).protocols(httpProtocol)
          


### Scenario 6
1. 使用feeder中的csv动态传递参数
2. 使用check抓取并保存进入Documentation主题页面的URL,检查请求状态为200
3. 访问保存的URL进入指定页面

#### Refer to
http://gatling.io/docs/2.0.0-RC2/advanced_tutorial.html#step-03-use-dynamic-data-with-feeders-and-checks
http://gatling.io/docs/2.0.0-RC2/session/feeder.html#feeder
http://gatling.io/docs/2.0.0-RC2/http/http_check.html#http-response-body
http://gatling.io/docs/2.0.0-RC2/http/http_check.html#saving

#### Example

    val feeder = csv("keyWords.csv").circular
    //Search
    object Search {
        val search =
          exec(http("Home")
            .get("/")
            .headers(headers_0))
            .pause(1)
            .feed(feeder)
            .exec(http("Search")
              .get("/search")
              .headers(headers_0)
              .queryParam("q", "${searchCriterion}")
            )
            .pause(1)
            .exec(http("Select")
              .get("/questions/22563517/using-gatling-as-an-integration-test-tool")
              .headers(headers_0)
              .queryParam("s", "1|3.1596"))
            .pause(1)
      }
      
      //Browse
      object Browse {
         
         val browse =
            feed(feeder)
                 .exec(http("Page 2")
                   .get("/search")
                   .headers(headers_0)
                   .queryParam("page", "2")
                   .queryParam("tab", "${searchTabName}")
                   .queryParam("q", "${searchCriterion}")
                 )
                 .pause(2)
                 .exec(http("Page 3")
                   .get("/search")
                   .headers(headers_0)
                   .queryParam("page", "3")
                   .queryParam("tab", "${searchTabName}")
                   .queryParam("q", "${searchCriterion}")
                 )
        }
        
        //Documentation
        object Documentations {
        
            val documentation =
              exec(http("Documentation")
                .get("/documentation")
                .headers(headers_0))
                .pause(1)
                .feed(feeder)
                .exec(http("Tag")
                  .post("/documentation/filter/submit")
                  .headers(headers_1)
                  .formParam("filter", "${documentationCriterion}")
                  .formParam("fkey", "f757bad658420c9ff22bd7a93654132c")
                  .formParam("tab", "${documentationTabName}")
                  .check(css("a:contains('${documentationName}')", "href").saveAs("topicsURL"))
                  .check(status.is(200)))
                .pause(1)
                .exec(http("Topics")
                  .get("${topicsURL}")
                  .headers(headers_0))
          }
          
         

### Scenario 7
1. 使用repeat简化翻页

#### Refer to 
http://gatling.io/docs/2.0.0-RC2/advanced_tutorial.html#step-04-looping
http://gatling.io/docs/2.0.0-RC2/general/scenario.html#repeat

#### Example

      object Browse {
        //简单方法封装
        def gotoPage(page: Int) =
          feed(feeder)
            .exec(http("Page " + page)
              .get("/search")
              .headers(headers_0)
              .queryParam("page", page)
              .queryParam("tab", "${searchTabName}")
              .queryParam("q", "${searchCriterion}"))
            .pause(1)
        val browse = exec(gotoPage(2), gotoPage(3))
        
        //repeat method (从0页开始)
        val browse = repeat(2, "i") {
            exec(http("Page" + "${i}")
               .get("/search")
               .headers(headers_0)
               .queryParam("page", "${i}")
               .queryParam("tab", "relevance")
               .queryParam("q", "gatling"))
            .pause(1)
        }
      }



