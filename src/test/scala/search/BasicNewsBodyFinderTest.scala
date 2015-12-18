package search

import org.jsoup.Jsoup
import org.scalatest.{FlatSpec, MustMatchers}
import util.ResourceReader


class BasicNewsBodyFinderTest extends FlatSpec with MustMatchers with BasicNewsBodyFinder {
  private val htmlSample = ResourceReader.read("/news1_tests.html")

  it should "calc the scores of the all the 5 nodes" in {
    val doc = Jsoup.parse(htmlSample)
    val nodeA = doc.select("div.a").first
    val nodeB = doc.select("div.b").first
    val nodeB1 = doc.select("div.b-1").first
    val nodeB11 = doc.select("div.b-1-1").first
    val nodeB12 = doc.select("div.b-1-2").first

    val results = articleNodesScores(htmlSample)

    results must have size 5
    results must contain theSameElementsAs Vector((5.0, nodeA), (16.5, nodeB), (15.0, nodeB1), (10.0, nodeB11), (10.0, nodeB12))
  }

  it should "give the Node of the most likely article node that is 'div.b'" in {
    val nodeB = Jsoup.parse(htmlSample).select("div.b").first

    articleNode(htmlSample) must be (Some(nodeB))
  }

  it should "give the HTML of the most likely article node that is 'div.b'" in {
    val nodeB = Jsoup.parse(htmlSample).select("div.b").first

    articleNodeHTML(htmlSample) must be (nodeB.outerHtml())
  }
}
