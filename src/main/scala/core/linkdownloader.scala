package core

import java.util.concurrent.TimeUnit

import akka.actor.{Actor, ActorLogging}
import akka.util.Timeout
import core.CrawlerCoreMessages.HTMLExtracted
import core.LinkDownloaderMessages.DownloadLink
import spray.client.pipelining._
import spray.http._

import scala.concurrent.Future
import scala.util.{Failure, Success}

object LinkDownloaderMessages {
  case class DownloadLink(link: String)
}

class LinkDownloaderActor extends Actor with ActorLogging {
  import context.dispatcher

  implicit val timeout = Timeout(10, TimeUnit.MINUTES)
  val pipeline: HttpRequest => Future[HttpResponse] = sendReceive

  def receive: Receive = {
    case DownloadLink(link) =>
      val origSender = sender

      val responseFuture = pipeline(Get(link))
      responseFuture.onComplete {
        case Success(result) => onSuccess(result)
        case Failure(error) => onFailure(error)
      }

      def onSuccess(result: HttpResponse): Unit = {
        result.status match {
          case s if s.isSuccess =>
            log.info(s"The download of [$link] was complete successfully!")
            origSender ! HTMLExtracted(result.entity.data.asString, link)
          case _ =>
            log.error(s"It could not download the link [$link] properly. Status code [${result.status}]")
        }
      }

      def onFailure(error: Throwable): Unit = {
        log.error(error, s"Something went wrong trying to download the link.")
      }
  }
}
