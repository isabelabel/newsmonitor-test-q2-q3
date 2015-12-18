package core

import java.util.concurrent.TimeUnit

import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestKit}
import akka.util.Timeout
import org.mockito.Mockito._
import org.scalatest.mock.MockitoSugar
import org.scalatest.{BeforeAndAfterAll, FlatSpecLike, MustMatchers}
import spray.http.{HttpEntity, HttpResponse, StatusCode}
import util.ResourceReader

import scala.concurrent.duration._

class BaseTest(_system: ActorSystem)
  extends TestKit(_system)
  with ImplicitSender
  with MustMatchers
  with FlatSpecLike
  with MockitoSugar
  with BeforeAndAfterAll {

  def this() = this(ActorSystem("TestActor"))

  override def afterAll: Unit = {
    system.shutdown()
    system.awaitTermination(10.seconds)
  }

  val htmlSample = ResourceReader.read("/news_with_links_tests.html")
  implicit val timeout = Timeout(15, TimeUnit.SECONDS)

  val mockStatus = mock[StatusCode]
  val mockResponse = mock[HttpResponse]
  when(mockResponse.status).thenReturn(mockStatus)

  val body = HttpEntity(htmlSample.getBytes())
  when(mockResponse.entity).thenReturn(body)
}
