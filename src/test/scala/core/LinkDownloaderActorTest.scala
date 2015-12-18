package core

import java.io.IOException

import akka.actor.Props
import akka.testkit.{TestActorRef, TestProbe}
import core.CrawlerCoreMessages.HTMLExtracted
import core.LinkDownloaderMessages.DownloadLink
import org.mockito.Mockito._
import spray.http.HttpRequest

import scala.concurrent.Promise


class LinkDownloaderActorTest extends BaseTest {

  it should "request the link and send a message back with the response data and the passed link" in {
    val mockSend = (req:HttpRequest) => Promise.successful(mockResponse).future
    when(mockStatus.isSuccess).thenReturn(true)

    val probe = TestProbe()
    val linkDownloader = TestActorRef(Props(new LinkDownloaderActor {
      override val pipeline = mockSend
    }))

    probe.send(linkDownloader, DownloadLink("http://g1.globo.com"))
    probe.expectMsg(HTMLExtracted(htmlSample, "http://g1.globo.com"))
  }

  it should "request the link, receive a bad response with status error and then send None back" in {
    val mockSend = (req:HttpRequest) => Promise.successful(mockResponse).future
    when(mockStatus.isSuccess).thenReturn(false)

    val probe = TestProbe()
    val linkDownloader = TestActorRef(Props(new LinkDownloaderActor {
      override val pipeline = mockSend
    }))

    probe.send(linkDownloader, DownloadLink("http://g1.globo.com"))
    probe.expectNoMsg()
  }

  it should "request the link, failure in the download and then send None back" in {
    val mockSend = (req:HttpRequest) => Promise.failed(new IOException).future

    val probe = TestProbe()
    val linkDownloader = TestActorRef(Props(new LinkDownloaderActor {
      override val pipeline = mockSend
    }))

    probe.send(linkDownloader, DownloadLink("http://g1.globo.com"))
    probe.expectNoMsg()
  }
}
