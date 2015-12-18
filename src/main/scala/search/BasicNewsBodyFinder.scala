package search

import org.jsoup.Jsoup
import org.jsoup.nodes.{Element, Node}

import scala.collection.JavaConversions._

object NewsBodyFinderApp {
  def main (args: Array[String]) {
    if (args.isEmpty) {
      sys.error("You should pass the HTML as argument!")
    }
    println(DefaultNewsBodyFinder$.articleNodeHTML(args(0)))
  }
}

trait NewsBodyFinder[A <: Node] extends ScoreCalculator[A] {
  def articleNodesScores(htmlTxt: String): List[(Double, A)]

  def articleNode(htmlTxt: String): Option[A] = {
    val result = moreLikely(articleNodesScores(htmlTxt))
    result.map(_._2)
  }

  def articleNodeHTML(htmlTxt: String): String = {
    val result = articleNode(htmlTxt)

    if (result.isDefined) {
      result.get.outerHtml
    } else {
      "It could not find a node that could represent one article."
    }
  }

  private def moreLikely(results: List[(Double, A)]): Option[(Double, A)] = {
    results
      .filter(_._1 > 0.0)
      .sortBy(_._1)
      .reverse
      .headOption
  }
}

trait BasicNewsBodyFinder extends NewsBodyFinder[Element] with BasicScoreCalculator {

  override def articleNodesScores(htmlTxt: String): List[(Double, Element)] = {
    val mainNodes = Jsoup.parse(htmlTxt).body().childNodes().toList
    calcScoreElements(elements(mainNodes))
  }

  private def calcScoreElements(nodes: List[Element]): List[(Double, Element)] = {
    nodes match {
      case Nil => Nil
      case x :: xs => (calcScore(x), x) :: calcScoreElements(xs)
    }
  }

  private def elements(nodes: List[Node]): List[Element] = {
    val allNodes = nodes match {
      case Nil => Nil
      case x :: xs => x :: elements(x.childNodes.toList) ::: elements(xs)
    }

    allNodes
      .filter(_.isInstanceOf[Element])
      .map(_.asInstanceOf[Element])
  }
}

object DefaultNewsBodyFinder$ extends BasicNewsBodyFinder
