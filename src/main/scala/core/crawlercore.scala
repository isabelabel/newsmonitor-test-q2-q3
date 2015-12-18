package core

import akka.actor.{Actor, ActorLogging, ActorRef}
import core.CrawlerCoreMessages.{StartRun, LinksExtracted, NewsNodeExtracted, HTMLExtracted}
import core.LinkDownloaderMessages.DownloadLink
import core.LinkExtractorMessages.ExtractLinks
import core.NewsExtractorMessages.ExtractArticle
import org.jsoup.nodes.Node

import scala.collection.mutable.ListBuffer
import scala.concurrent.Future

object CrawlerCoreMessages {
  case class StartRun(startURL: String, levels: Int)
  case class HTMLExtracted(html: String, link: String)
  case class NewsNodeExtracted[A <: Node](node: A, html: String, link: String)
  case class LinksExtracted(links: List[String])
}

class CrawlerCoreActor(linkExtractor: ActorRef, linkDownloader: ActorRef, newsExtractor: ActorRef)
  extends Actor with ActorLogging {
  import akka.pattern.pipe
  import context.dispatcher

  private var level: Int = _
  private var controlLevel = 1 // control the links to download in each level
  private val newsNodes = new ListBuffer[Node]
  private var originSender: ActorRef = _

  def receive: Receive = {
    case StartRun(startURL, levels) => start(sender, startURL, levels)
    case HTMLExtracted(html, link) => nodeExtraction(html, link)
    case NewsNodeExtracted(node, html, link) => linksExtraction(node, html, link)
    case LinksExtracted(links) => linksDownload(links)
    case _ => log.error("Errorr")
  }

  def start(sender: ActorRef, url: String, l: Int): Unit = {
    log.info(s"Starting to crawl, downloading the link [$url]")
    originSender = sender
    level = l

    linkDownloader ! DownloadLink(url)
  }

  def nodeExtraction(html: String, link: String): Unit = {
    log.info(s"[L($level)] HTML of link [$link] received, extracting the news...")
    newsExtractor ! ExtractArticle(html, link)
  }

  def linksExtraction(node: Node, html: String, link: String): Unit = {
    log.info(s"[L($level)] News for the link [$link] received")
    newsNodes += node
    controlLevel -= 1

    if (controlLevel == 0 && level > 0) {
      log.info(s"[L($level)] Extracting the links for [$link]")
      linkExtractor ! ExtractLinks(origin = node.baseUri(), html = node.outerHtml())
      level -= level // finish one level of extraction
    } else if (controlLevel == 0 && level == 0) {
      log.info(s"Finishing execution, returning [${newsNodes.size}] news...")
      Future.successful(newsNodes.toList.distinct) pipeTo originSender
    }
  }

  def linksDownload(links: List[String]): Unit = {
    controlLevel += links.size
    log.debug(s"[L($level)] Received a list of links [$links]")

    links.foreach{ link =>
      log.info(s"[L($level)] Downloading the links [$link]")
      linkDownloader ! DownloadLink(link)
    }
  }
}
