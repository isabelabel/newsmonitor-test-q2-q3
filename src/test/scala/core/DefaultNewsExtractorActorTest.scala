package core

import akka.actor.Props
import akka.testkit.{TestActorRef, TestProbe}
import core.CrawlerCoreMessages.NewsNodeExtracted
import core.NewsExtractorMessages.ExtractArticle
import org.jsoup.Jsoup
import util.ResourceReader

class DefaultNewsExtractorActorTest extends BaseTest {
  private val html = ResourceReader.read("/news1_tests.html")
  private val defaultArticleExtractor = TestActorRef(Props[DefaultNewsExtractorActor])

  it should "send back the HTML of the most likely news node that is 'div.b'" in {
    val nodeB = Jsoup.parse(html).select("div.b").first

    val prob = TestProbe()

    prob.send(defaultArticleExtractor, ExtractArticle(html, "http://terra.com.br"))
    prob.expectMsg(NewsNodeExtracted(nodeB, html, "http://terra.com.br"))
  }

  it should "return None when it's not possible to finder the news" in {
    val nodeB = Jsoup.parse(html).select("div.b").first

    val prob = TestProbe()

    prob.send(defaultArticleExtractor, ExtractArticle("invalid", "http://terra.com.br"))
    prob.expectNoMsg()
  }
}
