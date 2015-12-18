package core

import akka.actor.Actor
import core.CrawlerCoreMessages.LinksExtracted
import core.LinkExtractorMessages.ExtractLinks
import org.jsoup.Jsoup

trait LinkExtractor {
  def extract(domainsToAllow: List[String], origin: String, html: String): List[String]
}

trait BasicLinkExtractor extends LinkExtractor {
  import scala.collection.JavaConversions._

  override def extract(domainsToAllow: List[String], origin: String, html: String): List[String] = {
    val regex = domainsToAllow
      .map(d => s"($d)")
      .mkString("|")

    val cssQuery = s"a[href~=$regex]"

    Jsoup.parse(html)
      .select(cssQuery)
      .map(_.absUrl("href"))
      .filter(_ != origin)
      .toList
      .distinct
  }
}

object LinkExtractorMessages {
  case class ExtractLinks(domainsToAllow: List[String]=List.empty, origin: String, html: String)
}

trait LinkExtractorActor extends Actor {
  this: LinkExtractor =>

  def receive: Receive = {
    case ExtractLinks(domainsToAllow, origin, html) => sender ! LinksExtracted(extract(domainsToAllow, origin, html))
  }
}


class DefaultLinkExtractorActor extends LinkExtractorActor with BasicLinkExtractor