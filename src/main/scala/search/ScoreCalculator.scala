package search

import org.jsoup.nodes.{Element, Node}

trait ScoreCalculator[A <: Node] {
  def calcScore(node: A): Double
}

trait BasicScoreCalculator extends ScoreCalculator[Element] {
  import scala.collection.JavaConversions._

  override def calcScore(node: Element): Double = {
    textLength(node) + (0.5 * childrenScore(node))
  }

  def textLength(node: Element): Int = {
    node.ownText.trim.length
  }

  def childrenScore(node: Element): Double = {
    node.children
      .map(calcScore)
      .sum
  }
}
