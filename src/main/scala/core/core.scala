package core

import java.util.concurrent.TimeUnit

import akka.actor.{ActorSystem, Props}
import akka.routing.RandomPool
import akka.util.Timeout
import core.CrawlerCoreMessages.StartRun
import org.jsoup.nodes.Node

trait Core {

  implicit def system: ActorSystem

}

trait BootedCore extends Core {

  implicit lazy val system = ActorSystem("news-crawler", defaultConf)

  sys.addShutdownHook(system.shutdown())
}

trait CoreActors {
  this: Core =>

  val numCores = Runtime.getRuntime.availableProcessors()
  val articleExtractor = system.actorOf(Props[DefaultNewsExtractorActor], name = "article-extractor")
  val linkExtractor = system.actorOf(Props[DefaultLinkExtractorActor].withRouter(RandomPool(numCores)), name = "link-extractor")
  val linkDownloader = system.actorOf(Props(new LinkDownloaderActor()).withRouter(RandomPool(numCores)), name = "link-downloader")
  val crawlerCore = system.actorOf(Props(new CrawlerCoreActor(linkExtractor, linkDownloader, articleExtractor)), name = "crawler-core")
}

object CrawlerApp extends BootedCore with CoreActors {
  import akka.pattern.ask
  import system.dispatcher
  implicit val timeout = Timeout(10, TimeUnit.MINUTES)

  def process(startUrl: String, level: Int): Unit = {
    val futureResult = (crawlerCore ? StartRun(startUrl, level)).mapTo[List[Node]]
    futureResult.map { result =>
      println("Results: ")
      result.zipWithIndex.foreach { r =>
        println(s"[${r._2}] => ${r._1}\n\n")
      }
      system.shutdown()
    }
  }

  def main (args: Array[String]) {
    if (args.length != 2) {
      sys.error("The number of arguments is wrong. Please you have to pass two arguments: <start_url> <levels>")
    }

    val levels = if (args.length == 2) args(1).toInt else 0
    process(args(0), levels)
  }
}
