package core

import akka.actor.Props
import akka.testkit.{TestActorRef, TestProbe}
import core.CrawlerCoreMessages.LinksExtracted
import core.LinkExtractorMessages.ExtractLinks

class DefaultLinkExtractorActorTest extends BaseTest {
  private val basicLinkExtractor = TestActorRef(Props[DefaultLinkExtractorActor])

  it should "return all the links without duplicated and origin. Should not remove other domains" in {
    val prob = TestProbe()

    prob.send(basicLinkExtractor, ExtractLinks(origin="http://g1.globo.com", html=htmlSample))
    prob.expectMsg(LinksExtracted(List("http://g1.globo.com/esportes", "http://g1.globo.com/cultura",  "http://globo.com/n1", "http://google.com", "http://globo.com/n2")))
  }

  it should "return all the links without duplicated, origin and should not return not allowed domain" in {
    val prob = TestProbe()

    prob.send(basicLinkExtractor, ExtractLinks(List("http://g1.globo", "http://globo.com"), origin="http://g1.globo.com", html=htmlSample))
    prob.expectMsg(LinksExtracted(List("http://g1.globo.com/esportes", "http://g1.globo.com/cultura",  "http://globo.com/n1", "http://globo.com/n2")))
  }
}
