package core

import akka.actor.{Actor, Props}
import akka.testkit.{TestActorRef, TestProbe}
import core.CrawlerCoreMessages.{HTMLExtracted, LinksExtracted, NewsNodeExtracted, StartRun}
import core.LinkDownloaderMessages.DownloadLink
import core.LinkExtractorMessages.ExtractLinks
import core.NewsExtractorMessages.ExtractArticle
import org.jsoup.Jsoup

class CrawlerCoreActorTest extends BaseTest {
  class MockedLinkDownloader extends Actor {
    def receive = {
      case DownloadLink(link) =>
        link match {
          case l if l == startUrl => sender ! HTMLExtracted(htmlSample, startUrl)
          case l if l == link1 => sender ! HTMLExtracted(htmlSample, link1)
          case l if l == link2 => sender ! HTMLExtracted(htmlSample, link2)
          case _ => None
        }
    }
  }

  class MockedNewsExtractor extends Actor {
    def receive = {
      case ExtractArticle(html, link) =>
        link match {
          case l if l == startUrl => sender ! NewsNodeExtracted(nodeResult, html, link)
          case l if l == link1 => sender ! NewsNodeExtracted(nodeResult1, html, link)
          case l if l == link2 => sender ! NewsNodeExtracted(nodeResult2, html, link)
        }
    }
  }

  class MockedLinkExtractor extends Actor {
    def receive = {
      case ExtractLinks(domainsToAllow, origin, html) => sender ! LinksExtracted(List(link1, link2))
    }
  }

  private val startUrl = "http://g1.globo.com"
  private val link1 = "link1"
  private val link2 = "link2"
  private val nodeResult = Jsoup.parse(htmlSample).select("div.b").first()
  private val nodeResult1 = Jsoup.parse(htmlSample).select("div.b1").first()
  private val nodeResult2 = Jsoup.parse(htmlSample).select("div.b2").first()
  private val linkDownloaderMock = TestActorRef(Props(new MockedLinkDownloader))
  private val linkExtractorMock = TestActorRef(Props(new MockedLinkExtractor))
  private val newsExtractorMock = TestActorRef(Props(new MockedNewsExtractor))

  it should "extract the node that represents the news in the page <code>startUtl</code>" in {
    val crawlerCore = TestActorRef(Props(new CrawlerCoreActor(linkExtractorMock, linkDownloaderMock, newsExtractorMock)))
    val probe = TestProbe()

    probe.send(crawlerCore, StartRun(startUrl, 0))
    probe.expectMsg(List(nodeResult))
  }

  it should "extract the nodes in the page <code>startUrl</code> and in the pages for the links inside first page" in {
    val crawlerCore = TestActorRef(Props(new CrawlerCoreActor(linkExtractorMock, linkDownloaderMock, newsExtractorMock)))
    val probe = TestProbe()

    probe.send(crawlerCore, StartRun(startUrl, 1))
    probe.expectMsg(List(nodeResult, nodeResult1, nodeResult2))
  }
}
