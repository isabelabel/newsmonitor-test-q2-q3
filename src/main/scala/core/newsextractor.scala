package core

import akka.actor.{ActorLogging, Actor}
import core.CrawlerCoreMessages.NewsNodeExtracted
import core.NewsExtractorMessages.ExtractArticle
import org.jsoup.nodes.{Element, Node}
import search.{BasicNewsBodyFinder, NewsBodyFinder}

object NewsExtractorMessages {
  case class ExtractArticle(html: String, link: String)
}

trait NewsExtractorActor[A <: Node] extends Actor with ActorLogging {
  this: NewsBodyFinder[A] =>

  def receive: Receive = {
    case ExtractArticle(html, link) =>
      val node = articleNode(html)
      if (node.isDefined) {
        sender ! NewsNodeExtracted(node.get, html, link)
      } else {
        log.error(s"It could not find the new for the link [$link]")
      }
  }
}

class DefaultNewsExtractorActor extends NewsExtractorActor[Element] with BasicNewsBodyFinder
